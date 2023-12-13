package com.arffy.server.domian.payment.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus

enum class PaymentErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다.", 10801),
    NOT_MATCH_STATUS(HttpStatus.BAD_REQUEST, "결제 상태가 일치하지 않습니다.", 10802),
    FAILED_PAYMENT(HttpStatus.BAD_REQUEST, "결제에 실패하였습니다.", 10803),
    REQUIRED_VERIFY_REQUEST(HttpStatus.BAD_REQUEST, "결제 검증 요청 정보는 필수입니다.", 10804),
    REQUIRED_IMP_UID(HttpStatus.BAD_REQUEST, "아임포트 UID는 필수입니다.", 10805),
    REQUIRED_MERCHANT_UID(HttpStatus.BAD_REQUEST, "결제 고유번호는 필수입니다.", 10806),
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