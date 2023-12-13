package com.arffy.server.domian.order.controller

import com.arffy.server.domian.order.dto.*
import com.arffy.server.domian.order.facade.OrderFacade
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.security.CurrentUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "주문 API")
@RestController
@RequestMapping("/api/v1/order")
class OrderController(
    val orderFacade: OrderFacade,
) {
    @PostMapping
    @Operation(summary = "주문 번호 요청", description = "주문 번호 요청", security = [SecurityRequirement(name = "bearerAuth")])
    @Parameter(description = "상품 ID 리스트", required = true, example = "[1, 2, 3]")
    fun getMerchantUid(
        @CurrentUser
        @Parameter(hidden = true)
        user: User,
        @RequestParam
        productIds: List<Long>,
    ): ResponseEntity<OrderInfoResponse> {
        return ResponseEntity.ok(
            orderFacade.getOrderInfoResponseByProductIdInAndUser(
                productIds,
                user
            )
        )
    }

    @PostMapping("/prepare")
    @Operation(
        summary = "주문 번호 사전등록",
        description = "주문 번호 사전등록",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun postPrepare(
        @RequestBody
        prepareRequest: PrepareRequest?,
        @CurrentUser @Parameter(hidden = true)
        user: User,
    ): ResponseEntity<PrepareResponse> {
        return ResponseEntity.ok(
            orderFacade.postPrepareByPrepareRequestAndUser(
                prepareRequest,
                user
            )
        )
    }

    @GetMapping
    @Operation(summary = "주문 리스트 조회", description = "주문 리스트 조회", security = [SecurityRequirement(name = "bearerAuth")])
    fun getOrderList(
        @CurrentUser
        @Parameter(hidden = true)
        user: User,
        pageable: Pageable
    ): ResponseEntity<Page<OrdersResponse>> {
        return ResponseEntity.ok(
            orderFacade.getAllOrdersResponseByUserAndPageable(
                user,
                pageable
            )
        )
    }

    @GetMapping("/{ordersId}")
    @Operation(summary = "주문 상세 조회", description = "주문 상세 조회", security = [SecurityRequirement(name = "bearerAuth")])
    fun getOrderDetail(
        @CurrentUser
        @Parameter(hidden = true)
        user: User,
        @PathVariable("ordersId")
        ordersId: Long,
        @RequestParam
        @Parameter(description = "주문 번호", required = true, example = "ORDyyyyMMdd_000001")
        merchantUid: String,
    ): ResponseEntity<OrdersDetailResponse> {
        return ResponseEntity.ok(
            orderFacade.getOrdersDetailResponseByOrderIdAndMerchantUidAndUserId(
                ordersId,
                merchantUid,
                user.id!!
            )
        )
    }

    @GetMapping("/payment/{ordersId}")
    @Operation(
        summary = "결제 정보 조회",
        description = "결제 전 구매자, 상품, 주문금액 조회",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getOrderPaymentInfo(
        @CurrentUser
        @Parameter(hidden = true)
        user: User,
        @PathVariable("ordersId")
        ordersId: Long,
    ): ResponseEntity<OrdersPaymentInfoResponse> {
        return ResponseEntity.ok(
            orderFacade.getOrdersPaymentInfoResponseByOrdersIdAndUser(
                ordersId,
                user
            )
        )
    }

}
