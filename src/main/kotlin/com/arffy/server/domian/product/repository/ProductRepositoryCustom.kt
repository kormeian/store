package com.arffy.server.domian.product.repository

import com.arffy.server.domian.product.dto.ProductResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductRepositoryCustom {
    fun findAllProduct(
        productName: String?,
        category: String,
        pageable: Pageable
    ): Page<ProductResponse>
}