package com.arffy.server.domian.qna.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus

enum class QnaErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    FAILED_TO_CREATE_QNA(HttpStatus.INTERNAL_SERVER_ERROR, "QnA 생성에 실패하였습니다.", 10301),
    NOT_FOUND_QNA(HttpStatus.NOT_FOUND, "QnA를 찾을 수 없습니다.", 10302),
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "접근권한이 없습니다.", 10303),
    IMAGE_TYPE_IS_NOT_MATCH(HttpStatus.BAD_REQUEST, "이미지 타입이 맞지 않습니다.", 10304),
    IMAGE_QUANTITY_IS_TOO_MANY(HttpStatus.BAD_REQUEST, "이미지가 너무 많습니다. 3개 이하로 보내주세요", 10305),
    REQUIRED_QNA_REQUEST(HttpStatus.BAD_REQUEST, "QnA 요청 정보는 필수입니다.", 10306),
    REQUIRED_QNA_TITLE(HttpStatus.BAD_REQUEST, "QnA 제목은 필수입니다.", 10307),
    REQUIRED_QNA_CONTENT(HttpStatus.BAD_REQUEST, "QnA 내용은 필수입니다.", 10308),
    REQUIRED_QNA_TYPE(HttpStatus.BAD_REQUEST, "QnA 타입은 필수입니다.", 10309),
    REQUIRED_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "이미지 타입은 필수입니다.", 10310),
    REQUIRED_QNA_ID(HttpStatus.BAD_REQUEST, "QnA 아이디는 필수입니다.", 10311),
    REQUIRED_PRODUCT_ID(HttpStatus.BAD_REQUEST, "상품 아이디는 필수입니다.", 10312),
    QNA_TYPE_IS_NOT_MATCHED(HttpStatus.BAD_REQUEST, "QnA 타입이 맞지 않습니다.", 10313),
    ;

    override val errorReason: ErrorReason
        get() = ErrorReason(
            httpStatus = httpStatus,
            message = message,
            messageCode = messageCode
        )
    override val explainError: String
        get() = this.message
}