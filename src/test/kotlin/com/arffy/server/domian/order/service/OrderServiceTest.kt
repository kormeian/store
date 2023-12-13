package com.arffy.server.domian.order.service

import com.arffy.server.domian.order.dto.PrepareRequest
import com.arffy.server.domian.order.entity.OrderStatus
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.order.entity.OrdersDetail
import com.arffy.server.domian.order.exception.OrderErrorCode
import com.arffy.server.domian.order.repository.OrdersRepository
import com.arffy.server.domian.order.repository.OrdersRepositoryCustomImpl
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
import io.mockk.mockkStatic
import io.mockk.slot
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class OrderServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val ordersRepository = mockk<OrdersRepository>(relaxed = true)
    val ordersRepositoryCustom = mockk<OrdersRepositoryCustomImpl>(relaxed = true)
    val orderService = OrderServiceImpl(ordersRepository, ordersRepositoryCustom)

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

    Given("상품 목록으로 주문 생성 요청") {
        val list = listOf(product)
        val orderCount: Long = 0
        val merchantUid = "ORD${LocalDate.MIN.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}" +
                "_${(orderCount + 1).toString().padStart(6, '0')}"
        When("상품리스트와 유저가 정상적으로 주어짐") {
            val slot = slot<Orders>()
            every { ordersRepository.countByCreatedAtAfter(any()) } returns orderCount
            every { ordersRepository.save(capture(slot)) } returns mockk<Orders>()
            mockkStatic(LocalDate::class)
            every { LocalDate.now() } returns LocalDate.MIN
            Then("주문이 성공적으로 저장") {
                orderService.saveByProductsAndUser(list, user)
                val result = slot.captured
                result.merchantUid shouldBe merchantUid
                result.totalProductOrderCount shouldBe list.size
            }
        }
    }
    Given("포트원의 주문번호 사전등록 요청") {
        val prepareRequest = PrepareRequest(
            orderInfo = PrepareRequest.OrderInfo(
                merchant_uid = "merchantUid",
                amount = 10000
            ),
            receiverInfo = PrepareRequest.ReceiverInfo(
                deliveryAddress = "deliveryAddress",
                deliveryAddressDetail = "deliveryAddressDetail",
                deliveryPostCode = "deliveryPostCode",
                receiverName = "receiverName",
                receiverPhoneNumber = "receiverPhoneNumber",
                deliveryRequestContent = "deliveryRequestContent"
            )
        )
        val requestOrder = Orders(
            user = user,
            merchantUid = "merchantUid",
            originTotalPrice = 10000,
            totalDiscountPrice = 9000,
            totalProductOrderCount = 1,
            orderStatus = OrderStatus.READY,
            cancelAvailableAmount = 9000

        )
        When("PrepareRequest Dto가 정상적으로 주어짐") {
            val slot = slot<Orders>()
            every { ordersRepository.save(capture(slot)) } returns mockk<Orders>()
            Then("request로 주문을 수정") {
                val result = orderService.updateByPrepareRequest(requestOrder, prepareRequest)
                val updateOrder = slot.captured
                result.merchant_uid shouldBe requestOrder.merchantUid
                updateOrder.deliveryAddress shouldBe prepareRequest.receiverInfo?.deliveryAddress
            }
        }
    }

    Given("merchantUid로 주문 조회 요청") {
        When("merchantUid가 정상적으로 주어짐") {
            Then("주문을 정상적으로 조회") {
                every { ordersRepository.findByMerchantUid(any()) } returns order
                val result = orderService.findByMerchantUid(order.merchantUid)
                result shouldBe order
            }
            Then("주문이 조회되지 않음 ${OrderErrorCode.NOT_FOUND_ORDER} 예외 발생") {
                every { ordersRepository.findByMerchantUid(any()) } returns null
                val result = shouldThrow<RestApiException> { orderService.findByMerchantUid("") }
                result.baseErrorCode shouldBe OrderErrorCode.NOT_FOUND_ORDER
            }
        }
    }

    Given("merchantUid로 주문과 유저 조회 요청") {
        When("merchantUid가 정상적으로 주어짐") {
            Then("주문과 유저가 성공적으로 조회") {
                every { ordersRepository.findByMerchantUid(any()) } returns order
                val result = orderService.findOrderAndUserByMerchantUid(order.merchantUid)
                result.first shouldBe order
                result.second.name shouldBe user.name
                result.second.email shouldBe user.email
            }
            Then("주문이 조회되지 않음") {
                every { ordersRepository.findByMerchantUid(any()) } returns null
                val result = shouldThrow<RestApiException> { orderService.findOrderAndUserByMerchantUid("") }
                result.baseErrorCode shouldBe OrderErrorCode.NOT_FOUND_ORDER
            }
        }
    }

    Given("유저의 완료된 주문 조회 요청") {
        When("userId가 정상적으로 주어짐") {
            Then("주문을 성공적으로 조회") {
                every {
                    ordersRepositoryCustom.findAllCompleteOrdersByUserId(
                        any(),
                        any()
                    )
                } returns PageImpl(listOf(order))
                val result = orderService.findAllByUserIdAndPageable(user.id!!, Pageable.ofSize(1))
                result.content.size shouldBe 1
                result.content.first().merchantUid shouldBe order.merchantUid
            }
            Then("주문이 없음") {
                every {
                    ordersRepositoryCustom.findAllCompleteOrdersByUserId(
                        any(),
                        any()
                    )
                } returns PageImpl(emptyList())
                val result = orderService.findAllByUserIdAndPageable(user.id!!, Pageable.ofSize(1))
                result.content.size shouldBe 0
            }
        }
    }

    Given("주문 아이디로 주문 조회 요청") {
        When("주문 아이디가 정상적으로 주어짐") {
            Then("주문을 성공적으로 조회") {
                every { ordersRepository.findById(any()) } returns Optional.of(order)
                val result = orderService.findByOrderId(order.id!!)
                result shouldBe order
            }
            Then("주문이 조회되지 않음") {
                every { ordersRepository.findById(any()) } returns Optional.empty()
                val result = shouldThrow<RestApiException> { orderService.findByOrderId(0L) }
                result.baseErrorCode shouldBe OrderErrorCode.NOT_FOUND_ORDER
            }
        }
    }

    Given("유저의 주문이 존재하는지 요청") {
        When("유저 아이디가 정상적으로 주어짐") {
            Then("주문이 존재함") {
                every { ordersRepository.existsByUserId(any()) } returns true
                val result = orderService.existsByUserId(user.id!!)
                result shouldBe true
            }
            Then("주문이 존재하지 않음") {
                every { ordersRepository.existsByUserId(any()) } returns false
                val result = orderService.existsByUserId(user.id!!)
                result shouldBe false

            }
        }
    }
})
