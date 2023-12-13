package com.arffy.server.global.security.filter

import com.arffy.server.domian.user.reposiroty.RefreshTokenRepository
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.global.security.exception.SecurityErrorCode
import com.arffy.server.global.security.lib.JwtTokenProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
) : GenericFilterBean() {

    override fun doFilter(
        servletRequest: ServletRequest,
        servletResponse: ServletResponse,
        filterChain: FilterChain
    ) {

        val token = jwtTokenProvider.resolveToken(servletRequest as HttpServletRequest)

        if (!token.isNullOrBlank() && jwtTokenProvider.validateToken(token)) {
            refreshTokenRepository.findByAccessToken(token)
                ?: throw RestApiException(SecurityErrorCode.LOGOUT_TOKEN)
            val authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }
        if (token == null && !servletRequest.requestURL.contains("/qna/product/")) throw RestApiException(
            SecurityErrorCode.REQUIRED_TOKEN
        )
        filterChain.doFilter(servletRequest, servletResponse)
    }
}