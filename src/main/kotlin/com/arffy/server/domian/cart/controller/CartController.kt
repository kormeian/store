package com.arffy.server.domian.cart.controller

import com.arffy.server.domian.cart.dto.CartCreateResponse
import com.arffy.server.domian.cart.dto.CartListResponse
import com.arffy.server.domian.cart.facade.CartFacade
import com.arffy.server.domian.cart.service.CartServiceImpl
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.security.CurrentUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "장바구니 API")
@RestController
@RequestMapping("/api/v1/cart")
class CartController(
    private val cartService: CartServiceImpl,
    private val cartFacade: CartFacade,
) {

    @Operation(
        summary = "장바구니 담기",
        description = "장바구니 담기",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @PostMapping
    fun addCartByProductIdAndUser(
        @RequestParam("productId")
        productId: Long?,
        @Parameter(hidden = true) @CurrentUser
        user: User
    ): ResponseEntity<CartCreateResponse> {
        return ResponseEntity.ok(
            cartFacade.addCartByProductIdAndUser(
                productId,
                user
            )
        )
    }

    @GetMapping
    @Operation(
        summary = "장바구니 조회",
        description = "장바구니 조회",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getCartByUser(
        @Parameter(hidden = true) @CurrentUser
        user: User
    ): ResponseEntity<CartListResponse> {
        return ResponseEntity.ok(
            cartService.findCartListResponseByUserId(
                user.id!!
            )
        )
    }

    @DeleteMapping("/{cartId}")
    @Operation(
        summary = "장바구니 삭제",
        description = "장바구니 삭제",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteCartByIdAndUser(
        @PathVariable("cartId")
        cartId: Long?,
        @Parameter(hidden = true) @CurrentUser
        user: User
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(
            cartService.deleteByIdAndUserId(
                cartId,
                user.id!!
            )
        )
    }

    @DeleteMapping
    @Operation(
        summary = "장바구니 리스트 삭제",
        description = "장바구니 리스트 삭제",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteAllByIdInAndUserId(
        @RequestParam("cartIdList")
        cartIdList: List<Long>,
        @Parameter(hidden = true) @CurrentUser
        user: User
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(
            cartService.deleteAllByIdInAndUserId(
                cartIdList,
                user.id!!
            )
        )
    }

    @DeleteMapping("/all")
    @Operation(
        summary = "사용지 장바구니 전체 삭제",
        description = "사용자 장바구니 전체 삭제",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteAllByUserId(
        @Parameter(hidden = true) @CurrentUser
        user: User
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(
            cartService.deleteAllByUserId(
                user.id!!
            )
        )
    }
}