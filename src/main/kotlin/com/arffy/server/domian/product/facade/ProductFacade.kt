package com.arffy.server.domian.product.facade

import com.arffy.server.domian.product.dto.ProductDetailResponse
import com.arffy.server.domian.product.exception.ProductErrorCode
import com.arffy.server.domian.product.service.ProductServiceImpl
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.infra.s3.constant.ImageType
import com.arffy.server.infra.s3.service.ImageService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class ProductFacade(
    private val productService: ProductServiceImpl,
    private val imageService: ImageService,
) {
    @Transactional(readOnly = true)
    fun findProductDetailResponseById(
        id: Long?
    ): ProductDetailResponse {
        if (id == null) throw RestApiException(ProductErrorCode.REQUIRED_PRODUCT_ID)
        log.info { "ProductFacade.findProductDetailResponseById" }
        log.info { "productId : $id" }
        val product = productService.findById(id)
        val imageUrlList = imageService.getImageList(
            product.id!!,
            ImageType.PRODUCT
        ).map { image -> image.imageUrl }
        return ProductDetailResponse.from(product, imageUrlList)
    }
}