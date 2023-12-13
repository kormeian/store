package com.arffy.server.domian.user.facade

import com.arffy.server.domian.user.entity.RefreshToken
import com.arffy.server.domian.user.entity.Role
import com.arffy.server.domian.user.exception.UserErrorCode
import com.arffy.server.domian.user.service.RefreshTokenServiceImpl
import com.arffy.server.domian.user.service.UserServiceImpl
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.global.security.dto.UserResponseDto
import com.arffy.server.global.security.exception.SecurityErrorCode
import com.arffy.server.global.security.lib.JwtTokenProvider
import mu.KotlinLogging
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class RefreshTokenFacade(
    private val refreshTokenService: RefreshTokenServiceImpl,
    private val userService: UserServiceImpl,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @Transactional
    fun refreshAccessToken(
        accessToken: String
    ): String {
        log.info { "RefreshTokenFacade.refreshAccessToken" }
        log.info { "accessToken = $accessToken" }
        val info = getEmailAndAuthoritiesFromToken(accessToken)
        val email = info.first
        val authorities = info.second
        val refreshTokenEntity = refreshTokenService.findByEmail(email)
            ?: throw RestApiException(UserErrorCode.REFRESH_TOKEN_NOT_FOUND)

        val user = userService.findByEmail(email)
        val tokenInfo: UserResponseDto.TokenInfo = if (user.hasInfo()) {
            jwtTokenProvider.generateToken(email, authorities, true)
        } else {
            jwtTokenProvider.generateToken(email, authorities, false)
        }
        if (!validateToken(refreshTokenEntity.token)) {
            refreshTokenService.deleteById(
                refreshTokenEntity.id!!
            )
            throw RestApiException(UserErrorCode.REFRESH_TOKEN_EXPIRED)
        }
        refreshTokenEntity.accessToken = tokenInfo.accessToken
        refreshTokenService.save(
            refreshTokenEntity
        )

        return tokenInfo.accessToken
    }

    @Transactional
    fun testAccountLogin(): String {
        log.info { "RefreshTokenFacade.testAccountLogin" }
        userService.findByEmail("test@test.com")
        val authentication: Collection<GrantedAuthority> = listOf(GrantedAuthority { Role.ROLE_USER.name })
        val token = jwtTokenProvider.generateToken("test@test.com", authentication, true)
        val tokenEntity = refreshTokenService.findByEmail("test@test.com")
            ?: refreshTokenService.save(
                RefreshToken(
                    token.refreshToken,
                    token.accessToken,
                    "test@test.com",
                )
            )

        return tokenEntity.accessToken
    }

    fun getEmailAndAuthoritiesFromToken(
        accessToken: String
    ): Pair<String, Collection<GrantedAuthority>> {
        val email: String?
        val authorities: Collection<GrantedAuthority>?
        try {
            email = jwtTokenProvider.getEmailFromToken(accessToken)
            authorities = jwtTokenProvider.getAuthoritiesFromToken(accessToken)
        } catch (e: RestApiException) {
            if (e.baseErrorCode == SecurityErrorCode.INVALID_TOKEN) {
                throw RestApiException(UserErrorCode.INVALID_TOKEN)
            }
            if (e.baseErrorCode == SecurityErrorCode.UNSUPPORTED_TOKEN) {
                throw RestApiException(UserErrorCode.UNSUPPORTED_TOKEN)
            }
            if (e.baseErrorCode == SecurityErrorCode.EMPTY_TOKEN) {
                throw RestApiException(UserErrorCode.EMPTY_TOKEN)
            }
            throw RestApiException(UserErrorCode.INVALID_TOKEN)
        }
        return Pair(email, authorities)
    }

    private fun validateToken(refreshToken: String): Boolean {
        try {
            jwtTokenProvider.validateToken(refreshToken)
        } catch (e: RestApiException) {
            if (e.baseErrorCode == SecurityErrorCode.ACCESS_TOKEN_EXPIRED) {
                return false
            }
        }
        return true
    }

}