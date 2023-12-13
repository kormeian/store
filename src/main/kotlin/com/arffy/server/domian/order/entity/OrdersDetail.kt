package com.arffy.server.domian.order.entity

import com.arffy.server.domian.product.Product
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class OrdersDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", nullable = false)
    val orders: Orders,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(nullable = false)
    val productOrderCount: Int,
    @Column(nullable = false)
    val originPrice: Int,
    @Column(nullable = false)
    val discountPrice: Int,
    @Column(nullable = false)
    val discountRate: Int,

    var requestCancelledAt: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    var cancelReason: CancelReason? = null,
    var cancelReasonContent: String? = null,

    @Enumerated(EnumType.STRING)
    var refundStatus: RefundStatus? = null,
)

enum class CancelReason {
    PRODUCT_DAMAGE,
    CHANGE_MIND,
    DELAY_DELIVERY,
    ETC,
}

enum class RefundStatus {
    READY, PROGRESS, COMPLETE,
}