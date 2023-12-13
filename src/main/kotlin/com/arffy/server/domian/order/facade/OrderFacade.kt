package com.arffy.server.domian.order.facade

import com.arffy.server.domian.order.dto.*
import com.arffy.server.domian.order.entity.OrderStatus
import com.arffy.server.domian.order.exception.OrderErrorCode
import com.arffy.server.domian.order.service.OrderDetailServiceImpl
import com.arffy.server.domian.order.service.OrderServiceImpl
import com.arffy.server.domian.payment.service.PaymentServiceImpl
import com.arffy.server.domian.product.service.ProductServiceImpl
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.infra.portOne.service.PortOneServiceImpl
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Component
class OrderFacade(
    val orderService: OrderServiceImpl,
    val orderDetailService: OrderDetailServiceImpl,
    val productService: ProductServiceImpl,
    val paymentService: PaymentServiceImpl,
    val portOneService: PortOneServiceImpl,
) {
    fun getOrderInfoResponseByProductIdInAndUser(
        productIds: List<Long>,
        user: User
    ): OrderInfoResponse {
        log.info { "OrderFacade.getOrderInfoResponseByProductIdInAndUser" }
        log.info { "productIds = $productIds, userId = ${user.id}" }
        if (productIds.isEmpty()) throw RestApiException(OrderErrorCode.REQUIRED_PRODUCT_ID)
        val products = productService.findAllByIdIn(productIds)
        products.forEach {
            if (it.quantity == 0) throw RestApiException(OrderErrorCode.NOT_ENOUGH_PRODUCT)
        }

        val order = orderService.saveByProductsAndUser(products, user)

        orderDetailService.saveAllByOrderAndProductList(order, products)

        return OrderInfoResponse(
            merchant_uid = order.merchantUid,
            amount = order.totalDiscountPrice,
            ordersId = order.id ?: throw RestApiException(OrderErrorCode.NOT_FOUND_ORDER),
        )
    }

    @Transactional
    fun postPrepareByPrepareRequestAndUser(
        prepareRequest: PrepareRequest?,
        user: User
    ): PrepareResponse {
        if (prepareRequest == null) throw RestApiException(OrderErrorCode.REQUIRED_PREPARE_REQUEST)
        validatePrepareRequest(prepareRequest)
        log.info { "OrderFacade.postPrepareByPrepareRequestAndUser" }
        log.info { "merchantUid = ${prepareRequest.orderInfo?.merchant_uid}, userId = ${user.id}" }
        val order = orderService.findByMerchantUid(prepareRequest.orderInfo?.merchant_uid!!)
        if (order.user.id != user.id) throw RestApiException(OrderErrorCode.NOT_MATCH_USER)
        val orderDetails = orderDetailService.findAllByOrderId(
            order.id!!
        )
        if (orderDetails.isEmpty()) throw RestApiException(OrderErrorCode.NOT_FOUND_ORDER_DETAIL)
        val products = orderDetails.map { it.product }
        if (products.isEmpty()) throw RestApiException(OrderErrorCode.NOT_FOUND_PRODUCT)
        products.forEach {
            if (it.quantity == 0) throw RestApiException(OrderErrorCode.NOT_ENOUGH_PRODUCT)
        }
        if (order.orderStatus != OrderStatus.READY) {

            if (order.orderStatus == OrderStatus.PROGRESS) {

                order.orderStatus = OrderStatus.PROGRESS
                orderService.save(order)
                return PrepareResponse(
                    merchant_uid = prepareRequest.orderInfo.merchant_uid,
                    amount = order.totalDiscountPrice,
                    name = if (products.size == 1) products.first().productName else
                        "${products.first().productName} 외 ${products.size - 1}건",
                    buyer_email = user.email,
                    buyer_name = user.name,
                    buyer_tel = user.phoneNumber,
                    buyer_addr = if (user.addressDetail.isNullOrBlank()) "${user.address}" else "${user.address} ${user.addressDetail}",
                    buyer_postcode = user.postCode,
                )
            }

            throw RestApiException(OrderErrorCode.NOT_MATCH_ORDER_STATUS)
        }
        val payResponse = orderService.updateByPrepareRequest(order, prepareRequest)
        payResponse.name = if (products.size == 1) products.first().productName else
            "${products.first().productName} 외 ${products.size - 1}건"
        payResponse.buyer_email = user.email
        payResponse.buyer_name = user.name
        payResponse.buyer_tel = user.phoneNumber
        payResponse.buyer_addr = "${user.address} ${user.addressDetail}"
        payResponse.buyer_postcode = user.postCode
        portOneService.postPrepare(payResponse.merchant_uid, payResponse.amount)
        return payResponse
    }

    private fun validatePrepareRequest(
        prepareRequest: PrepareRequest
    ) {
        if (prepareRequest.orderInfo == null) throw RestApiException(OrderErrorCode.REQUIRED_ORDER_INFO)
        if (prepareRequest.receiverInfo == null) throw RestApiException(OrderErrorCode.REQUIRED_RECEIVER_INFO)
        if (prepareRequest.orderInfo.merchant_uid.isNullOrBlank()) throw RestApiException(OrderErrorCode.REQUIRED_MERCHANT_UID)
        if (prepareRequest.orderInfo.amount == 0) throw RestApiException(OrderErrorCode.REQUIRED_AMOUNT)
        if (prepareRequest.receiverInfo.deliveryAddress.isNullOrBlank()) throw RestApiException(OrderErrorCode.REQUIRED_DELIVERY_ADDRESS)
        if (prepareRequest.receiverInfo.deliveryPostCode.isNullOrBlank()) throw RestApiException(OrderErrorCode.REQUIRED_DELIVERY_POST_CODE)
        if (prepareRequest.receiverInfo.receiverName.isNullOrBlank()) throw RestApiException(OrderErrorCode.REQUIRED_RECEIVER_NAME)
        if (prepareRequest.receiverInfo.receiverPhoneNumber.isNullOrBlank()) throw RestApiException(OrderErrorCode.REQUIRED_RECEIVER_PHONE_NUMBER)
    }

    fun getAllOrdersResponseByUserAndPageable(
        user: User,
        pageable: Pageable
    ): Page<OrdersResponse> {
        log.info { "OrderFacade.getAllOrdersResponseByUserAndPageable" }
        log.info { "userId = ${user.id}" }
        try {
            val orderPage = orderService.findAllByUserIdAndPageable(user.id!!, pageable).map { OrdersResponse.of(it) }
            val orderIds = orderPage.content.map { it.ordersId }
            val orderDetailList = orderDetailService.findAllByOrderIdCustom(orderIds)
            orderPage.content.forEach { ordersResponse ->
                orderDetailList.forEach { orderDetail ->
                    if (ordersResponse.ordersId == orderDetail.ordersId) {
                        ordersResponse.productList.add(orderDetail)
                    }
                }
            }
            return orderPage
        } catch (e: RestApiException) {
            throw e
        }
    }

    @Transactional(readOnly = true)
    fun getOrdersDetailResponseByOrderIdAndMerchantUidAndUserId(
        ordersId: Long,
        merchantUid: String,
        userId: Long
    ): OrdersDetailResponse {
        log.info { "OrderFacade.getOrdersDetailResponseByOrderIdAndMerchantUidAndUserId" }
        log.info { "ordersId = $ordersId, merchantUid = $merchantUid, userId = $userId" }
        try {
            val order = orderService.findByOrderId(ordersId)
            if (order.user.id != userId) throw RestApiException(OrderErrorCode.NOT_AUTHORIZED)
            if (order.orderStatus != OrderStatus.PAID &&
                order.orderStatus != OrderStatus.PARTIAL &&
                order.orderStatus != OrderStatus.REFUND
            ) {
                throw RestApiException(OrderErrorCode.NOT_MATCH_ORDER_STATUS)
            }
            val orderDetailList = orderDetailService.findAllByOrderIdCustom(listOf(ordersId))
            if (orderDetailList.isEmpty()) throw RestApiException(OrderErrorCode.NOT_FOUND_ORDER_DETAIL)
            val payment = paymentService.findByOrderId(ordersId)
            return OrdersDetailResponse.of(
                order,
                orderDetailList,
                payment,
            )
        } catch (e: RestApiException) {
            throw e
        }
    }


    @Transactional(readOnly = true)
    fun getOrdersPaymentInfoResponseByOrdersIdAndUser(
        ordersId: Long,
        user: User
    ): OrdersPaymentInfoResponse {
        log.info { "OrderFacade.getOrdersPaymentInfoResponseByOrdersIdAndUser" }
        log.info { "ordersId = $ordersId, userId = ${user.id}" }
        val order = orderService.findByOrderId(ordersId)
        if (order.user.id != user.id) throw RestApiException(OrderErrorCode.NOT_AUTHORIZED)
        val products = orderDetailService.findAllByOrderId(ordersId).map { it.product }
        if (products.isEmpty()) throw RestApiException(OrderErrorCode.NOT_FOUND_PRODUCT)
        return OrdersPaymentInfoResponse(
            ordersId = ordersId,
            buyerInfo = OrdersPaymentInfoResponse.BuyerInfo(
                name = user.name,
                mobile = user.phoneNumber ?: "",
                address = user.address ?: "",
                addressDetail = user.addressDetail ?: "",
                postCode = user.postCode ?: "",
            ),
            productsInfo = products.map { OrdersPaymentInfoResponse.ProductInfo.of(it) },
            priceInfo = OrdersPaymentInfoResponse.PriceInfo(
                originTotalPrice = order.originTotalPrice,
                totalDiscountPrice = order.totalDiscountPrice,
                discountedPrice = order.originTotalPrice - order.totalDiscountPrice,
            )
        )
    }
}