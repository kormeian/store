package com.arffy.server.domian.product.service

import com.arffy.server.domian.product.Product
import com.arffy.server.domian.product.dto.ProductResponse
import com.arffy.server.domian.product.exception.ProductErrorCode
import com.arffy.server.domian.product.repository.ProductRepository
import com.arffy.server.domian.product.repository.ProductRepositoryCustomImpl
import com.arffy.server.global.exception.RestApiException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.springframework.data.domain.PageImpl
import java.util.*

class ProductServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val productRepository = mockk<ProductRepository>(relaxed = true)
    val productRepositoryCustom = mockk<ProductRepositoryCustomImpl>(relaxed = true)
    val productService = ProductServiceImpl(productRepository, productRepositoryCustom)


    Given("상품 이름, 카테고리로 상품 페이지 조회 요청") {
        When("상품 이름, 카테고리가 주어졌을때") {
            val productName1 = "productName"
            val productCategory1 = "PENDANT"
            val product1 = mockk<Product>(relaxed = true)
            every { product1.productName } returns productName1
            val productResponse1 = ProductResponse.from(product1)
            every { productRepositoryCustom.findAllProduct(productName1, productCategory1, any()) } returns PageImpl(
                listOf(
                    productResponse1
                )
            )
            Then("상품 페이지 조회 성공") {
                val result = productService.findAllProductResponseByProductNameAndCategoryAndPageable(
                    productName1,
                    productCategory1,
                    mockk()
                )
                result.content[0].productName shouldBe "productName"
                result.size shouldBe 1
            }
        }
        When("상품 이름만 주어졌을때") {
            val productName2 = "productName"
            val product2 = mockk<Product>(relaxed = true)
            every { product2.productName } returns productName2
            val productResponse2 = ProductResponse.from(product2)
            val slot = slot<String>()
            every { productRepositoryCustom.findAllProduct(productName2, capture(slot), any()) } returns PageImpl(
                listOf(
                    productResponse2
                )
            )
            Then("상품 페이지 조회 성공") {
                val result = productService.findAllProductResponseByProductNameAndCategoryAndPageable(
                    productName2,
                    null,
                    mockk()
                )
                result.content[0].productName shouldBe "productName"
                slot.captured shouldBe "ALL"
                result.size shouldBe 1
            }
        }
        When("카테고리만 주어졌을 때") {
            val productCategory3 = "PENDANT"
            val product3 = mockk<Product>(relaxed = true)
            val productResponse3 = ProductResponse.from(product3)
            val slot = slot<String>()
            every { productRepositoryCustom.findAllProduct(null, productCategory3, any()) } returns PageImpl(
                listOf(
                    productResponse3
                )
            )
            Then("상품 페이지 조회 성공") {
                val result = productService.findAllProductResponseByProductNameAndCategoryAndPageable(
                    null,
                    productCategory3,
                    mockk()
                )
                result.content[0] shouldBe productResponse3
                result.size shouldBe 1
            }
        }
        When("상품 이름과 카테고리가 안 주어졌을 때") {
            val product4 = mockk<Product>(relaxed = true)
            val productResponse4 = ProductResponse.from(product4)
            val slot = slot<String>()
            every { productRepositoryCustom.findAllProduct(null, capture(slot), any()) } returns PageImpl(
                listOf(
                    productResponse4
                )
            )
            Then("상품 페이지 조회 성공") {
                val result = productService.findAllProductResponseByProductNameAndCategoryAndPageable(
                    null,
                    null,
                    mockk()
                )
                result.content[0] shouldBe productResponse4
                slot.captured shouldBe "ALL"
                result.size shouldBe 1
            }
        }
        When("잘못된 카테고리가 주어졌을때") {
            val productCategory5 = "WRONG"
            Then("상품 조회 실패 - ${ProductErrorCode.INVALID_CATEGORY} 예외 발생") {
                val result = shouldThrow<RestApiException> {
                    productService.findAllProductResponseByProductNameAndCategoryAndPageable(
                        null,
                        productCategory5,
                        mockk()
                    )
                }
                result.baseErrorCode shouldBe ProductErrorCode.INVALID_CATEGORY
            }
        }
    }
    Given("상품 아이디로 상품 조회 요청") {
        val productId = 1L
        val product1 = mockk<Product>()
        every { product1.id } returns productId
        When("상품 아이디가 정상적으로 주어졌을때") {
            Then("조회 성공") {
                every { productRepository.findById(any()) } returns Optional.of(product1)
                val result = productService.findById(productId)
                result.id shouldBe productId
            }
            Then("조회 실패 - ${ProductErrorCode.NOT_FOUND_PRODUCT} 예외 발생") {
                every { productRepository.findById(any()) } returns Optional.empty()
                val result = shouldThrow<RestApiException> { productService.findById(productId) }
                result.baseErrorCode shouldBe ProductErrorCode.NOT_FOUND_PRODUCT
            }
        }
    }
    Given("상품 아이디 리스트로 상품 리스트 조회 요청") {
        val productId = 1L
        val product1 = mockk<Product>()
        every { product1.id } returns productId
        val productIdList = listOf(productId)
        val productList = listOf(product1)
        When("상품 아이디 리스트가 비어있지 않을 때") {
            every { productRepository.findAllByIdIn(any()) } returns productList
            Then("조회 성공") {
                val result = productService.findAllByIdIn(productIdList)
                result.size shouldBe productList.size
                result[0].id shouldBe product1.id
            }
        }
        When("상품 아이디 리스트가 비어져 있을 때") {
            every { productRepository.findAllByIdIn(emptyList()) } returns emptyList()
            Then("빈 리스트 반환") {
                val result = productService.findAllByIdIn(emptyList())
                result.size shouldBe 0
            }
        }
    }
    Given("상품 저장 요청") {
        val product1 = mockk<Product>(relaxed = true)
        val slot = slot<Product>()
        When("저장할 상품이 주어졌을 때") {
            every { productRepository.save(capture(slot)) } returns product1
            Then("상품 저장 성공") {
                val result = productService.save(product1)
                val slotResult = slot.captured
                result.productName shouldBe product1.productName
                slotResult.productName shouldBe product1.productName
            }
        }
    }
})
