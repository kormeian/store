package com.arffy.server.domian.order.entity

import com.arffy.server.domian.BaseEntity
import com.arffy.server.domian.user.entity.User
import javax.persistence.*

@Entity
class Orders(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val merchantUid: String,

    var impUid: String? = null,
    @Column(nullable = false)
    val originTotalPrice: Int,
    @Column(nullable = false)
    val totalDiscountPrice: Int,
    @Column(nullable = false)
    val totalProductOrderCount: Int,

    var deliveryAddress: String? = null,
    var deliveryAddressDetail: String? = null,
    var deliveryPostCode: String? = null,
    var receiverName: String? = null,
    var receiverPhoneNumber: String? = null,
    var deliveryRequestContent: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus,

    @Column(nullable = false)
    val cancelAvailableAmount: Int,
) : BaseEntity()

enum class OrderStatus {
    PARTIAL, REFUND, // 결제 취소 시
    READY,  // 주문 요청
    PROGRESS, // 결제 요청
    PAID, CANCELLED, FAILED // 결제 응답
}