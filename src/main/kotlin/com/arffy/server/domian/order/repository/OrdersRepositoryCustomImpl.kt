package com.arffy.server.domian.order.repository

import com.arffy.server.domian.order.entity.OrderStatus
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.order.entity.QOrders.orders
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class OrdersRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : OrdersRepositoryCustom {
    override fun findAllCompleteOrdersByUserId(
        userId: Long,
        pageable: Pageable
    ): Page<Orders> {
        val query = jpaQueryFactory.selectFrom(orders)
        val count: JPQLQuery<Orders> = query.where(
            orders.user.id.eq(userId)
                .and(orders.orderStatus.`in`(OrderStatus.PAID, OrderStatus.REFUND, OrderStatus.PARTIAL))
        )
        val orders = query.orderBy(orders.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
        return PageImpl(orders, pageable, count.fetchCount())
    }
}