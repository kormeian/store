package com.arffy.server.domian.order.service

import com.arffy.server.domian.order.dto.PrepareRequest
import com.arffy.server.domian.order.dto.PrepareResponse
import com.arffy.server.domian.order.entity.OrderStatus
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.order.exception.OrderErrorCode
import com.arffy.server.domian.order.repository.OrdersRepository
import com.arffy.server.domian.order.repository.OrdersRepositoryCustomImpl
import com.arffy.server.domian.product.Product
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.exception.RestApiException
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val log = KotlinLogging.logger {}

@Service
class OrderServiceImpl(
    val ordersRepository: OrdersRepository,
    val ordersRepositoryCustom: OrdersRepositoryCustomImpl,
) : OrderService {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun saveByProductsAndUser(
        products: List<Product>,
        user: User
    ): Orders {
        log.info { "OrderServiceImpl.saveByProductsAndUser" }
        log.info { "productIds = ${products.map { it.id }}, userId = ${user.id}" }
        val merchantUid = createMerchantUid()
        return saveByUserAndProductsAndMerchantUid(user, products, merchantUid)
    }

    @Transactional(readOnly = true)
    fun createMerchantUid(): String {
        val count = ordersRepository.countByCreatedAtAfter(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
        val merchantUid = orderCountToMerchantUid(count)
        log.info { "OrderServiceImpl.createMerchantUid" }
        log.info { "merchantUid = $merchantUid" }
        return merchantUid
    }

    private fun orderCountToMerchantUid(
        count: Long
    ): String {
        return "ORD${LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}" +
                "_${(count + 1).toString().padStart(6, '0')}"
    }

    @Transactional
    override fun saveByUserAndProductsAndMerchantUid(
        user: User,
        products: List<Product>,
        merchantUid: String,
    ): Orders {
        log.info { "OrderServiceImpl.saveByUserAndProductsAndMerchantUid" }
        log.info { "userId = ${user.id}, productIds = ${products.map { it.id }}, merchantUid = $merchantUid" }
        return save(
            Orders(
                user = user,
                merchantUid = merchantUid,
                originTotalPrice = products.sumOf { it.price },
                totalDiscountPrice = products.sumOf { it.discountPrice },
                totalProductOrderCount = products.size,
                orderStatus = OrderStatus.READY,
                cancelAvailableAmount = products.sumOf { it.discountPrice },
            )
        )
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun updateByPrepareRequest(
        order: Orders,
        prepareRequest: PrepareRequest
    ): PrepareResponse {
        log.info { "OrderServiceImpl.updateByPrepareRequest" }
        log.info { "orderId = ${order.id}" }
        order.deliveryAddress = prepareRequest.receiverInfo?.deliveryAddress
        order.deliveryAddressDetail = prepareRequest.receiverInfo?.deliveryAddressDetail
        order.deliveryPostCode = prepareRequest.receiverInfo?.deliveryPostCode
        order.receiverName = prepareRequest.receiverInfo?.receiverName
        order.receiverPhoneNumber = prepareRequest.receiverInfo?.receiverPhoneNumber
        order.deliveryRequestContent = prepareRequest.receiverInfo?.deliveryRequestContent
        order.orderStatus = OrderStatus.PROGRESS
        save(order)

        return PrepareResponse(
            merchant_uid = order.merchantUid,
            amount = order.totalDiscountPrice,
        )
    }

    @Transactional(readOnly = true)
    override fun findByMerchantUid(
        merchantUid: String
    ): Orders {
        log.info { "OrderServiceImpl.findByMerchantUid" }
        log.info { "merchantUid = $merchantUid" }
        return ordersRepository.findByMerchantUid(merchantUid) ?: throw RestApiException(OrderErrorCode.NOT_FOUND_ORDER)
    }

    @Transactional(readOnly = true)
    fun findOrderAndUserByMerchantUid(
        merchantUid: String
    ): Pair<Orders, User> {
        log.info { "OrderServiceImpl.findOrderAndUserByMerchantUid" }
        log.info { "merchantUid = $merchantUid" }
        val orders = findByMerchantUid(merchantUid)
        val user = User(
            name = orders.user.name,
            email = orders.user.email,
            phoneNumber = orders.user.phoneNumber,
            address = orders.user.address,
            addressDetail = orders.user.addressDetail,
            postCode = orders.user.postCode,
            role = orders.user.role,
            authProvider = orders.user.authProvider,
            oauth2Id = orders.user.oauth2Id,
        )
        user.id = orders.user.id
        return Pair(orders, user)
    }

    @Transactional
    override fun save(
        order: Orders
    ): Orders {
        val saveOrder = ordersRepository.save(order)
        log.info { "OrderServiceImpl.save" }
        log.info { "orderID = ${saveOrder.id}" }
        return saveOrder
    }

    @Transactional(readOnly = true)
    override fun findAllByUserIdAndPageable(
        userId: Long,
        pageable: Pageable
    ): Page<Orders> {
        log.info { "OrderServiceImpl.findAllByUserIdAndPageable" }
        log.info { "userId = $userId" }
        return ordersRepositoryCustom.findAllCompleteOrdersByUserId(
            userId,
            pageable
        )
    }

    @Transactional(readOnly = true)
    override fun findByOrderId(
        ordersId: Long
    ): Orders {
        log.info { "OrderServiceImpl.findByOrderId" }
        log.info { "ordersId = $ordersId" }
        return ordersRepository.findById(ordersId).orElseThrow { RestApiException(OrderErrorCode.NOT_FOUND_ORDER) }
    }

    @Transactional(readOnly = true)
    override fun existsByUserId(
        userId: Long
    ): Boolean {
        log.info { "OrderServiceImpl.existsByUserId" }
        log.info { "userId = $userId" }
        return ordersRepository.existsByUserId(userId)
    }

}