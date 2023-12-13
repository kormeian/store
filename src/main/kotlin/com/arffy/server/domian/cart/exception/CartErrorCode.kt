package com.arffy.server.domian.cart.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus

enum class CartErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    PRODUCT_QUANTITY_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "상품의 재고가 없습니다.", 10401),
    NOT_FOUND_CART(HttpStatus.NOT_FOUND, "존재하지 않는 장바구니입니다.", 10403),
    ALREADY_EXIST_CART(HttpStatus.BAD_REQUEST, "이미 장바구니에 존재하는 상품입니다.", 10404),
    REQUIRED_PRODUCT_ID(HttpStatus.BAD_REQUEST, "상품 아이디는 필수입니다.", 10405),
    REQUIRED_CART_ID(HttpStatus.BAD_REQUEST, "장바구니 아이디는 필수입니다.", 10406),
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