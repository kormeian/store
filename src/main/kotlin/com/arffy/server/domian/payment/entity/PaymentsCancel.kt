package com.arffy.server.domian.payment.entity

import com.arffy.server.domian.order.entity.OrdersDetail
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class PaymentsCancel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_detail_id", nullable = false)
    val ordersDetail: OrdersDetail,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payments_id", nullable = false)
    val payments: Payments,

    @Column(nullable = false)
    val cancelApiStatus: String,
    @Column(nullable = false)
    val pgTid: String,
    @Column(nullable = false)
    val amount: Int,

    var taxFree: Int? = null,
    var vatAmount: Int? = null,

    @Column(nullable = false)
    val cancelledAt: LocalDateTime,
    @Column(nullable = false)
    val reason: String,

    var receiptUrl: String? = null,
)