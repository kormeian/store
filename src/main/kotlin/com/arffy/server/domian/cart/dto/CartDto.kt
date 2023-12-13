package com.arffy.server.domian.cart.dto

import com.arffy.server.domian.cart.entity.Cart
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

class CartCreateResponse(
    @field:Schema(description = "장바구니 아이디")
    val cartId: Long,
    @field:Schema(description = "상품 아이디")
    val productId: Long,
) {
    companion object {
        fun of(
            cart: Cart
        ): CartCreateResponse {
            return CartCreateResponse(
                cartId = cart.id!!,
                productId = cart.product.id!!,
            )
        }
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class CartResponse(
    @field:Schema(description = "장바구니 아이디")
    val cartId: Long,

    @field:Schema(description = "상품 아이디")
    val productId: Long,

    @field:Schema(description = "상품 썸네일")
    val thumbnail: String? = null,

    @field:Schema(description = "상품 썸네일 버전")
    val thumbnailVersion: Int? = null,

    @field:Schema(description = "상품 이름")
    val productName: String,

    @field:Schema(description = "상품 가격")
    val price: Int,

    @field:Schema(description = "상품 수량")
    val quantity: Int,

    @field:Schema(description = "상품 할인율")
    val discountRate: Int,

    @field:Schema(description = "상품 할인 가격")
    val discountPrice: Int,
) {
    companion object {
        fun of(
            cart: Cart
        ): CartResponse {
            return CartResponse(
                cartId = cart.id!!,
                productId = cart.product.id!!,
                thumbnail = cart.product.thumbnail,
                thumbnailVersion = cart.product.thumbnailVersion,
                productName = cart.product.productName,
                price = cart.product.price,
                discountRate = cart.product.discountRate,
                discountPrice = cart.product.discountPrice,
                quantity = cart.product.quantity,
            )
        }
    }
}

class CartListResponse(
    @field:Schema(description = "장바구니 목록")
    val cartList: List<CartResponse>,

    @field:Schema(description = "장바구니 총 가격")
    val totalPrice: Int = cartList.sumOf { it.price },

    @field:Schema(description = "장바구니 총 할인 가격")
    val totalDiscountPrice: Int = cartList.sumOf { it.discountPrice }
) {
    companion object {
        fun of(
            cartList: List<Cart>
        ): CartListResponse {
            return CartListResponse(
                cartList = cartList.map { CartResponse.of(it) }
            )
        }
    }
}