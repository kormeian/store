package com.arffy.server.domian.order.repository

import com.arffy.server.domian.delivery.DeliveryCarrier
import com.arffy.server.domian.delivery.DeliveryStatus
import com.arffy.server.domian.order.entity.CancelReason
import com.arffy.server.domian.order.entity.RefundStatus
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

interface OrdersDetailRepositoryCustom {
    fun findAllByOrdersIds(
        ordersIds: List<Long?>
    ): List<OrdersDetailQueryDto>
}


class OrdersDetailQueryDto @QueryProjection constructor(
    val ordersId: Long,
    val ordersDetailId: Long,
    var requestCancelledAt: LocalDateTime? = null,
    var cancelReason: CancelReason? = null,
    var cancelReasonContent: String? = null,
    var refundStatus: RefundStatus? = null,
    val productId: Long,
    val productName: String,
    val price: Int,
    val discountPrice: Int,
    val discountRate: Int,
    val thumbnail: String,
    var deliveryId: Long? = null,
    var deliveryStatus: DeliveryStatus? = null,
    var deliveryCarrier: DeliveryCarrier? = null,
    var waybillNumber: String? = null,
)

