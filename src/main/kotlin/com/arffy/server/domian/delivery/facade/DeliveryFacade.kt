package com.arffy.server.domian.delivery.facade

import com.arffy.server.domian.delivery.DeliveryStatus
import com.arffy.server.domian.delivery.service.DeliveryServiceImpl
import com.arffy.server.domian.delivery.service.DeliveryTrackingService
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class DeliveryFacade(
    val deliveryService: DeliveryServiceImpl,
    val deliveryTrackingService: DeliveryTrackingService
) {
    @Scheduled(cron = "0 0/30 * * * *")
    fun trackingAndUpdate() {
        log.info("DeliveryFacade.trackingAndUpdate")
        val deliveries = deliveryService.findAllByDeliveryStatus(DeliveryStatus.DELIVERY)
        if (deliveries.isEmpty()) {
            return
        }
        deliveries.forEach {
            val deliveryTrackerDto = deliveryTrackingService.tracking(it.deliveryCarrier.code, it.trackingNumber)
            if (deliveryTrackerDto != null) {
                val progress = deliveryTrackerDto.progresses.last().status.text
                if (it.deliveryProgress == progress) {
                    return@forEach
                }
                it.deliveryProgress = progress
                if (progress == "배송완료") {
                    it.deliveryStatus = DeliveryStatus.COMPLETE
                }
                try {
                    deliveryService.save(it)
                } catch (e: Exception) {
                    return@forEach
                }
            }
        }
    }
}