package com.arffy.server.domian.payment.repository

import com.arffy.server.domian.payment.entity.Payments
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PaymentsRepository : JpaRepository<Payments, Long> {
    fun countByCreatedAtAfter(
        createdAt: LocalDateTime
    ): Long

    fun findFirstByOrdersIdOrderByCreatedAtDesc(
        ordersId: Long
    ): Payments?

    fun findByOrdersMerchantUid(
        merchantUid: String
    ): Payments?

    fun existsByOrdersIdAndStatus(
        ordersId: Long,
        status: String
    ): Boolean

    fun findLastByOrdersId(
        ordersId: Long
    ): Payments?

}