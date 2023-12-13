package com.arffy.server.domian.cart.repository

import com.arffy.server.domian.cart.entity.Cart
import com.arffy.server.domian.cart.entity.QCart.cart
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CartRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : CartRepositoryCustom {
    override fun findAllByUserIdOrderByProductQuantityDescCreatedAtDesc(userId: Long): List<Cart> {
        return jpaQueryFactory.selectFrom(cart)
            .where(cart.user.id.eq(userId))
            .where(cart.product.deleteYn.eq(false))
            .orderBy(cart.product.quantity.desc())
            .orderBy(cart.createdAt.desc())
            .fetch()
    }
}