package com.arffy.server.domian.delivery.service

import com.arffy.server.domian.delivery.Delivery
import com.arffy.server.domian.delivery.DeliveryStatus
import com.arffy.server.domian.delivery.exception.DeliveryErrorCode
import com.arffy.server.domian.delivery.repository.DeliveryRepository
import com.arffy.server.global.exception.RestApiException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class DeliveryServiceImpl(
    val deliveryRepository: DeliveryRepository,
) : DeliveryService {

    @Transactional(readOnly = true)
    override fun findByOrdersDetailId(
        ordersDetailId: Long
    ): Delivery {
        log.info { "DeliveryServiceImpl.findByOrdersDetailId" }
        log.info { "ordersDetailId = $ordersDetailId" }
        return deliveryRepository.findByOrdersDetailId(ordersDetailId)
            ?: throw RestApiException(DeliveryErrorCode.NOT_FOUND_DELIVERY)
    }

    @Transactional(readOnly = true)
    override fun findByTrackingNumber(
        trackingNumber: String
    ): Delivery {
        log.info { "DeliveryServiceImpl.findByTrackingNumber" }
        log.info { "trackingNumber = $trackingNumber" }
        return deliveryRepository.findByTrackingNumber(trackingNumber)
            ?: throw RestApiException(DeliveryErrorCode.NOT_FOUND_DELIVERY)
    }

    @Transactional
    override fun save(
        delivery: Delivery
    ): Delivery {
        val saveDelivery = deliveryRepository.save(delivery)
        log.info { "DeliveryServiceImpl.save" }
        log.info { "deliveryId = ${saveDelivery.id}" }
        return saveDelivery
    }

    @Transactional(readOnly = true)
    override fun findAllByDeliveryStatus(
        deliveryStatus: DeliveryStatus
    ): List<Delivery> {
        log.info { "DeliveryServiceImpl.findAllByDeliveryStatus" }
        log.info { "deliveryStatus = ${deliveryStatus.text}" }
        return deliveryRepository.findAllByDeliveryStatus(deliveryStatus)
    }

    @Transactional
    override fun saveAll(
        delivery: List<Delivery>
    ): List<Delivery> {
        val saveDeliveryList = deliveryRepository.saveAll(delivery)
        log.info { "DeliveryServiceImpl.saveAll" }
        log.info { "deliveryIds = ${saveDeliveryList.map { it.id }}" }
        return saveDeliveryList
    }
}