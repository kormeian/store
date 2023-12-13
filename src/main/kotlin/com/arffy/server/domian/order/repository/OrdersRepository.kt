package com.arffy.server.domian.order.repository

import com.arffy.server.domian.order.entity.Orders
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.persistence.LockModeType

@Repository
interface OrdersRepository : JpaRepository<Orders, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun countByCreatedAtAfter(
        createdAt: LocalDateTime
    ): Long

    fun findByMerchantUid(
        merchantUid: String
    ): Orders?

    fun existsByUserId(
        userId: Long
    ): Boolean

    fun findByImpUid(
        impUid: String
    ): Orders?
}