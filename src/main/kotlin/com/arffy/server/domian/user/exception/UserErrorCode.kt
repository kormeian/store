package com.arffy.server.domian.user.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다.", 10101),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리프레시 토큰입니다.", 10102),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.", 10103),
    REQUIRED_USER_NAME(HttpStatus.BAD_REQUEST, "사용자 이름은 필수입니다.", 10104),
    REQUIRED_USER_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "사용자 전화번호는 필수입니다.", 10105),
    REQUIRED_USER_ADDRESS(HttpStatus.BAD_REQUEST, "사용자 주소는 필수입니다.", 10106),
    REQUIRED_USER_POST_CODE(HttpStatus.BAD_REQUEST, "사용자 우편번호는 필수입니다.", 10108),
    REQUIRED_USER_MODIFY_REQUEST(HttpStatus.BAD_REQUEST, "사용자 수정 요청 정보는 필수입니다.", 10109),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.", 10110),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다.", 10111),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다.", 10112),
    UNLINK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 연동 해제에 실패했습니다.", 10113),
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

