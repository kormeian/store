package com.arffy.server.domian.product.dto

import com.arffy.server.domian.product.Product
import com.fasterxml.jackson.annotation.JsonInclude
import com.querydsl.core.annotations.QueryProjection
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "상품 목록 조회 응답")
data class ProductResponse @QueryProjection constructor(
    @field:Schema(description = "상품 ID")
    val productId: Long,

    @field:Schema(description = "상품 썸네일")
    val thumbnail: String?,

    @field:Schema(description = "상품 썸네일 버전")
    val thumbnailVersion: Int?,

    @field:Schema(description = "상품명")
    val productName: String,

    @field:Schema(description = "상품 가격")
    val price: Int,

    @field:Schema(description = "상품 할인율")
    val discountRate: Int,

    @field:Schema(description = "상품 할인 가격")
    val discountPrice: Int,

    @field:Schema(description = "상품 재고")
    val quantity: Int,
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                productId = product.id!!,
                thumbnail = product.thumbnail,
                thumbnailVersion = product.thumbnailVersion,
                productName = product.productName,
                price = product.price,
                discountRate = product.discountRate,
                discountPrice = product.discountPrice,
                quantity = product.quantity,
            )
        }
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "상품 상세 조회 응답")
data class ProductDetailResponse(
    @field:Schema(description = "상품 이미지")
    val imageUrls: List<String>?,

    @field:Schema(description = "상품 ID")
    val productId: Long,

    @field:Schema(description = "상품명")
    val productName: String,

    @field:Schema(description = "상품 가격")
    val price: Int,

    @field:Schema(description = "상품 할인율")
    val discountRate: Int,

    @field:Schema(description = "상품 할인 가격")
    val discountPrice: Int,

    @field:Schema(description = "상품 재고")
    val quantity: Int,

    @field:Schema(description = "상품 설명")
    val description: String,

    @field:Schema(description = "상품 생산 연도")
    val period: String,

    @field:Schema(description = "상품 생산 국가")
    val country: String,

    @field:Schema(description = "상품 폭")
    val width: String? = null,

    @field:Schema(description = "상품 깊이")
    val depth: String? = null,

    @field:Schema(description = "상품 높이")
    val height: String? = null,

    @field:Schema(description = "상품 최소 줄 길이")
    val minLineHeight: String? = null,

    @field:Schema(description = "상품 최대 줄 길이")
    val maxLineHeight: String? = null,

    @field:Schema(description = "상품 소재")
    val material: String,

    @field:Schema(description = "상품 상태")
    val condition: String,
) {
    companion object {
        fun from(product: Product, imageList: List<String>): ProductDetailResponse {
            return ProductDetailResponse(
                imageUrls = imageList,
                productId = product.id!!,
                productName = product.productName,
                price = product.price,
                discountRate = product.discountRate,
                discountPrice = product.discountPrice,
                quantity = product.quantity,
                description = product.description,
                period = product.period,
                country = product.country,
                width = product.width,
                depth = product.depth,
                height = product.height,
                minLineHeight = product.minLineHeight,
                maxLineHeight = product.maxLineHeight,
                material = product.material,
                condition = product.status,
            )
        }
    }

}