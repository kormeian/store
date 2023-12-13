package com.arffy.server.domian.payment.service

import com.arffy.server.domian.payment.entity.Payments

interface PaymentService {
    fun save(
        payment: Payments
    ): Payments

    fun findByOrderId(
        orderId: Long
    ): Payments

    fun existsByOrderIdAndStatus(
        orderId: Long,
        status: String
    ): Boolean
}