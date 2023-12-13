package com.arffy.server.domian.delivery.repository

import com.arffy.server.domian.delivery.Delivery
import com.arffy.server.domian.delivery.DeliveryStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeliveryRepository : JpaRepository<Delivery, Long> {
    fun findByOrdersDetailId(
        ordersDetailId: Long
    ): Delivery?

    fun findByTrackingNumber(
        trackingNumber: String
    ): Delivery?

    fun findAllByDeliveryStatus(
        deliveryStatus: DeliveryStatus
    ): List<Delivery>

}