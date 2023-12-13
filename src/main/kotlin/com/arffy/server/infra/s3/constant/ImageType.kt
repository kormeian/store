package com.arffy.server.infra.s3.constant

import com.arffy.server.global.exception.RestApiException
import com.arffy.server.infra.s3.exception.ImageErrorCode
import java.util.*
import java.util.function.Supplier

enum class ImageType(
    val imageQuantity: Int = 0
) {
    NOTICE(3),
    QNA(3),
    PRODUCT(20),
    QNA_COMMENT(3);


    companion object {
        /**
         * string to iamge type
         *
         * @param imageType 이미지 타입
         * @return 이미지 타입
         */
        fun from(imageType: String?): ImageType {
            return Arrays.stream<ImageType>(values())
                .filter { type: ImageType -> type.name.equals(imageType, ignoreCase = true) }
                .findFirst()
                .orElseThrow<RuntimeException>(Supplier<RuntimeException> {
                    RestApiException(
                        ImageErrorCode.BAD_REQUEST,
                        "지원하지 않는 카테고리 타입니다."
                    )
                })
        }
    }
}
