package com.arffy.server.domian.product.repository

import com.arffy.server.domian.product.ProductCategory
import com.arffy.server.domian.product.QProduct.product
import com.arffy.server.domian.product.dto.ProductResponse
import com.arffy.server.domian.product.dto.QProductResponse
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ProductRepositoryCustom {

    override fun findAllProduct(
        productName: String?,
        category: String,
        pageable: Pageable
    ): Page<ProductResponse> {
        val query = jpaQueryFactory.select(
            QProductResponse(
                product.id,
                product.thumbnail,
                product.thumbnailVersion,
                product.productName,
                product.price,
                product.discountRate,
                product.discountPrice,
                product.quantity,
            )
        ).from(product)

        val count: JPQLQuery<ProductResponse> = if (category != "ALL") {
            query.where(
                isNotDeleted().and(
                    categoryEq(category).and(
                        containsProductName(productName)
                    )
                )
            )
        } else {
            query.where(
                isNotDeleted().and(
                    containsProductName(productName)
                )
            )
        }

        query.orderBy(
            product.quantity.desc(),
            product.createdAt.desc(),
            product.soldOutAt.desc()
        )

        val products = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(products, pageable, count.fetchCount())
    }

    private fun containsProductName(
        productName: String?
    ): BooleanExpression {
        return product.productName.contains(productName ?: "")
    }

    private fun categoryEq(
        category: String
    ): BooleanExpression {
        return product.category.eq(ProductCategory.valueOf(category))
    }

    private fun isNotDeleted(): BooleanExpression {
        return product.deleteYn.eq(false)
    }
}
