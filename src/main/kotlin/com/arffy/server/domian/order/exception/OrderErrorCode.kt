package com.arffy.server.domian.order.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus


enum class OrderErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    REQUIRED_PRODUCT_ID(HttpStatus.BAD_REQUEST, "상품 ID가 필요합니다.", 10601),
    NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.", 10602),
    UNSUPPORTED_DELIVERY_CARRIER(HttpStatus.BAD_REQUEST, "지원하지 않는 배송사입니다.", 10603),
    NOT_MATCH_ORDER_STATUS(HttpStatus.BAD_REQUEST, "주문 상태가 결제 성공, 부분 환불, 전체 환불일 경우에만 조회가 가능합니다.", 10604),
    NOT_FOUND_ORDER_DETAIL(HttpStatus.NOT_FOUND, "주문 상세를 찾을 수 없습니다.", 10605),
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "권한이 없습니다.", 10606),
    NOT_MATCH_USER(HttpStatus.BAD_REQUEST, "주문자가 일치하지 않습니다.", 10607),
    REQUIRED_RECEIVER_INFO(HttpStatus.BAD_REQUEST, "수취인 정보가 필요합니다.", 10608),
    NOT_ENOUGH_PRODUCT(HttpStatus.BAD_REQUEST, "상품의 재고가 부족합니다.", 10609),
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.", 10610),
    REQUIRED_ORDER_INFO(HttpStatus.BAD_REQUEST, "주문 정보는 필수입니다.", 10611),
    REQUIRED_MERCHANT_UID(HttpStatus.BAD_REQUEST, "주문 번호는 필수입니다.", 10612),
    REQUIRED_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액은 필수입니다.", 10613),
    REQUIRED_DELIVERY_ADDRESS(HttpStatus.BAD_REQUEST, "배송지 주소는 필수입니다.", 10614),
    REQUIRED_DELIVERY_POST_CODE(HttpStatus.BAD_REQUEST, "배송지 우편번호는 필수입니다.", 10616),
    REQUIRED_RECEIVER_NAME(HttpStatus.BAD_REQUEST, "수취인 이름은 필수입니다.", 10617),
    REQUIRED_RECEIVER_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "수취인 전화번호는 필수입니다.", 10618),
    REQUIRED_PREPARE_REQUEST(HttpStatus.BAD_REQUEST, "결제 준비 요청 정보는 필수입니다.", 10619),
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