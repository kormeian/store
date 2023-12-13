package com.arffy.server.domian.delivery.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus

enum class DeliveryErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    NOT_FOUND_DELIVERY(HttpStatus.NOT_FOUND, "해당 배송을 찾을 수 없습니다.", 10701),
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