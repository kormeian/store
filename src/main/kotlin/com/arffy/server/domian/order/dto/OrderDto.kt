package com.arffy.server.domian.order.dto

import com.arffy.server.domian.delivery.DeliveryCarrier
import com.arffy.server.domian.delivery.DeliveryStatus
import com.arffy.server.domian.order.entity.Orders
import com.arffy.server.domian.order.repository.OrdersDetailQueryDto
import com.arffy.server.domian.payment.entity.Payments
import com.arffy.server.domian.product.Product
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.time.format.DateTimeFormatter
import javax.validation.constraints.NotNull

class OrderInfoResponse(
    @field:Schema(description = "주문 번호", example = "ORDyyyyMMdd_000001")
    val merchant_uid: String,
    @field:Schema(description = "주문 가격", example = "10000")
    val amount: Int,
    @field:Schema(description = "주문 아이디", example = "1")
    val ordersId: Long,
)

class PrepareRequest(
    @field:NotNull
    val orderInfo: OrderInfo?,
    @field:NotNull
    val receiverInfo: ReceiverInfo?,
) {
    class OrderInfo(
        @field:Schema(description = "주문 번호", example = "ORDyyyyMMdd_000001")
        val merchant_uid: String?,
        @field:Schema(description = "주문 금액", example = "10000")
        val amount: Int?,
    )

    class ReceiverInfo(
        @field:Schema(description = "배송지 주소", example = "서울특별시 강남구 삼성동")
        val deliveryAddress: String?,
        @field:Schema(description = "배송지 상세 주소", example = "삼성아파트 101동 101호")
        val deliveryAddressDetail: String? = null,
        @field:Schema(description = "배송지 우편번호", example = "12345")
        val deliveryPostCode: String?,
        @field:Schema(description = "받는 사람 이름", example = "홍길동")
        val receiverName: String?,
        @field:Schema(description = "받는 사람 전화번호", example = "01012345678")
        val receiverPhoneNumber: String?,
        @field:Schema(description = "배송 요청 사항", example = "부재시 경비실에 맡겨주세요", required = false)
        val deliveryRequestContent: String? = null,
    )
}

class PrepareResponse(
    @field:Schema(description = "주문 번호", example = "ORDyyyyMMdd_000001")
    val merchant_uid: String,
    @field:Schema(description = "결제 금액", example = "10000")
    val amount: Int,
    @field:Schema(description = "주문 이름", example = "첫번째 상품명 외 2견")
    var name: String? = null,
    @field:Schema(description = "주문자 이메일", example = "asdf1234@kakao.com")
    var buyer_email: String? = null,
    @field:Schema(description = "주문자 이름", example = "홍길동")
    var buyer_name: String? = null,
    @field:Schema(description = "주문자 전화번호", example = "01012345678")
    var buyer_tel: String? = null,
    @field:Schema(description = "주문자 주소", example = "서울특별시 강남구 삼성동 ~~")
    var buyer_addr: String? = null,
    @field:Schema(description = "주문자 우편번호", example = "12345")
    var buyer_postcode: String? = null,
)


class DeliveryDto(
    @field:Schema(description = "배송 ID", example = "1")
    val deliveryId: Long? = null,
    @field:Schema(description = "배송 상태", example = "배송중")
    val deliveryStatus: String? = null,
    @field:Schema(description = "배송 업체", example = "CJ대한통운")
    val deliveryCarrier: String? = null,
    @field:Schema(description = "송장 번호", example = "1234567890")
    val trackingNumber: String? = null,
) {
    companion object {
        fun of(
            deliveryId: Long,
            deliveryStatus: DeliveryStatus,
            deliveryCarrier: DeliveryCarrier,
            trackingNumber: String
        ): DeliveryDto {
            return DeliveryDto(
                deliveryId = deliveryId,
                deliveryStatus = deliveryStatus.text,
                deliveryCarrier = deliveryCarrier.text,
                trackingNumber = trackingNumber,
            )
        }
    }
}


