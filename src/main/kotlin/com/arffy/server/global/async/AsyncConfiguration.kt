package com.arffy.server.global.async

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@EnableAsync
@Configuration
class AsyncConfiguration {
    @Bean
    fun getAsyncExecutor(): ThreadPoolTaskExecutor {
        val processors = Runtime.getRuntime().availableProcessors()
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = processors
        executor.maxPoolSize = processors * 2
        executor.queueCapacity = 30
        executor.setThreadNamePrefix("async-thread-")
        executor.initialize()
        return executor
    }
}