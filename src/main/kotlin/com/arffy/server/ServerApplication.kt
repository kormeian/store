package com.arffy.server

import com.querydsl.jpa.impl.JPAQueryFactory
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@EnableJpaAuditing
@EnableScheduling
@OpenAPIDefinition(servers = [Server(url = "http://localhost:8080"), Server(url = "https://api.arffy.store")])
@Configuration
class ArffyApplicationConfiguration

@SpringBootApplication
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}

@Configuration
class QueryDslConfig {

    @PersistenceContext
    lateinit var em: EntityManager

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(em)
    }
}