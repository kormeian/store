package com.arffy.server.domian.payment.facade

import com.arffy.server.domian.cart.service.CartServiceImpl
import com.arffy.server.domian.order.entity.OrderStatus
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.order.service.OrderDetailServiceImpl
import com.arffy.server.domian.order.service.OrderServiceImpl
import com.arffy.server.domian.payment.dto.ConfirmProcessResponse
import com.arffy.server.domian.payment.dto.ConfirmRequest
import com.arffy.server.domian.payment.dto.VerifyRequest
import com.arffy.server.domian.payment.entity.Callback
import com.arffy.server.domian.payment.entity.Payments
import com.arffy.server.domian.payment.exception.PaymentErrorCode
import com.arffy.server.domian.payment.service.PaymentServiceImpl
import com.arffy.server.domian.product.Product
import com.arffy.server.domian.product.service.ProductServiceImpl
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.infra.portOne.service.PortOneServiceImpl
import com.siot.IamportRestClient.response.Payment
import mu.KotlinLogging
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val log = KotlinLogging.logger {}

@Component
class PaymentFacade(
    val orderService: OrderServiceImpl,
    val orderDetailService: OrderDetailServiceImpl,
    val productService: ProductServiceImpl,
    val cartService: CartServiceImpl,
    val portOneService: PortOneServiceImpl,
    val paymentService: PaymentServiceImpl,
) {
    val restTemplate: RestTemplate = RestTemplateBuilder().build()

    enum class ConfirmFailReason(
        val reason: String
    ) {
        NOT_FOUND_ORDER("주문 내역이 없습니다."),
        NOT_PROGRESS_ORDER("사전 등록된 상품이 아닙니다."),
        NOT_MATCH_AMOUNT("결제 금액이 일치하지 않습니다."),
        NOT_FOUND_ORDER_DETAIL("주문 상세가 없습니다."),
        NOT_ENOUGH_STOCK("재고가 부족합니다.");

        companion object {
            fun from(text: String): ConfirmFailReason? {
                return values().firstOrNull { it.reason == text }
            }
        }
    }

    @Transactional
    fun confirmProcess(
        confirmRequest: ConfirmRequest,
    ): ConfirmProcessResponse {
        log.info { "PaymentFacade.confirmProcess" }
        log.info { "merchantUid = ${confirmRequest.merchant_uid}" }
        val order: Orders
        try {
            order = orderService.findByMerchantUid(confirmRequest.merchant_uid)
        } catch (e: RestApiException) {
            return ConfirmProcessResponse(
                reason = ConfirmFailReason.NOT_FOUND_ORDER.reason
            )
        }

        if (order.orderStatus != OrderStatus.PROGRESS) {
            return ConfirmProcessResponse(
                reason = ConfirmFailReason.NOT_PROGRESS_ORDER.reason
            )
        }
        if (order.totalDiscountPrice != confirmRequest.amount) {
            return ConfirmProcessResponse(
                reason = ConfirmFailReason.NOT_MATCH_AMOUNT.reason
            )
        }
        val orderDetails = orderDetailService.findAllByOrderId(order.id!!)
        if (orderDetails.isEmpty()) {
            return ConfirmProcessResponse(
                reason = ConfirmFailReason.NOT_FOUND_ORDER_DETAIL.reason
            )
        }
        orderDetails.forEach {
            if (it.product.quantity < 1) {
                return ConfirmProcessResponse(
                    reason = ConfirmFailReason.NOT_ENOUGH_STOCK.reason
                )
            }
        }
        orderDetails.forEach {
            it.product.quantity = 0
            it.product.soldOutAt = LocalDateTime.now()
            productService.save(it.product)
        }
        return ConfirmProcessResponse()
    }

    @Transactional
    fun verifyPayment(
        verifyRequest: VerifyRequest
    ) {
        log.info { "PaymentFacade.verifyPayment" }
        log.info { "CallBack from = ${Callback.CLIENT.name}, merchantUid = ${verifyRequest.merchant_uid}" }
        val order = orderService.findByMerchantUid(verifyRequest.merchant_uid!!)
        val portOnePayment: Payment?
        var products: List<Product>? = null
        try {
            portOnePayment = portOneService.getPayment(verifyRequest.imp_uid!!)
        } catch (e: Exception) {
            throw e
        }
        try {
            when (portOnePayment.status) {
                "paid" -> {
                    order.orderStatus = OrderStatus.PAID
                }

                "failed" -> {
                    order.orderStatus = OrderStatus.FAILED
                    if (ConfirmFailReason.from(portOnePayment.failReason) == ConfirmFailReason.NOT_ENOUGH_STOCK &&
                        !paymentService.existsByOrderIdAndStatus(
                            order.id!!,
                            "paid"
                        )
                    ) {
                        products = orderDetailService.findAllByOrderId(
                            order.id!!
                        ).map { it.product }
                        throw RestApiException(PaymentErrorCode.FAILED_PAYMENT)
                    }
                }

                else -> {
                    throw RestApiException(PaymentErrorCode.NOT_MATCH_STATUS)
                }
            }
        } catch (e: Exception) {
            log.error { "PaymentFacade.verifyPayment Error : ${e.message}" }
        } finally {
            order.impUid = verifyRequest.imp_uid
            orderService.save(order)
            paymentService.save(
                Payments.from(portOnePayment, order, Callback.CLIENT)
            )
            products?.forEach {
                it.quantity = 1
                it.soldOutAt = null
                productService.save(it)
            }
        }
    }

    fun verifyPaymentWebHook(
        verifyRequest: VerifyRequest,
    ) {
        log.info { "PaymentFacade.verifyPaymentWebHook" }
        log.info { "CallBack from = ${Callback.PORT_ONE.name}, merchantUid = ${verifyRequest.merchant_uid}" }
        val (order, user) = orderService.findOrderAndUserByMerchantUid(verifyRequest.merchant_uid!!)
        val portOnePayment: Payment?
        var products: List<Product>? = null
        val payments: Payments
        try {
            portOnePayment = portOneService.getPayment(verifyRequest.imp_uid!!)
        } catch (e: Exception) {
            throw e
        }
        try {
            log.info { "portOnePaymentStatus = ${portOnePayment.status}" }
            when (portOnePayment.status) {
                "paid" -> {
                    order.orderStatus = OrderStatus.PAID
                    products = orderDetailService.findAllProductsByOrderId(
                        order.id!!
                    )
                }

                else -> {
                    throw RestApiException(PaymentErrorCode.NOT_MATCH_STATUS)
                }
            }
        } catch (e: Exception) {
            log.error { "PaymentFacade.verifyPaymentWebHook Error : ${e.message}" }
        } finally {
            order.impUid = verifyRequest.imp_uid
            orderService.save(order)
            payments = paymentService.save(
                Payments.from(portOnePayment, order, Callback.PORT_ONE)
            )
            if (!products.isNullOrEmpty()) {
                cartService.deleteAllByProductIdInAndUserId(
                    products.map { it.id!! },
                    user.id!!
                )
            }
        }
        if (order.orderStatus == OrderStatus.PAID
            && !products.isNullOrEmpty()
            && payments.status == "paid"
        ) {
            sendMail(order, user, payments, products)
        }
    }

    @Async("getAsyncExecutor")
    fun sendMail(orders: Orders, user: User, payments: Payments, products: List<Product>) {
        log.info { "PaymentFacade.sendMail" }
        log.info { "orderId = ${orders.id}" }
        val mailRequestDto = MailRequestDto.from(orders, user, payments, products)
        try {
            restTemplate.postForEntity(
                "http://3.39.97.190:777/mail/PAYMENTS",
                mailRequestDto,
                Void::class.java
            )
        } catch (e: Exception) {
            log.error { "PaymentFacade.sendMail Error : ${e.message}" }
        }

    }

    private class MailRequestDto(
        val mailType: String,
        val name: String,
        val email: String,
        val phoneNumber: String,
        val merchantUid: String,
        val paidAt: String,
        val payMethod: String,
        val pgProvider: String,
        val originTotalPrice: Int,
        val totalDiscountPrice: Int,
        val discountedPrice: Int,
        val deliveryAddress: String,
        val deliveryAddressDetail: String,
        val deliveryPostCode: String,
        val receiverName: String,
        val receiverPhoneNumber: String,
        val deliveryRequestContent: String?,
        val ordersDetails: List<OrdersDetailDto>,
    ) {
        class OrdersDetailDto(
            val productName: String,
            val thumbnail: String,
            val thumbnailVersion: Int,
            val price: Int,
            val discountPrice: Int,
        )

        companion object {
            fun from(orders: Orders, user: User, payments: Payments, products: List<Product>): MailRequestDto {
                return MailRequestDto(
                    mailType = "PAYMENTS",
                    name = orders.receiverName!!,
                    email = user.email,
                    phoneNumber = orders.receiverPhoneNumber!!,
                    merchantUid = orders.merchantUid,
                    paidAt = payments.paidAt!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    payMethod = payments.payMethod!!.text,
                    pgProvider = payments.pgProvider!!.text,
                    originTotalPrice = orders.originTotalPrice,
                    totalDiscountPrice = orders.totalDiscountPrice,
                    discountedPrice = orders.originTotalPrice - orders.totalDiscountPrice,
                    deliveryAddress = orders.deliveryAddress!!,
                    deliveryAddressDetail = orders.deliveryAddressDetail!!,
                    deliveryPostCode = orders.deliveryPostCode!!,
                    receiverName = orders.receiverName!!,
                    receiverPhoneNumber = orders.receiverPhoneNumber!!,
                    deliveryRequestContent = orders.deliveryRequestContent,
                    ordersDetails = products.map {
                        OrdersDetailDto(
                            productName = it.productName,
                            thumbnail = it.thumbnail!! + "?",
                            thumbnailVersion = it.thumbnailVersion,
                            price = it.price,
                            discountPrice = it.discountPrice,
                        )
                    }
                )
            }
        }
    }
}