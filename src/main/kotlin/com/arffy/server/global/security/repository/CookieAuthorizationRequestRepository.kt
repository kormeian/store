package com.arffy.server.global.security.repository

import com.arffy.server.global.security.lib.CookieUtils
import com.nimbusds.oauth2.sdk.util.StringUtils
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CookieAuthorizationRequestRepository : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private val oauth2AuthorizationRequestCookieName = "oauth2_auth_request"
    val redirectUriParamCookieName = "redirect_uri"
    private val cookieExpireSeconds = 180
    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest {
        return CookieUtils.getCookie(
            request,
            oauth2AuthorizationRequestCookieName
        )
            .map { cookie ->
                CookieUtils.deserialize(
                    cookie,
                    OAuth2AuthorizationRequest::class.java
                )
            }
            .orElse(null)
    }

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(
                request,
                response,
                oauth2AuthorizationRequestCookieName
            )
            CookieUtils.deleteCookie(
                request,
                response,
                redirectUriParamCookieName
            )
            return
        }
        CookieUtils.addCookie(
            response,
            oauth2AuthorizationRequestCookieName,
            CookieUtils.serialize(authorizationRequest),
            cookieExpireSeconds
        )
        val redirectUriAfterLogin: String? = request.getParameter(redirectUriParamCookieName)
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            CookieUtils.addCookie(
                response, redirectUriParamCookieName,
                redirectUriAfterLogin, cookieExpireSeconds
            )
        }
    }

    override fun removeAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest {
        return loadAuthorizationRequest(request)
    }

    fun removeAuthorizationRequestCookies(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        CookieUtils.deleteCookie(
            request, response,
            oauth2AuthorizationRequestCookieName
        )
        CookieUtils.deleteCookie(
            request, response,
            redirectUriParamCookieName
        )
    }
}