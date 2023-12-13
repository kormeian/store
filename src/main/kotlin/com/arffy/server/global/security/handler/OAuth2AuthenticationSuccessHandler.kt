package com.arffy.server.global.security.handler

import com.arffy.server.domian.user.entity.RefreshToken
import com.arffy.server.domian.user.reposiroty.RefreshTokenRepository
import com.arffy.server.global.security.dto.UserResponseDto
import com.arffy.server.global.security.lib.CookieUtils
import com.arffy.server.global.security.lib.JwtTokenProvider
import com.arffy.server.global.security.repository.CookieAuthorizationRequestRepository
import mu.KotlinLogging
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val cookieAuthorizationRequestRepository: CookieAuthorizationRequestRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) : SimpleUrlAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {

        val targetUrl = determineTargetUrl(
            request, response,
            authentication
        )
        if (response.isCommitted) {
            log.debug("Response has already been committed.")
            return
        }
        clearAuthenticationAttributes(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }

    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ): String {
        val redirectUri: Optional<String> = CookieUtils.getCookie(
            request,
            cookieAuthorizationRequestRepository.redirectUriParamCookieName
        ).map { obj: Cookie -> obj.value }
//        if (redirectUri.isPresent) {
//            throw RestApiException(SecurityErrorCode.INVALID_REDIRECT_URI)
//        }
        val targetUrl = redirectUri.orElse(defaultTargetUrl)

        val tokenInfo: UserResponseDto.TokenInfo = jwtTokenProvider.generateToken(authentication)
        val tokenEntity = refreshTokenRepository.findByEmail(authentication.name)
            ?: refreshTokenRepository.save(
                RefreshToken(
                    tokenInfo.refreshToken,
                    tokenInfo.accessToken,
                    authentication.name,
                )
            )
        tokenEntity.token = tokenInfo.refreshToken
        tokenEntity.accessToken = tokenInfo.accessToken
        refreshTokenRepository.save(tokenEntity)
        return UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("token", tokenInfo.accessToken)
            .build().toUriString()
    }

    protected fun clearAuthenticationAttributes(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        super.clearAuthenticationAttributes(request)
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
    }

}