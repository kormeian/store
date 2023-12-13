package com.arffy.server.global.security.handler

import com.arffy.server.global.security.lib.CookieUtils
import com.arffy.server.global.security.repository.CookieAuthorizationRequestRepository
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OAuth2AuthenticationFailureHandler(
    private val cookieAuthorizationRequestRepository: CookieAuthorizationRequestRepository
) : SimpleUrlAuthenticationFailureHandler() {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authenticationException: AuthenticationException
    ) {
        var targetUrl: String = CookieUtils.getCookie(
            request,
            cookieAuthorizationRequestRepository.redirectUriParamCookieName
        )
            .map { obj: Cookie -> obj.value }
            .orElse("/")
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam(
                "error",
                authenticationException.localizedMessage
            )
            .build().toUriString()
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}