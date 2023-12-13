package com.arffy.server.infra.s3.dto

import com.arffy.server.global.exception.RestApiException
import com.arffy.server.infra.s3.constant.ImageType
import com.arffy.server.infra.s3.entity.ImageEntity
import com.arffy.server.infra.s3.exception.ImageErrorCode
import io.swagger.v3.oas.annotations.media.Schema

class ImageDto(
    @field:Schema(description = "이미지 ID", example = "1")
    val imageId: Long,

    @field:Schema(description = "이미지 URL", example = "https://arffy-image.s3.ap-northeast-2.amazonaws.com/1.png")
    val imageUrl: String,

    @field:Schema(description = "구분 ID", example = "1")
    val divideId: Long,

    @field:Schema(
        description = "이미지 유형",
        example = "PRODUCT",
        allowableValues = ["NOTICE", "PRODUCT", "QNA", "QNA_COMMENT"]
    )
    val imageType: ImageType,
) {
    companion object {
        fun from(imageEntity: ImageEntity): ImageDto {
            return ImageDto(
                imageEntity.id
                    ?: throw RestApiException(ImageErrorCode.NOT_FOUND_IMAGE_ID),
                imageEntity.imageUrl,
                imageEntity.divideId,
                imageEntity.imageType
            )
        }
    }
}