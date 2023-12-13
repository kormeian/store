package com.arffy.server.domian.notice.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus

enum class NoticeErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    NOT_FOUND_NOTICE(HttpStatus.NOT_FOUND, "해당 공지사항을 찾을 수 없습니다.", 10501),
    REQUIRED_NOTICE_ID(HttpStatus.BAD_REQUEST, "공지사항 아이디는 필수입니다.", 10502)
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