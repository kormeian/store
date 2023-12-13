package com.arffy.server.domian.payment.service

import com.arffy.server.domian.order.entity.OrderStatus
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.payment.entity.Payments
import com.arffy.server.domian.payment.exception.PaymentErrorCode
import com.arffy.server.domian.payment.repository.PaymentsRepository
import com.arffy.server.domian.user.entity.AuthProvider
import com.arffy.server.domian.user.entity.Role
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.exception.RestApiException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class PaymentServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val paymentRepository = mockk<PaymentsRepository>(relaxed = true)
    val paymentService = PaymentServiceImpl(paymentRepository)
    val user = User(
        name = "name",
        email = "email",
        phoneNumber = "010-1234-5678",
        role = Role.ROLE_USER,
        authProvider = AuthProvider.KAKAO,
        oauth2Id = "oauth2Id"
    )
    val order = Orders(
        user = user,
        merchantUid = "merchantUid",
        originTotalPrice = 10000,
        totalDiscountPrice = 9000,
        totalProductOrderCount = 1,
        orderStatus = OrderStatus.PROGRESS,
        cancelAvailableAmount = 9000,
    )
    val payment = Payments(
        orders = order,
        amount = order.totalDiscountPrice,
    )

    beforeContainer {
        user.id = 1L
        order.id = 1L
        payment.id = 1L
    }

    Given("결제 정보 저장 요청") {
        When("결제 정보가 정상적으로 주어짐") {
            every { paymentRepository.save(any()) } returns payment
            Then("결제 정보 저장 성공") {
                val result = paymentService.save(payment)
                result shouldBe payment
            }
        }
    }
    Given("오더 아이디로 결제 정보 조회 요청") {
        When("오더 아이디가 정상적으로 주어짐") {
            Then("결제 조회 성공") {
                every { paymentRepository.findFirstByOrdersIdOrderByCreatedAtDesc(any()) } returns payment
                val result = paymentService.findByOrderId(order.id!!)
                result shouldBe payment
            }
            Then("결제 조회 실패 - ${PaymentErrorCode.NOT_FOUND_PAYMENT} 예외 발생") {
                every { paymentRepository.findFirstByOrdersIdOrderByCreatedAtDesc(any()) } returns null
                val exception = shouldThrow<RestApiException> {
                    paymentService.findByOrderId(order.id!!)
                }
                exception.baseErrorCode shouldBe PaymentErrorCode.NOT_FOUND_PAYMENT
            }
        }
    }
    Given("오더 아이디와 결제 상태로 결제 정보 존재 여부 조회 요청") {
        When("오더 아이디와 결제 상태가 정상적으로 주어짐") {
            Then("결제 정보 존재 여부 조회 성공") {
                every { paymentRepository.existsByOrdersIdAndStatus(any(), any()) } returns true
                val result = paymentService.existsByOrderIdAndStatus(order.id!!, "status")
                result shouldBe true
            }
            Then("결제 정보 존재 여부 조회 실패") {
                every { paymentRepository.existsByOrdersIdAndStatus(any(), any()) } returns false
                val result = paymentService.existsByOrderIdAndStatus(order.id!!, "status")
                result shouldBe false
            }
        }
    }
})
