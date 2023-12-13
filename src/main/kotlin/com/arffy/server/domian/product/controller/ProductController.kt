package com.arffy.server.domian.product.controller

import com.arffy.server.domian.product.dto.ProductDetailResponse
import com.arffy.server.domian.product.dto.ProductResponse
import com.arffy.server.domian.product.facade.ProductFacade
import com.arffy.server.domian.product.service.ProductServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "상품 API")
@RestController
@RequestMapping("/api/v1/product")
class ProductController(
    private val productService: ProductServiceImpl,
    private val productFacade: ProductFacade,
) {

    @GetMapping
    @Operation(
        summary = "상품 목록 조회",
        description = "카테고리, 상품명으로 상품 목록을 조회"
    )
    @Parameters(
        value = [
            Parameter(name = "productName", description = "상품명", example = "아이폰", required = false),
            Parameter(name = "category", description = "카테고리", example = "ALL"),
        ]
    )
    fun getAllProduct(
        @RequestParam
        productName: String?,
        @RequestParam
        category: String?,
        pageable: Pageable
    ): ResponseEntity<Page<ProductResponse>> {
        return ResponseEntity.ok(
            productService.findAllProductResponseByProductNameAndCategoryAndPageable(
                productName,
                category,
                pageable
            )
        )
    }

    @GetMapping("/{productId}")
    @Operation(
        summary = "상품 상세 조회",
        description = "상품 상세 조회"
    )
    fun getProductById(
        @PathVariable("productId")
        id: Long?
    ): ResponseEntity<ProductDetailResponse> {
        return ResponseEntity.ok(
            productFacade.findProductDetailResponseById(id)
        )
    }

    @GetMapping("/{productId}/basic")
    @Operation(
        summary = "상품 기본 정보 조회",
        description = "상품 기본 정보 조회"
    )
    fun getProductBasicById(
        @PathVariable("productId")
        id: Long?
    ): ResponseEntity<ProductResponse> {
        return ResponseEntity.ok(
            productService.findProductResponseById(id)
        )
    }

}
