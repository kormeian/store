package com.arffy.server.infra.portOne.config

import com.siot.IamportRestClient.IamportClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PortOneConfiguration {
    @Value("\${portone.api-key}")
    private val portOneApiKey: String? = null

    @Value("\${portone.secret-key}")
    private val portOneSecretKey: String? = null

    @Bean
    fun iamportClient(): IamportClient {
        return IamportClient(portOneApiKey, portOneSecretKey)
    }
}