package com.arffy.server.domian.order.service

import com.arffy.server.domian.order.dto.OrderProductDto
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.order.entity.OrdersDetail
import com.arffy.server.domian.order.repository.OrdersDetailRepository
import com.arffy.server.domian.order.repository.OrdersDetailRepositoryCustomImpl
import com.arffy.server.domian.product.Product
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class OrderDetailServiceImpl(
    val ordersDetailRepository: OrdersDetailRepository,
    val ordersDetailRepositoryCustom: OrdersDetailRepositoryCustomImpl,
) : OrderDetailService {

    @Transactional
    override fun saveAllByOrderAndProductList(
        order: Orders,
        products: List<Product>
    ): List<OrdersDetail> {
        log.info { "OrderDetailServiceImpl.saveAllByOrderAndProductList" }
        log.info { "orderId = ${order.id}, productIds = ${products.map { it.id }}" }
        val orderDetails = mutableListOf<OrdersDetail>()
        products.forEach {
            orderDetails.add(
                ordersDetailRepository.save(
                    OrdersDetail(
                        orders = order,
                        product = it,
                        productOrderCount = 1,
                        originPrice = it.price,
                        discountPrice = it.discountPrice,
                        discountRate = it.discountRate,
                    )
                )
            )
        }
        return orderDetails
    }

    @Transactional(readOnly = true)
    override fun findAllByOrderId(
        orderId: Long
    ): List<OrdersDetail> {
        log.info { "OrderDetailServiceImpl.findAllByOrderId" }
        log.info { "orderId = $orderId" }
        return ordersDetailRepository.findAllByOrdersId(orderId)
    }

    @Transactional
    override fun findAllProductsByOrderId(
        orderId: Long
    ): List<Product> {
        log.info { "OrderDetailServiceImpl.findAllProductsByOrderId" }
        log.info { "orderId = $orderId" }
        val products = mutableListOf<Product>()
        ordersDetailRepository.findAllByOrdersId(orderId).forEach {
            products.add(Product(
                productName = it.product.productName,
                price = it.product.price,
                discountPrice = it.product.discountPrice,
                discountRate = it.product.discountRate,
                period = it.product.period,
                country = it.product.country,
                width = it.product.width,
                depth = it.product.depth,
                height = it.product.height,
                minLineHeight = it.product.minLineHeight,
                maxLineHeight = it.product.maxLineHeight,
                category = it.product.category,
                material = it.product.material,
                status = it.product.status,
                description = it.product.description,
                quantity = it.product.quantity,
                thumbnail = it.product.thumbnail,
                thumbnailVersion = it.product.thumbnailVersion,
            ).apply { id = it.product.id })
        }
        return products
    }

    @Transactional(readOnly = true)
    override fun findAllByOrderIdCustom(
        orderIds: List<Long?>
    ): List<OrderProductDto> {
        log.info { "OrderDetailServiceImpl.findAllByOrderIdCustom" }
        log.info { "orderIds = $orderIds" }
        return ordersDetailRepositoryCustom.findAllByOrdersIds(orderIds).map { OrderProductDto.from(it) }
    }

}