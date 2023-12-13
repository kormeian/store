package com.arffy.server.infra.portOne.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus

enum class PortOneErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "포트원 인증에 실패하였습니다.", 11001),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "포트원 서버 내부 에러", 11002),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", 11003),
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