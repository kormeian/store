package com.arffy.server.domian.order.service

import com.arffy.server.domian.order.entity.OrderStatus
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.order.entity.OrdersDetail
import com.arffy.server.domian.order.repository.OrdersDetailQueryDto
import com.arffy.server.domian.order.repository.OrdersDetailRepository
import com.arffy.server.domian.order.repository.OrdersDetailRepositoryCustomImpl
import com.arffy.server.domian.product.Product
import com.arffy.server.domian.product.ProductCategory
import com.arffy.server.domian.user.entity.AuthProvider
import com.arffy.server.domian.user.entity.Role
import com.arffy.server.domian.user.entity.User
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class OrderDetailServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val orderDetailRepository = mockk<OrdersDetailRepository>(relaxed = true)
    val orderDetailRepositoryCustom = mockk<OrdersDetailRepositoryCustomImpl>(relaxed = true)
    val orderDetailService = OrderDetailServiceImpl(orderDetailRepository, orderDetailRepositoryCustom)
    val user = User(
        name = "name",
        email = "email",
        phoneNumber = "010-1234-5678",
        role = Role.ROLE_USER,
        authProvider = AuthProvider.KAKAO,
        oauth2Id = "oauth2Id"
    )
    val order = Orders(
        user = user,
        merchantUid = "merchantUid",
        originTotalPrice = 10000,
        totalDiscountPrice = 9000,
        totalProductOrderCount = 1,
        orderStatus = OrderStatus.READY,
        cancelAvailableAmount = 9000,
    )
    val product = Product(
        productName = "productName",
        price = 10000,
        discountPrice = 9000,
        discountRate = 10,
        period = "2023",
        country = "country",
        width = "1",
        depth = "2",
        height = "3",
        minLineHeight = "4",
        maxLineHeight = "5",
        material = "material",
        status = "status",
        description = "description",
        quantity = 1,
        category = ProductCategory.ETC,
        thumbnail = "thumbnail",
        thumbnailVersion = 0,
        deleteYn = false,
    )
    val orderDetail = OrdersDetail(
        orders = order,
        product = product,
        productOrderCount = 1,
        originPrice = 10000,
        discountPrice = 9000,
        discountRate = 10,
    )

    beforeContainer {
        user.id = 1L
        order.id = 1L
        product.id = 1L
        orderDetail.id = 1L
    }

    given("주문과 상품리스트로 주문상세 저장 요청") {
        When("order와 product List가 정상적으로 주어졌을때") {
            val productList = listOf(product)
            every { orderDetailRepository.save(any()) } returns orderDetail
            Then("정상적으로 저장") {
                val orderDetailList = orderDetailService.saveAllByOrderAndProductList(order, productList)
                orderDetailList.size shouldBe productList.size
                orderDetailList.forEachIndexed { index, orderDetail ->
                    orderDetail.product.id shouldBe productList[index].id
                }
            }
        }
        When("product List가 비어있을 때") {
            val productList = emptyList<Product>()
            Then("빈 리스트 반환") {
                val orderDetailList = orderDetailService.saveAllByOrderAndProductList(order, productList)
                orderDetailList.size shouldBe 0
            }
        }
    }

    Given("주문으로 모든 주문 상세 조회") {
        When("orderId가 정상적으로 주어졌을 때") {
            val orderDetailList = listOf(orderDetail)
            every { orderDetailRepository.findAllByOrdersId(any()) } returns orderDetailList
            Then("정상적으로 반환") {
                val result = orderDetailService.findAllByOrderId(order.id!!)
                result.size shouldBe orderDetailList.size
                result.forEachIndexed { index, orderDetail ->
                    orderDetail.id shouldBe orderDetailList[index].id
                }
            }
        }
    }

    Given("주문으로 모든 주문 상세의 상품 조회") {
        When("orderId가 정상적으로 주어졌을 때") {
            val orderDetailList = listOf(orderDetail)
            every { orderDetailRepository.findAllByOrdersId(any()) } returns orderDetailList
            Then("정상적으로 반환") {
                val result = orderDetailService.findAllProductsByOrderId(order.id!!)
                result.size shouldBe orderDetailList.size
                result.forEachIndexed { index, product ->
                    product.id shouldBe orderDetailList[index].product.id
                }
            }
        }
        When("orderId가 정상적으로 주어졌지만 주문 상세가 없을 때") {
            every { orderDetailRepository.findAllByOrdersId(any()) } returns emptyList()
            Then("빈 리스트 반환") {
                val result = orderDetailService.findAllProductsByOrderId(order.id!!)
                result.size shouldBe 0
            }
        }
    }

    Given("주문상세에 관련된 dto 요청") {
        When("주문 id가 정상적으로 주어졌을 때") {
            val orderDetailQueryDto = OrdersDetailQueryDto(
                orderDetail.orders.id!!,
                orderDetail.id!!,
                orderDetail.requestCancelledAt,
                orderDetail.cancelReason,
                orderDetail.cancelReasonContent,
                orderDetail.refundStatus,
                product.id!!,
                product.productName,
                orderDetail.originPrice,
                orderDetail.discountPrice,
                orderDetail.discountRate,
                "${product.thumbnail}?version=${product.thumbnailVersion}",
                null,
                null,
                null,
                null
            )
            val orderDetailList = listOf(orderDetailQueryDto)
            every { orderDetailRepositoryCustom.findAllByOrdersIds(any()) } returns listOf(orderDetailQueryDto)
            Then("정상적으로 반환") {
                val result = orderDetailService.findAllByOrderIdCustom(listOf(order.id!!))
                result.size shouldBe orderDetailList.size
                result.forEachIndexed { index, orderProductDto ->
                    orderProductDto.ordersDetailId shouldBe orderDetailList[index].ordersDetailId
                }
            }
        }
        When("주문 id가 정상적으로 주어졌지만 주문 상세가 없을 때") {
            every { orderDetailRepositoryCustom.findAllByOrdersIds(any()) } returns emptyList()
            Then("빈 리스트 반환") {
                val result = orderDetailService.findAllByOrderIdCustom(listOf(order.id!!))
                result.size shouldBe 0
            }
        }
    }

})
