package com.arffy.server.domian.order.repository

import com.arffy.server.domian.order.entity.OrdersDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrdersDetailRepository : JpaRepository<OrdersDetail, Long> {
    fun findAllByOrdersId(
        ordersId: Long
    ): List<OrdersDetail>
}