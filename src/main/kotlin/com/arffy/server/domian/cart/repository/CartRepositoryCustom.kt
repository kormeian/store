package com.arffy.server.domian.cart.repository

import com.arffy.server.domian.cart.entity.Cart

interface CartRepositoryCustom {
    fun findAllByUserIdOrderByProductQuantityDescCreatedAtDesc(
        userId: Long
    ): List<Cart>
}