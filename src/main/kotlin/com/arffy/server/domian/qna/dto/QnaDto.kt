package com.arffy.server.domian.qna.dto

import com.arffy.server.domian.qna.entity.Qna
import com.arffy.server.domian.qna.entity.QnaComment
import com.arffy.server.domian.qna.entity.QnaType
import com.arffy.server.infra.s3.dto.ImageDto
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.time.format.DateTimeFormatter

data class QnaRequest(
    @field:Schema(description = "제목", example = "제목입니다.")
    val title: String?,

    @field:Schema(description = "내용", example = "내용입니다.")
    val content: String?,

    @field:Schema(description = "상품 번호", example = "1", nullable = true)
    val productId: Long?,

    @field:Schema(description = "문의 유형", example = "PRODUCT", allowableValues = ["PRODUCT", "DELIVERY", "ETC"])
    val qnaType: String?,

    @field:Schema(
        description = "이미지 유형",
        example = "QNA",
        allowableValues = ["NOTICE", "PRODUCT", "QNA", "QNA_COMMENT"]
    )
    val imageType: String?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
class QnaDetailResponseDto(
    val qna: Qna,
    val product: Product? = null,
) {
    class Qna(
        @field:Schema(description = "QNA ID", example = "1")
        val qnaId: Long,
        @field:Schema(description = "제목", example = "제목입니다.")
        val title: String,
        @field:Schema(description = "내용", example = "내용입니다.")
        val content: String,
        @field:Schema(description = "답변 여부", example = "true")
        val isAnswered: Boolean,
        @field:Schema(description = "생성일", example = "2021-01-01 00:00")
        val createdAt: String,
        @field:Schema(description = "유저 ID", example = "1")
        val userId: Long,
        @field:Schema(description = "유저 이메일", example = "asd1234@kakao.com")
        val email: String,
        @field:Schema(description = "이미지 DTO List")
        val imageList: List<ImageDto>? = null,
    ) {
        companion object {
            fun from(
                qna: com.arffy.server.domian.qna.entity.Qna,
                imageDtoList: List<ImageDto>? = null,
            ): Qna {
                return Qna(
                    qnaId = qna.id!!,
                    title = qna.title,
                    content = qna.content,
                    isAnswered = qna.isAnswered,
                    createdAt = qna.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    userId = qna.user.id!!,
                    email = qna.user.email,
                    imageList = imageDtoList,
                )
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Product(
        @field:Schema(description = "상품 ID", example = "1")
        val productId: Long,
        @field:Schema(description = "상품 이름", example = "상품입니다.")
        val productName: String,
        @field:Schema(description = "상품 가격", example = "10000")
        val price: Int,
        @field:Schema(description = "상품 할인 가격", example = "1000")
        val discountPrice: Int,
        @field:Schema(description = "상품 할인율", example = "10")
        val discountRate: Int,
        @field:Schema(description = "상품 썸네일", example = "AwsS3Url/Product/asd.jpg")
        val thumbnail: String? = null,
        @field:Schema(description = "상품 썸네일 버전")
        val thumbnailVersion: Int? = null,
    ) {
        companion object {
            fun from(
                product: com.arffy.server.domian.product.Product,
            ): Product {
                return Product(
                    productId = product.id!!,
                    productName = product.productName,
                    price = product.price,
                    discountPrice = product.discountPrice,
                    discountRate = product.discountRate,
                    thumbnail = product.thumbnail,
                    thumbnailVersion = product.thumbnailVersion,
                )
            }
        }
    }

    companion object {
        fun from(
            qna: com.arffy.server.domian.qna.entity.Qna,
            product: com.arffy.server.domian.product.Product? = null,
            imageDtoList: List<ImageDto>? = null,
        ): QnaDetailResponseDto {
            return QnaDetailResponseDto(
                qna = Qna.from(qna, imageDtoList),
                product = product?.let { Product.from(it) }
            )
        }
    }
}

class QnaResponse(
    @field:Schema(description = "QNA ID", example = "1")
    val qnaId: Long,
    @field:Schema(description = "제목", example = "제목입니다.")
    val title: String,
    @field:Schema(description = "작성자 이름", example = "홍길동")
    val name: String,
    @field:Schema(description = "QNA 타입", example = "PRODUCT")
    val qnaType: QnaType,
    @field:Schema(description = "생성일", example = "2021-01-01 00:00")
    val createdAt: String,
    @field:Schema(description = "답변 여부", example = "true")
    val isAnswered: Boolean,
    @field:Schema(description = "내가 작성한 QNA 여부", example = "true")
    val isMine: Boolean,
) {

    companion object {
        fun from(
            qna: Qna,
            isMine: Boolean
        ): QnaResponse {
            return QnaResponse(
                qnaId = qna.id!!,
                title = qna.title,
                name = qna.user.name,
                qnaType = qna.qnaType,
                createdAt = qna.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                isAnswered = qna.isAnswered,
                isMine = isMine
            )
        }
    }

}

class QnaCommentResponse(
    @field:Schema(description = "QNA 댓글 ID", example = "1")
    val qnaCommentId: Long,

    @field:Schema(description = "QNA ID", example = "1")
    val qnaId: Long,

    @field:Schema(description = "댓글 내용", example = "댓글입니다.")
    val content: String,

    @field:Schema(description = "생성일", example = "2021-01-01 00:00")
    val createdAt: String,

    @field:Schema(description = "어드민 ID", example = "1")
    val userId: Long,

    @field:Schema(description = "어드민 이메일", example = "asdf1234@asdf.com")
    val email: String,

    @field:Schema(description = "이미지 DTO List")
    var imageList: List<ImageDto>? = null,
) {
    companion object {
        fun from(qnaComment: QnaComment): QnaCommentResponse {
            return QnaCommentResponse(
                qnaCommentId = qnaComment.id!!,
                content = qnaComment.comment,
                createdAt = qnaComment.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                userId = qnaComment.user.id!!,
                email = qnaComment.user.email,
                qnaId = qnaComment.qna.id!!,
            )
        }
    }
}
