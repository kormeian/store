package com.arffy.server.domian.cart.service

import com.arffy.server.domian.cart.entity.Cart
import com.arffy.server.domian.cart.exception.CartErrorCode
import com.arffy.server.domian.cart.repository.CartRepository
import com.arffy.server.domian.cart.repository.CartRepositoryCustomImpl
import com.arffy.server.domian.product.Product
import com.arffy.server.domian.product.ProductCategory
import com.arffy.server.domian.user.entity.AuthProvider
import com.arffy.server.domian.user.entity.Role
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.exception.RestApiException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CartServiceImplTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val cartRepository = mockk<CartRepository>(relaxed = true)
    val cartRepositoryCustom = mockk<CartRepositoryCustomImpl>(relaxed = true)
    val cartService = CartServiceImpl(cartRepository, cartRepositoryCustom)
    val user = User(
        name = "name",
        email = "email",
        role = Role.ROLE_USER,
        authProvider = AuthProvider.KAKAO,
        oauth2Id = "oauth2Id",
        phoneNumber = "010-1234-5678"
    )
    val product = Product(
        productName = "productName",
        price = 18000,
        discountPrice = 1000,
        discountRate = 10,
        period = "2012",
        country = "country",
        width = "11.1",
        depth = "11.2",
        height = "11.3",
        minLineHeight = "11.4",
        maxLineHeight = "11.5",
        material = "material",
        status = "status",
        description = "description",
        quantity = 1,
        category = ProductCategory.PENDANT,
        thumbnail = "url",
        thumbnailVersion = 0
    )
    val cart = Cart(
        user = user,
        product = product
    )

    beforeContainer {
        user.id = 1L
        product.id = 1L
        cart.id = 1L
    }

    Given("유저의 장바구니 조회") {
        every { cartRepositoryCustom.findAllByUserIdOrderByProductQuantityDescCreatedAtDesc(any()) } returns listOf(cart)
        When("userId가 null이 아님") {
            val userId = user.id!!
            Then("정상적으로 반환") {
                val cartListResponse = cartService.findCartListResponseByUserId(userId)
                cartListResponse.cartList.size shouldBe 1
                cartListResponse.cartList[0].cartId shouldBe cart.id
                cartListResponse.cartList[0].productId shouldBe product.id
            }
        }
    }
    Given("유저의 장바구니 하나 삭제") {
        When("cartId가 null이 아님") {
            every { cartRepository.findByIdAndUserId(any(), any()) } returns cart
            Then("정상적으로 삭제") {
                val cartId = cart.id!!
                val userId = user.id!!
                cartService.deleteByIdAndUserId(cartId, userId)
                verify(exactly = 1) { cartRepository.findByIdAndUserId(cartId, userId) }
                verify(exactly = 1) { cartRepository.delete(cart) }
            }
        }
        When("cartId가 null임") {
            Then("CartErrorCode.REQUIRED_CART_ID 에러 발생") {
                val cartId = null
                val userId = user.id!!
                val exception = shouldThrow<RestApiException> {
                    cartService.deleteByIdAndUserId(cartId, userId)
                }
                exception.baseErrorCode shouldBe CartErrorCode.REQUIRED_CART_ID
            }
        }
        When("장바구니가 존재하지 않음") {
            every { cartRepository.findByIdAndUserId(any(), any()) } returns null
            Then("CartErrorCode.NOT_FOUND_CART 에러 발생") {
                val cartId = cart.id!!
                val userId = user.id!!
                val exception = shouldThrow<RestApiException> {
                    cartService.deleteByIdAndUserId(cartId, userId)
                }
                exception.baseErrorCode shouldBe CartErrorCode.NOT_FOUND_CART
            }
        }
    }
    Given("유저의 장바구니 리스트 삭제") {
        When("cartIdList가 비어있지 않음") {
            every { cartRepository.findByIdInAndUserId(any(), any()) } returns listOf(cart)
            Then("정상적으로 삭제") {
                val cartIdList = listOf(cart.id!!)
                val userId = user.id!!
                cartService.deleteAllByIdInAndUserId(cartIdList, userId)
                verify(exactly = 1) { cartRepository.findByIdInAndUserId(cartIdList, userId) }
                verify(exactly = 1) { cartRepository.deleteAll(listOf(cart)) }
            }
        }
        When("cartIdList가 비어있음") {
            every { cartRepository.findByIdInAndUserId(any(), any()) } returns emptyList()
            Then("정상적으로 삭제") {
                val cartIdList = listOf<Long>()
                val userId = user.id!!
                cartService.deleteAllByIdInAndUserId(cartIdList, userId)
                verify(exactly = 1) { cartRepository.findByIdInAndUserId(cartIdList, userId) }
                verify(exactly = 1) { cartRepository.deleteAll(emptyList()) }
            }
        }
    }
    Given("유저의 장바구니 전체 삭제") {
        When("userId가 null이 아님") {
            Then("정상적으로 삭제") {
                val userId = user.id!!
                cartService.deleteAllByUserId(userId)
                verify(exactly = 1) { cartRepository.deleteAllByUserId(userId) }
            }
        }
    }
    Given("유저의 장바구니 상품 삭제") {
        When("productIds가 비어있지 않음") {
            Then("정상적으로 삭제") {
                val productIds = listOf(product.id!!)
                val userId = user.id!!
                cartService.deleteAllByProductIdInAndUserId(productIds, userId)
                verify(exactly = 1) { cartRepository.deleteAllByProductIdInAndUserId(productIds, userId) }
            }
        }
        When("productIds가 비어있음") {
            Then("정상적으로 삭제") {
                val productIds = emptyList<Long>()
                val userId = user.id!!
                cartService.deleteAllByProductIdInAndUserId(productIds, userId)
                verify(exactly = 1) { cartRepository.deleteAllByProductIdInAndUserId(productIds, userId) }
            }
        }
    }
    Given("유저의 장바구니에 해당 상품이 있는지 확인") {
        When("productId, userId가 null이 아님") {
            val productId = product.id!!
            val userId = user.id!!
            Then("장바구니의 상품 확인") {
                every { cartRepository.existsByProductIdAndUserId(any(), any()) } returns true
                val result = cartService.existsByProductIdAndUserId(productId, userId)
                result shouldBe true
            }
            Then("상품이 확인되지 않음") {
                every { cartRepository.existsByProductIdAndUserId(any(), any()) } returns false
                val result = cartService.existsByProductIdAndUserId(productId, userId)
                result shouldBe false
            }
        }
    }
    Given("장바구니 추가") {
        When("cart가 null이 아님") {
            Then("정상적으로 추가") {
                every { cartRepository.save(any()) } returns cart
                val result = cartService.save(cart)
                verify(exactly = 1) { cartRepository.save(cart) }
                result shouldBe cart
            }
        }
    }
})
