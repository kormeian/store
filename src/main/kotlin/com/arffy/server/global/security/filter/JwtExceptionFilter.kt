package com.arffy.server.global.security.filter

import com.arffy.server.global.exception.RestApiException
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtExceptionFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (ex: RestApiException) {
            setErrorResponse(response, ex)
        } catch (ex: Exception) {
            setErrorResponse(response, ex)
        }
    }

    private fun setErrorResponse(
        res: HttpServletResponse,
        ex: RestApiException
    ) {
        res.contentType = MediaType.APPLICATION_JSON_VALUE
        res.characterEncoding = "UTF-8"
        res.status = ex.baseErrorCode.errorReason.httpStatus.value()
        val body = mapOf(
            "code" to ex.baseErrorCode.errorReason.messageCode,
            "message" to ex.errorMessage
        )
        res.writer.print(body)
    }

    private fun setErrorResponse(
        res: HttpServletResponse,
        ex: Exception
    ) {
        res.contentType = MediaType.APPLICATION_JSON_VALUE
        res.characterEncoding = "UTF-8"
        res.status = HttpServletResponse.SC_UNAUTHORIZED
        val body = mapOf(
            "code" to "UNAUTHORIZED",
            "message" to ex.message
        )
        res.writer.print(body)
    }
}