package com.arffy.server.domian.product.repository

import com.arffy.server.domian.product.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findAllByIdIn(
        idList: List<Long>
    ): List<Product>

}