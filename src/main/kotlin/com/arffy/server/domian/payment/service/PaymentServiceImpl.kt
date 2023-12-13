package com.arffy.server.domian.payment.service

import com.arffy.server.domian.payment.entity.Payments
import com.arffy.server.domian.payment.exception.PaymentErrorCode
import com.arffy.server.domian.payment.repository.PaymentsRepository
import com.arffy.server.global.exception.RestApiException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class PaymentServiceImpl(
    val paymentRepository: PaymentsRepository,
) : PaymentService {

    @Transactional
    override fun save(
        payment: Payments
    ): Payments {
        val savePayment = paymentRepository.save(payment)
        log.info { "PaymentServiceImpl.save" }
        log.info { "paymentId = ${savePayment.id}" }
        return savePayment
    }

    @Transactional(readOnly = true)
    override fun findByOrderId(
        orderId: Long
    ): Payments {
        log.info { "PaymentServiceImpl.findByOrderId" }
        log.info { "orderId = $orderId" }
        return paymentRepository.findFirstByOrdersIdOrderByCreatedAtDesc(orderId) ?: throw RestApiException(
            PaymentErrorCode.NOT_FOUND_PAYMENT
        )
    }

    @Transactional(readOnly = true)
    override fun existsByOrderIdAndStatus(
        orderId: Long,
        status: String
    ): Boolean {
        log.info { "PaymentServiceImpl.existsByOrderIdAndStatus" }
        log.info { "orderId = $orderId, status = $status" }
        return paymentRepository.existsByOrdersIdAndStatus(orderId, status)
    }
}