class OrderProductDto(
    @field:Schema(description = "주문 ID", example = "1")
    val ordersId: Long,
    @field:Schema(description = "주문 상세 ID", example = "1")
    val ordersDetailId: Long,

    @field:Schema(description = "주문 취소 요청 시간", example = "2021-01-01 00:00:00", nullable = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val requestCancelledAt: String? = null,

    @field:Schema(description = "주문 취소 사유", example = "사용자 요청", nullable = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val cancelReason: String? = null,

    @field:Schema(description = "주문 취소 사유 내용", example = "사용자가 주문을 취소하였습니다.", nullable = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val cancelReasonContent: String? = null,

    @field:Schema(description = "주문 취소 상태", example = "READY", nullable = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val refundStatus: String? = null,

    @field:Schema(description = "상품 ID", example = "1")
    val productId: Long,
    @field:Schema(description = "상품 이름", example = "상품1")
    val productName: String,
    @field:Schema(description = "상품 가격", example = "10000")
    val price: Int,
    @field:Schema(description = "상품 할인된 가격", example = "9000")
    val discountPrice: Int,
    @field:Schema(description = "상품 할인율", example = "10")
    val discountRate: Int,
    @field:Schema(description = "상품 썸네일", example = "https://~~")
    val thumbnail: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var delivery: DeliveryDto? = null,
) {
    companion object {
        fun from(ordersDetailQueryDto: OrdersDetailQueryDto): OrderProductDto {
            val orderProductDto = OrderProductDto(
                ordersId = ordersDetailQueryDto.ordersId,
                ordersDetailId = ordersDetailQueryDto.ordersDetailId,
                requestCancelledAt = ordersDetailQueryDto.requestCancelledAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                cancelReason = ordersDetailQueryDto.cancelReason?.name,
                cancelReasonContent = ordersDetailQueryDto.cancelReasonContent,
                refundStatus = ordersDetailQueryDto.refundStatus?.name,
                productId = ordersDetailQueryDto.productId,
                productName = ordersDetailQueryDto.productName,
                price = ordersDetailQueryDto.price,
                discountPrice = ordersDetailQueryDto.discountPrice,
                discountRate = ordersDetailQueryDto.discountRate,
                thumbnail = ordersDetailQueryDto.thumbnail,
            )
            if (ordersDetailQueryDto.deliveryId != null &&
                ordersDetailQueryDto.deliveryStatus != null &&
                ordersDetailQueryDto.deliveryCarrier != null &&
                ordersDetailQueryDto.waybillNumber != null
            ) {
                orderProductDto.delivery = DeliveryDto.of(
                    deliveryId = ordersDetailQueryDto.deliveryId!!,
                    deliveryStatus = ordersDetailQueryDto.deliveryStatus!!,
                    deliveryCarrier = ordersDetailQueryDto.deliveryCarrier!!,
                    trackingNumber = ordersDetailQueryDto.waybillNumber!!
                )
            }
            return orderProductDto
        }
    }
}


class OrdersResponse(
    @field:Schema(description = "주문 ID", example = "1")
    val ordersId: Long,
    @field:Schema(description = "주문 번호", example = "ORDyyyyMMdd_000001")
    val merchantUid: String,
    @field:Schema(description = "주문 상태", example = "결제완료")
    val orderStatus: String,
    @field:Schema(description = "상품 리스트")
    val productList: MutableList<OrderProductDto> = mutableListOf(),
) {
    companion object {
        fun of(order: Orders): OrdersResponse {
            return OrdersResponse(
                ordersId = order.id!!,
                merchantUid = order.merchantUid,
                orderStatus = order.orderStatus.name,
            )
        }
    }
}

class OrdersDetailResponse(
    @field:Schema(description = "주문 번호", example = "ORDyyyyMMdd_000001")
    val merchantUid: String,
    @field:Schema(description = "수취인 이름", example = "홍길동")
    val receiverName: String,
    @field:Schema(description = "수취인 전화번호", example = "010-1234-5678")
    val receiverPhoneNumber: String,
    @field:Schema(description = "배송 주소", example = "서울시 강남구")
    val deliveryAddress: String,
    @field:Schema(description = "배송 주소 상세", example = "역삼동 123-456")
    val deliveryAddressDetail: String,
    @field:Schema(description = "배송 우편번호", example = "12345")
    val deliveryPostCode: String,
    @field:Schema(description = "배송 요청 사항", example = "부재시 경비실에 맡겨주세요", nullable = true)
    val deliveryRequestContent: String? = null,
    @field:Schema(description = "총 상품 가격", example = "10000")
    val originTotalPrice: Int,
    @field:Schema(description = "총 할인된 금액", example = "9000")
    val totalDiscountPrice: Int,
    @field:Schema(description = "총 할인 금액", example = "`1000")
    val discountedPrice: Int,
    @field:Schema(description = "상품 상태", example = "PAID")
    val orderStatus: String,
    @field:Schema(description = "결제 방법", example = "포인트 결제", nullable = true)
    val payMethod: String?,
    @field:Schema(description = "결제 제공사", example = "카카오페이", nullable = true)
    val pgProvider: String?,
    @field:Schema(description = "상품 리스트")
    val productList: List<OrderProductDto>,
) {
    companion object {
        fun of(order: Orders, productList: List<OrderProductDto>, payment: Payments): OrdersDetailResponse {
            return OrdersDetailResponse(
                merchantUid = order.merchantUid,
                receiverName = order.receiverName!!,
                receiverPhoneNumber = order.receiverPhoneNumber!!,
                deliveryAddress = order.deliveryAddress!!,
                deliveryAddressDetail = order.deliveryAddressDetail!!,
                deliveryPostCode = order.deliveryPostCode!!,
                deliveryRequestContent = order.deliveryRequestContent,
                originTotalPrice = order.originTotalPrice,
                totalDiscountPrice = order.totalDiscountPrice,
                discountedPrice = order.originTotalPrice - order.totalDiscountPrice,
                orderStatus = order.orderStatus.name,
                payMethod = payment.payMethod?.text,
                pgProvider = payment.pgProvider?.text,
                productList = productList,
            )
        }
    }
}

class OrdersPaymentInfoResponse(
    @field:Schema(description = "주문 아이디", example = "1")
    val ordersId: Long,
    val buyerInfo: BuyerInfo,
    val productsInfo: List<ProductInfo>,
    val priceInfo: PriceInfo,
) {
    class BuyerInfo(
        @field:Schema(description = "구매자 이름", example = "홍길동")
        val name: String,
        @field:Schema(description = "구매자 폰 번호", example = "01012341234")
        val mobile: String,
        @field:Schema(description = "구매자 주소", example = "강남구 역삼동")
        val address: String,
        @field:Schema(description = "구매자 상세 주소", example = "123-456")
        val addressDetail: String,
        @field:Schema(description = "구매자 우편번호", example = "12345")
        val postCode: String,
    )

    class ProductInfo(
        @field:Schema(description = "상품 ID", example = "1")
        val productId: Long,
        @field:Schema(description = "상품 썸네일", example = "썸네일url?version=1")
        val thumbnail: String,
        @field:Schema(description = "상품 이름", example = "상품 이름")
        val productName: String,
        @field:Schema(description = "상품 가격", example = "10000")
        val price: Int,
        @field:Schema(description = "상품 할인된 가격", example = "9000")
        val discountPrice: Int,
    ) {
        companion object {
            fun of(product: Product): ProductInfo {
                return ProductInfo(
                    productId = product.id!!,
                    thumbnail = product.thumbnail + "?version=" + product.thumbnailVersion,
                    productName = product.productName,
                    price = product.price,
                    discountPrice = product.discountPrice,
                )
            }
        }
    }

    class PriceInfo(
        @field:Schema(description = "총 상품 가격", example = "10000")
        val originTotalPrice: Int,
        @field:Schema(description = "총 할인된 금액", example = "9000")
        val totalDiscountPrice: Int,
        @field:Schema(description = "총 할인 금액", example = "1000")
        val discountedPrice: Int,
    )
}

