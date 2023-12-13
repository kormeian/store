package com.arffy.server.domian.product.service

import com.arffy.server.domian.product.Product
import com.arffy.server.domian.product.ProductCategory
import com.arffy.server.domian.product.dto.ProductResponse
import com.arffy.server.domian.product.exception.ProductErrorCode
import com.arffy.server.domian.product.repository.ProductRepository
import com.arffy.server.domian.product.repository.ProductRepositoryCustomImpl
import com.arffy.server.global.exception.RestApiException
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productRepositoryCustom: ProductRepositoryCustomImpl,
) : ProductService {
    @Transactional(readOnly = true)
    override fun findAllProductResponseByProductNameAndCategoryAndPageable(
        productName: String?,
        category: String?,
        pageable: Pageable
    ): Page<ProductResponse> {
        val categoryRequest = category ?: "ALL"
        log.info("ProductServiceImpl.findAllProductResponseByProductNameAndCategoryAndPageable")
        log.info { "productName : $productName, category : $categoryRequest" }
        if (categoryRequest != "ALL") {
            ProductCategory.from(categoryRequest)
        }
        return productRepositoryCustom.findAllProduct(
            productName = productName,
            category = categoryRequest,
            pageable = pageable
        )
    }

    @Transactional(readOnly = true)
    override fun findById(
        id: Long
    ): Product {
        log.info { "ProductServiceImpl.findById" }
        log.info { "productId = $id" }
        return productRepository.findById(id).orElseThrow {
            RestApiException(
                ProductErrorCode.NOT_FOUND_PRODUCT
            )
        }
    }

    @Transactional(readOnly = true)
    override fun findProductResponseById(
        id: Long?
    ): ProductResponse {
        if (id == null) throw RestApiException(ProductErrorCode.REQUIRED_PRODUCT_ID)
        log.info { "ProductServiceImpl.findProductResponseById" }
        log.info { "productId = $id" }
        val product = productRepository.findById(id).orElseThrow {
            RestApiException(
                ProductErrorCode.NOT_FOUND_PRODUCT
            )
        }
        return ProductResponse.from(product)
    }

    @Transactional(readOnly = true)
    override fun findAllByIdIn(
        productIds: List<Long>
    ): List<Product> {
        log.info { "ProductServiceImpl.findAllByIdIn" }
        log.info { "productIds = $productIds" }
        return productRepository.findAllByIdIn(productIds)
    }

    @Transactional
    override fun save(
        product: Product
    ): Product {
        val saveProduct = productRepository.save(product)
        log.info { "ProductServiceImpl.save" }
        log.info { "productId = ${saveProduct.id}" }
        return saveProduct
    }
}



