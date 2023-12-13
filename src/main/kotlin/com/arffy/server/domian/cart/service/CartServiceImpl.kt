package com.arffy.server.domian.cart.service

import com.arffy.server.domian.cart.dto.CartListResponse
import com.arffy.server.domian.cart.entity.Cart
import com.arffy.server.domian.cart.exception.CartErrorCode
import com.arffy.server.domian.cart.repository.CartRepository
import com.arffy.server.domian.cart.repository.CartRepositoryCustomImpl
import com.arffy.server.global.exception.RestApiException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class CartServiceImpl(
    private val cartRepository: CartRepository,
    private val cartRepositoryCustom: CartRepositoryCustomImpl,
) : CartService {

    @Transactional(readOnly = true)
    override fun findCartListResponseByUserId(
        userId: Long
    ): CartListResponse {
        log.info { "CartServiceImpl.findCartListResponseByUserId" }
        log.info { "userId = $userId" }
        val cartList = cartRepositoryCustom.findAllByUserIdOrderByProductQuantityDescCreatedAtDesc(
            userId
        )
        return CartListResponse.of(cartList)
    }

    @Transactional
    override fun deleteByIdAndUserId(
        cartId: Long?,
        userId: Long
    ) {
        log.info { "CartServiceImpl.deleteByIdAndUserId" }
        log.info { "cartId = $cartId, userId = $userId" }
        if (cartId == null) throw RestApiException(CartErrorCode.REQUIRED_CART_ID)
        val cart = cartRepository.findByIdAndUserId(
            cartId,
            userId
        ) ?: throw RestApiException(CartErrorCode.NOT_FOUND_CART)
        cartRepository.delete(cart)
    }

    @Transactional
    override fun deleteAllByIdInAndUserId(
        cartIdList: List<Long>,
        userId: Long
    ) {
        log.info { "CartServiceImpl.deleteAllByIdInAndUserId" }
        log.info { "cartIdList = $cartIdList, userId = $userId" }
        val cartList = cartRepository.findByIdInAndUserId(
            cartIdList,
            userId
        )
        cartRepository.deleteAll(cartList)
    }

    @Transactional
    override fun deleteAllByUserId(
        userId: Long
    ) {
        log.info { "CartServiceImpl.deleteAllByUserId" }
        log.info { "userId = $userId" }
        cartRepository.deleteAllByUserId(
            userId
        )
    }

    @Transactional
    override fun deleteAllByProductIdInAndUserId(
        productIds: List<Long>,
        userId: Long
    ) {
        log.info { "CartServiceImpl.deleteAllByProductIdInAndUserId" }
        log.info { "productIds = $productIds, userId = $userId" }
        cartRepository.deleteAllByProductIdInAndUserId(productIds, userId)
    }

    @Transactional
    override fun existsByProductIdAndUserId(
        productId: Long,
        userId: Long
    ): Boolean {
        log.info { "CartServiceImpl.existsByProductIdAndUserId" }
        log.info { "productId = $productId, userId = $userId" }
        return cartRepository.existsByProductIdAndUserId(productId, userId)
    }

    @Transactional
    override fun save(
        cart: Cart
    ): Cart {
        val saveCart = cartRepository.save(cart)
        log.info { "CartServiceImpl.save" }
        log.info { "cartId = ${saveCart.id}" }
        return saveCart
    }
}