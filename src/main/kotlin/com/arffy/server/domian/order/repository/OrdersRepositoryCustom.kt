package com.arffy.server.domian.order.repository

import com.arffy.server.domian.order.entity.Orders
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrdersRepositoryCustom {
    fun findAllCompleteOrdersByUserId(
        userId: Long,
        pageable: Pageable
    ): Page<Orders>
}