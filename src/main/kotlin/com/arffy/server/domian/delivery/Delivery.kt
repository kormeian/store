package com.arffy.server.domian.delivery

import com.arffy.server.domian.BaseEntity
import com.arffy.server.domian.order.entity.OrdersDetail
import com.arffy.server.domian.order.exception.OrderErrorCode
import com.arffy.server.global.exception.RestApiException
import java.util.stream.Stream
import javax.persistence.*

@Entity
class Delivery(

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_detail_id", nullable = false)
    val ordersDetail: OrdersDetail,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var deliveryStatus: DeliveryStatus,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val deliveryCarrier: DeliveryCarrier,

    @Column(nullable = false)
    val trackingNumber: String,

    var deliveryProgress: String? = null,
) : BaseEntity()

enum class DeliveryStatus(
    val text: String,
) {
    DELIVERY("배송중"),
    COMPLETE("배송완료"),
}


enum class DeliveryCarrier(
    val text: String,
    val code: String,
) {
    E_POST("우체국", "kr.epost");

    companion object {

        fun of(code: String?): DeliveryCarrier {
            return Stream.of(*values())
                .filter { carrier: DeliveryCarrier -> carrier.name == code }
                .findAny()
                .orElseThrow { RestApiException(OrderErrorCode.UNSUPPORTED_DELIVERY_CARRIER) }
        }
    }
}
