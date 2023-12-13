package com.arffy.server.global.exception

import com.arffy.server.domian.cart.exception.CartErrorCode
import com.arffy.server.domian.notice.exception.NoticeErrorCode
import com.arffy.server.domian.order.exception.OrderErrorCode
import com.arffy.server.domian.payment.exception.PaymentErrorCode
import com.arffy.server.domian.product.exception.ProductErrorCode
import com.arffy.server.domian.qna.exception.QnaErrorCode
import com.arffy.server.domian.user.exception.UserErrorCode
import com.arffy.server.global.security.exception.SecurityErrorCode
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "예외 모음")
@RestController
@RequestMapping("/api/v1/exception")
class ExceptionExamController {

    @GetMapping("/global")
    @Operation(summary = "글로벌 예외", description = "글로벌 예외")
    @ApiErrorCodeExample(GlobalErrorCode::class)
    fun globalException() {
    }

    @GetMapping("/security")
    @Operation(summary = "소셜 로그인, 토큰 관련 예외", description = "보안 예외")
    @ApiErrorCodeExample(SecurityErrorCode::class)
    fun securityException() {
    }

    @GetMapping("/cart")
    @Operation(summary = "장바구니 예외", description = "장바구니 예외")
    @ApiErrorCodeExample(CartErrorCode::class)
    fun cartException() {
    }

    @GetMapping("/notice")
    @Operation(summary = "공지사항 예외", description = "공지사항 예외")
    @ApiErrorCodeExample(NoticeErrorCode::class)
    fun noticeException() {
    }

    @GetMapping("/order")
    @Operation(summary = "주문 예외", description = "주문 예외")
    @ApiErrorCodeExample(OrderErrorCode::class)
    fun orderException() {
    }

    @GetMapping("/payment")
    @Operation(summary = "결제 예외", description = "결제 예외")
    @ApiErrorCodeExample(PaymentErrorCode::class)
    fun paymentException() {
    }

    @GetMapping("/product")
    @Operation(summary = "상품 예외", description = "상품 예외")
    @ApiErrorCodeExample(ProductErrorCode::class)
    fun productException() {
    }

    @GetMapping("/qna")
    @Operation(summary = "Q&A 예외", description = "Q&A 예외")
    @ApiErrorCodeExample(QnaErrorCode::class)
    fun qnaException() {
    }

    @GetMapping("/user")
    @Operation(summary = "유저 예외", description = "유저 예외")
    @ApiErrorCodeExample(UserErrorCode::class)
    fun userException() {
    }
}