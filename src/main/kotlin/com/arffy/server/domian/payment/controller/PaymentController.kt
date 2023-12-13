package com.arffy.server.domian.payment.controller

import com.arffy.server.domian.payment.dto.ConfirmProcessResponse
import com.arffy.server.domian.payment.dto.ConfirmRequest
import com.arffy.server.domian.payment.dto.VerifyRequest
import com.arffy.server.domian.payment.exception.PaymentErrorCode
import com.arffy.server.domian.payment.facade.PaymentFacade
import com.arffy.server.global.exception.RestApiException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@Tag(name = "결제 API", description = "결제 관련 API")
@RestController
@RequestMapping("/api/v1/payment")
class PaymentController(
    val paymentFacade: PaymentFacade,
) {
    @PostMapping("/confirm")
    @Operation(hidden = true)
    fun confirmProcess(
        @RequestBody
        confirmRequest: ConfirmRequest,
    ): ResponseEntity<ConfirmProcessResponse> {
        log.info { "confirmRequest: $confirmRequest" }
        val response = paymentFacade.confirmProcess(confirmRequest)
        if (response.reason != null) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok().build()
    }

    @PostMapping("/verify/client")
    @Operation(
        summary = "결제 검증",
        description = "결제 검증",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun verifyClientPayment(
        @RequestBody
        verifyRequest: VerifyRequest?,
    ): ResponseEntity<Unit> {
        validateVerifyRequest(verifyRequest)
        return ResponseEntity.ok(
            paymentFacade.verifyPayment(
                verifyRequest!!
            )
        )
    }

    private fun validateVerifyRequest(verifyRequest: VerifyRequest?) {
        if (verifyRequest == null) {
            throw RestApiException(PaymentErrorCode.REQUIRED_VERIFY_REQUEST)
        }
        if (verifyRequest.imp_uid.isNullOrBlank()) {
            throw RestApiException(PaymentErrorCode.REQUIRED_IMP_UID)
        }
        if (verifyRequest.merchant_uid.isNullOrBlank()) {
            throw RestApiException(PaymentErrorCode.REQUIRED_MERCHANT_UID)
        }
    }

    @Operation(hidden = true)
    @PostMapping("/verify/webhook")
    fun verifyWebhookPayment(
        @RequestBody
        verifyRequest: VerifyRequest,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(
            paymentFacade.verifyPaymentWebHook(
                verifyRequest
            )
        )
    }


}