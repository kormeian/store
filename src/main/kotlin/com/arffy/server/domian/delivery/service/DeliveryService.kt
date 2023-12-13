package com.arffy.server.domian.delivery.service

import com.arffy.server.domian.delivery.Delivery
import com.arffy.server.domian.delivery.DeliveryStatus

interface DeliveryService {
    fun findByOrdersDetailId(
        ordersDetailId: Long
    ): Delivery

    fun findByTrackingNumber(
        trackingNumber: String
    ): Delivery

    fun save(
        delivery: Delivery
    ): Delivery

    fun findAllByDeliveryStatus(
        deliveryStatus: DeliveryStatus
    ): List<Delivery>

    fun saveAll(
        delivery: List<Delivery>
    ): List<Delivery>
}