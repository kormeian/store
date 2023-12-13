package com.arffy.server.global.security.lib

import com.arffy.server.domian.user.exception.UserErrorCode
import com.arffy.server.domian.user.reposiroty.UserRepository
import com.arffy.server.global.exception.GlobalErrorCode
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.global.security.config.ExpireTime
import com.arffy.server.global.security.dto.UserResponseDto
import com.arffy.server.global.security.exception.SecurityErrorCode
import com.arffy.server.global.security.oauth2.UserPrincipal
import com.arffy.server.global.security.service.UserAdapter
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.security.Key
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

private val log = KotlinLogging.logger {}

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") secretKey: String,
    private val userRepository: UserRepository,
) {
    private val key: Key

    init {
        val keyBytes: ByteArray = Decoders.BASE64.decode(secretKey)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(authentication: Authentication): UserResponseDto.TokenInfo {
        return generateToken(
            authentication.name,
            authentication.authorities,
            (authentication.principal as UserPrincipal).getHasInfo()
        )
    }

    fun generateToken(
        name: String?,
        inputAuthorities: Collection<GrantedAuthority>,
        hasInfo: Boolean,
    ): UserResponseDto.TokenInfo {
        val authorities: String = inputAuthorities.stream()
            .map<String> { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))
        val now = Date()
        val accessToken: String
        if (hasInfo) {
            accessToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .claim("type", TYPE_ACCESS)
                .setIssuedAt(now) //토큰 발행 시간 정보
                .setExpiration(Date(now.time + ExpireTime.ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
        } else {
            accessToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .claim("type", TYPE_ACCESS)
                .claim("hasInfo", false)
                .setIssuedAt(now) //토큰 발행 시간 정보
                .setExpiration(Date(now.time + ExpireTime.ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
        }


        val refreshToken: String = Jwts.builder()
            .claim("type", TYPE_REFRESH)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + ExpireTime.REFRESH_TOKEN_EXPIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return UserResponseDto.TokenInfo(
            grantType = BEARER_TYPE,
            accessToken = accessToken,
            accessTokenExpirationTime = ExpireTime.ACCESS_TOKEN_EXPIRE_TIME,
            refreshToken = refreshToken,
            refreshTokenExpirationTime = ExpireTime.REFRESH_TOKEN_EXPIRE_TIME
        )
    }

    fun getAuthentication(accessToken: String): Authentication {
        //토큰 복호화
        val claims: Claims = parseClaims(accessToken)
        if (claims[AUTHORITIES_KEY] == null) {
            throw RestApiException(GlobalErrorCode.INTERNAL_SERVER_GLOBAL_ERROR)
        }
        val user = userRepository.findByEmail(claims.subject)
            ?: throw RestApiException(UserErrorCode.NOT_FOUND_USER)
        val authorities: Collection<GrantedAuthority> =
            Arrays.stream(claims[AUTHORITIES_KEY].toString().split(",").toTypedArray())
                .map { role: String? ->
                    SimpleGrantedAuthority(role)
                }
                .collect(Collectors.toList())


        return UsernamePasswordAuthenticationToken(
            UserAdapter(user), "",
            authorities
        )
    }

    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            log.info("Invalid JWT Token", e)
            throw RestApiException(SecurityErrorCode.INVALID_TOKEN)
        } catch (e: MalformedJwtException) {
            log.info("Invalid JWT Token", e)
            throw RestApiException(SecurityErrorCode.INVALID_TOKEN)
        } catch (e: ExpiredJwtException) {
            log.info("Expired JWT Token", e)
            throw RestApiException(SecurityErrorCode.ACCESS_TOKEN_EXPIRED)
        } catch (e: UnsupportedJwtException) {
            log.info("Unsupported JWT Token", e)
            throw RestApiException(SecurityErrorCode.UNSUPPORTED_TOKEN)
        } catch (e: IllegalArgumentException) {
            log.info("JWT claims string is empty.", e)
            throw RestApiException(SecurityErrorCode.EMPTY_TOKEN)
        }
    }

    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).body
        } catch (e: ExpiredJwtException) {
            throw RestApiException(SecurityErrorCode.ACCESS_TOKEN_EXPIRED)
        }
    }

    private fun getClaims(token: String): Claims {
        val claims: Claims?
        try {
            claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
        } catch (e: SecurityException) {
            log.info("Invalid JWT Token", e)
            throw RestApiException(SecurityErrorCode.INVALID_TOKEN)
        } catch (e: MalformedJwtException) {
            log.info("Invalid JWT Token", e)
            throw RestApiException(SecurityErrorCode.INVALID_TOKEN)
        } catch (e: ExpiredJwtException) {
            log.info("Expired JWT Token", e)
            return e.claims
        } catch (e: UnsupportedJwtException) {
            log.info("Unsupported JWT Token", e)
            throw RestApiException(SecurityErrorCode.UNSUPPORTED_TOKEN)
        } catch (e: IllegalArgumentException) {
            log.info("JWT claims string is empty.", e)
            throw RestApiException(SecurityErrorCode.EMPTY_TOKEN)
        }
        return claims
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken: String = request.getHeader(AUTHORIZATION_HEADER) ?: return null
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            bearerToken.substring(7)
        } else null
    }

    fun getEmailFromToken(token: String): String {
        val claims: Claims = getClaims(token)
        return claims.subject
    }

    fun getAuthoritiesFromToken(token: String): Collection<GrantedAuthority> {
        val claims: Claims = getClaims(token)
        val authorities: Collection<GrantedAuthority> =
            Arrays.stream(claims[AUTHORITIES_KEY].toString().split(",").toTypedArray())
                .map { role: String? ->
                    SimpleGrantedAuthority(role)
                }
                .collect(Collectors.toList())
        return authorities
    }


    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val AUTHORITIES_KEY = "role"
        private const val BEARER_TYPE = "Bearer"
        private const val TYPE_ACCESS = "access"
        private const val TYPE_REFRESH = "refresh"
    }
}