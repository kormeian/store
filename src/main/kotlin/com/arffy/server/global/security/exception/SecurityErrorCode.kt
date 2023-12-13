package com.arffy.server.global.security.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus

enum class SecurityErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다.", 10901),
    INVALID_AUTH_PROVIDER(HttpStatus.NON_AUTHORITATIVE_INFORMATION, "유효하지 않은 인증 제공자입니다.", 10902),
    OAUTH2_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "OAuth2로부터 이메일을 받아오지 못했습니다.", 10903),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.", 10904),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다.", 10905),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다.", 10906),
    LOGOUT_TOKEN(HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다.", 10907),
    REQUIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 필요합니다.", 10908),
    ;

    override val errorReason: ErrorReason
        get() = ErrorReason(
            httpStatus = httpStatus,
            message = message,
            messageCode = messageCode
        )
    override val explainError: String
        get() = this.message
}