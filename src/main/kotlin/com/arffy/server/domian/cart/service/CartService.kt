package com.arffy.server.domian.cart.service

import com.arffy.server.domian.cart.dto.CartListResponse
import com.arffy.server.domian.cart.entity.Cart

interface CartService {
    fun findCartListResponseByUserId(
        userId: Long
    ): CartListResponse

    fun deleteByIdAndUserId(
        cartId: Long?,
        userId: Long
    )

    fun deleteAllByIdInAndUserId(
        cartIdList: List<Long>,
        userId: Long
    )

    fun deleteAllByUserId(
        userId: Long
    )

    fun deleteAllByProductIdInAndUserId(
        productIds: List<Long>,
        userId: Long
    )

    fun existsByProductIdAndUserId(
        productId: Long,
        userId: Long
    ): Boolean

    fun save(
        cart: Cart
    ): Cart
}