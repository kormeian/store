package com.arffy.server.domian.product.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus

enum class ProductErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다.", 10201),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리가 존재하지 않습니다.", 10202),
    REQUIRED_PRODUCT_ID(HttpStatus.BAD_REQUEST, "상품 아이디는 필수입니다.", 10203),
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