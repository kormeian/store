package com.arffy.server.domian.cart.repository

import com.arffy.server.domian.cart.entity.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : JpaRepository<Cart, Long> {
    fun findAllByUserId(
        userId: Long
    ): List<Cart>

    fun findByIdAndUserId(
        cartId: Long,
        userId: Long
    ): Cart?

    fun findByIdInAndUserId(
        cartIdList: List<Long>,
        userId: Long
    ): List<Cart>

    fun existsByProductIdAndUserId(
        productId: Long,
        userId: Long
    ): Boolean

    fun deleteAllByUserId(userId: Long)

    fun deleteAllByProductIdInAndUserId(
        productIdList: List<Long>,
        userId: Long
    ): List<Cart>
}