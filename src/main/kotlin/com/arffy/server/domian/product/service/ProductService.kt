package com.arffy.server.domian.product.service

import com.arffy.server.domian.product.Product
import com.arffy.server.domian.product.dto.ProductResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductService {
    fun findAllProductResponseByProductNameAndCategoryAndPageable(
        productName: String?,
        category: String?,
        pageable: Pageable
    ): Page<ProductResponse>

    fun findById(
        id: Long
    ): Product

    fun findProductResponseById(
        id: Long?
    ): ProductResponse

    fun findAllByIdIn(
        productIds: List<Long>
    ): List<Product>

    fun save(
        product: Product
    ): Product
}