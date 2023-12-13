package com.arffy.server.domian.delivery.service

import com.arffy.server.domian.delivery.dto.DeliveryTrackerDto
import mu.KotlinLogging
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class DeliveryTrackingService {
    val restTemplate = RestTemplateBuilder().build()

    fun tracking(carrierCode: String, trackingNumber: String): DeliveryTrackerDto? {
        log.info { "DeliveryTrackingService.tracking" }
        log.info { "carrierCode = $carrierCode, trackingNumber = $trackingNumber" }
        return try {
            restTemplate.getForObject(
                "https://apis.tracker.delivery/carriers/${carrierCode}/tracks/${trackingNumber}",
                DeliveryTrackerDto::class.java
            )
        } catch (e: Exception) {
            null
        }
    }
}