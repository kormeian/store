package com.arffy.server.domian.order.repository

import com.arffy.server.domian.delivery.QDelivery.delivery
import com.arffy.server.domian.order.entity.QOrdersDetail.ordersDetail
import com.arffy.server.domian.product.QProduct.product
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class OrdersDetailRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : OrdersDetailRepositoryCustom {


    override fun findAllByOrdersIds(
        ordersIds: List<Long?>
    ): List<OrdersDetailQueryDto> {
        return jpaQueryFactory.select(
            QOrdersDetailQueryDto(
                ordersDetail.orders.id,
                ordersDetail.id,
                ordersDetail.requestCancelledAt,
                ordersDetail.cancelReason,
                ordersDetail.cancelReasonContent,
                ordersDetail.refundStatus,
                product.id,
                product.productName,
                ordersDetail.originPrice,
                ordersDetail.discountPrice,
                ordersDetail.discountRate,
                Expressions.stringTemplate(
                    "CONCAT({0}, {1}, {2})",
                    product.thumbnail, "?version=", product.thumbnailVersion
                ),
                delivery.id,
                delivery.deliveryStatus,
                delivery.deliveryCarrier,
                delivery.trackingNumber
            )
        )
            .from(ordersDetail)
            .innerJoin(ordersDetail.product, product)
            .leftJoin(delivery).on(ordersDetail.eq(delivery.ordersDetail))
            .where(ordersDetail.orders.id.`in`(ordersIds))
            .fetch()
    }
}