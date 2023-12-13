package com.arffy.server.domian.order.service

import com.arffy.server.domian.order.dto.OrderProductDto
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.order.entity.OrdersDetail
import com.arffy.server.domian.product.Product

interface OrderDetailService {
    fun saveAllByOrderAndProductList(
        order: Orders,
        products: List<Product>
    ): List<OrdersDetail>

    fun findAllByOrderId(
        orderId: Long
    ): List<OrdersDetail>

    fun findAllByOrderIdCustom(
        orderIds: List<Long?>
    ): List<OrderProductDto>

    fun findAllProductsByOrderId(
        orderId: Long
    ): List<Product>
}