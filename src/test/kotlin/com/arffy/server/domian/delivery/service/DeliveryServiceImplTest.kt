package com.arffy.server.domian.delivery.service

import com.arffy.server.domian.delivery.Delivery
import com.arffy.server.domian.delivery.DeliveryCarrier
import com.arffy.server.domian.delivery.DeliveryStatus
import com.arffy.server.domian.delivery.exception.DeliveryErrorCode
import com.arffy.server.domian.delivery.repository.DeliveryRepository
import com.arffy.server.domian.order.entity.OrderStatus
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.order.entity.OrdersDetail
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
import io.mockk.slot

class DeliveryServiceImplTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val deliveryRepository = mockk<DeliveryRepository>(relaxed = true)
    val deliveryService = DeliveryServiceImpl(deliveryRepository)
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
    val delivery = Delivery(
        ordersDetail = orderDetail,
        deliveryStatus = DeliveryStatus.DELIVERY,
        deliveryCarrier = DeliveryCarrier.E_POST,
        trackingNumber = "12341234",
    )

    Given("ordersDetailId로 배송 조회 요청") {
        When("ordersDetailId가 null이 아님") {
            every { deliveryRepository.findByOrdersDetailId(any()) } returns delivery
            val result = deliveryService.findByOrdersDetailId(1L)
            Then("조회 성공") {
                result.trackingNumber shouldBe delivery.trackingNumber
            }
        }
        When("${DeliveryErrorCode.NOT_FOUND_DELIVERY} 에러 발생") {
            every { deliveryRepository.findByOrdersDetailId(any()) } returns null
            val result = shouldThrow<RestApiException> { deliveryService.findByOrdersDetailId(1L) }
            Then("에러 - 배송 정보를 찾지 못함") {
                result.baseErrorCode shouldBe DeliveryErrorCode.NOT_FOUND_DELIVERY
            }
        }
    }

    Given("trackingNumber로 배송 조회 요청") {
        When("trackingNumber가 null이 아님") {
            every { deliveryRepository.findByTrackingNumber(any()) } returns delivery
            val result = deliveryService.findByTrackingNumber("12341234")
            Then("조회 성공") {
                result.trackingNumber shouldBe delivery.trackingNumber
            }
        }
        When("배송 정보를 찾지 못함") {
            every { deliveryRepository.findByTrackingNumber(any()) } returns null
            val result = shouldThrow<RestApiException> { deliveryService.findByTrackingNumber("12341234") }
            Then("${DeliveryErrorCode.NOT_FOUND_DELIVERY} 에러 발생") {
                result.baseErrorCode shouldBe DeliveryErrorCode.NOT_FOUND_DELIVERY
            }
        }
    }

    Given("배송 정보 생성 요청") {
        When("delivery가 null이 아님") {
            every { deliveryRepository.save(any()) } returns delivery
            val result = deliveryService.save(delivery)
            Then("생성 성공") {
                result.trackingNumber shouldBe delivery.trackingNumber
            }
        }
    }

    Given("배송 상태로 모든 배송 정보 조회 요청") {
        When("deliveryStatus가 null이 아님") {
            every { deliveryRepository.findAllByDeliveryStatus(any()) } returns listOf(delivery)
            val result = deliveryService.findAllByDeliveryStatus(DeliveryStatus.DELIVERY)
            Then("조회 성공") {
                result[0].trackingNumber shouldBe delivery.trackingNumber
            }
        }
        When("조회 결과 없음") {
            every { deliveryRepository.findAllByDeliveryStatus(any()) } returns listOf()
            val result = deliveryService.findAllByDeliveryStatus(DeliveryStatus.DELIVERY)
            Then("조회 결과 없음") {
                result.size shouldBe 0
            }
        }
    }

    Given("배송정보 리스트 저장 요청") {
        When("list가 null이 아님") {
            val deliveryList = listOf(delivery)
            val slot = slot<List<Delivery>>()
            every { deliveryRepository.saveAll(capture(slot)) } returns deliveryList
            Then("정상적으로 저장") {
                deliveryService.saveAll(deliveryList)
                val result = slot.captured
                result.size shouldBe deliveryList.size
                result.forEachIndexed { index, delivery ->
                    delivery.trackingNumber shouldBe deliveryList[index].trackingNumber
                }
            }
        }
    }
})
