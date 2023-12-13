package com.arffy.server.domian.order.service

import com.arffy.server.domian.order.dto.PrepareRequest
import com.arffy.server.domian.order.dto.PrepareResponse
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.product.Product
import com.arffy.server.domian.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderService {
    fun saveByProductsAndUser(
        products: List<Product>,
        user: User
    ): Orders

    fun saveByUserAndProductsAndMerchantUid(
        user: User,
        products: List<Product>,
        merchantUid: String,
    ): Orders

    fun updateByPrepareRequest(
        order: Orders,
        prepareRequest: PrepareRequest
    ): PrepareResponse

    fun findByMerchantUid(
        merchantUid: String
    ): Orders

    fun save(
        order: Orders
    ): Orders

    fun findAllByUserIdAndPageable(
        userId: Long,
        pageable: Pageable
    ): Page<Orders>

    fun findByOrderId(
        ordersId: Long
    ): Orders

    fun existsByUserId(
        userId: Long
    ): Boolean
}