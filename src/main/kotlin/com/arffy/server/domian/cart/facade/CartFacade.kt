package com.arffy.server.domian.cart.facade

import com.arffy.server.domian.cart.dto.CartCreateResponse
import com.arffy.server.domian.cart.entity.Cart
import com.arffy.server.domian.cart.exception.CartErrorCode
import com.arffy.server.domian.cart.service.CartServiceImpl
import com.arffy.server.domian.product.service.ProductService
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.exception.RestApiException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class CartFacade(
    private val cartService: CartServiceImpl,
    private val productService: ProductService
) {
    @Transactional
    fun addCartByProductIdAndUser(
        productId: Long?,
        user: User
    ): CartCreateResponse {
        log.info { "CartFacade.addCartByProductIdAndUser" }
        log.info { "productId = $productId, userId = ${user.id}" }
        if (productId == null) throw RestApiException(CartErrorCode.REQUIRED_PRODUCT_ID)
        val product = productService.findById(productId)
        if (product.quantity <= 0) {
            throw RestApiException(CartErrorCode.PRODUCT_QUANTITY_NOT_ENOUGH)
        }
        if (cartService.existsByProductIdAndUserId(
                productId,
                user.id!!
            )
        ) {
            throw RestApiException(CartErrorCode.ALREADY_EXIST_CART)
        }
        val cart = cartService.save(
            Cart(
                product = product,
                user = user,
            )
        )
        return CartCreateResponse.of(cart)
    }
}