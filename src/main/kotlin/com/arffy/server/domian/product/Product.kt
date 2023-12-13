package com.arffy.server.domian.product

import com.arffy.server.domian.BaseEntity
import com.arffy.server.domian.product.exception.ProductErrorCode
import com.arffy.server.global.exception.RestApiException
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
class Product(

    @Column(nullable = false, length = 100)
    var productName: String,

    @Column(nullable = false)
    var price: Int,

    @Column(nullable = false)
    var discountPrice: Int = 0,

    @Column(nullable = false)
    var discountRate: Int,

    @Column(nullable = false)
    var period: String,

    @Column(nullable = false)
    var country: String,

    var width: String?,
    var depth: String?,
    var height: String?,
    var minLineHeight: String?,
    var maxLineHeight: String?,

    @Column(nullable = false)
    var material: String,

    @Column(nullable = false)
    var status: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var quantity: Int,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var category: ProductCategory,

    var thumbnail: String? = null,

    @Column(nullable = false)
    val thumbnailVersion: Int,

    @Column(nullable = false)
    var deleteYn: Boolean = false,

    var soldOutAt: LocalDateTime? = null
) : BaseEntity()

enum class ProductCategory {
    PENDANT,
    TABLE,
    WALL,
    ETC
    ;

    companion object {
        fun from(category: String?): ProductCategory {
            return values().find { it.name == category } ?: throw RestApiException(ProductErrorCode.INVALID_CATEGORY)
        }
    }
}