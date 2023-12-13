package com.arffy.server.infra.s3.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import org.springframework.http.HttpStatus

enum class ImageErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int,
) : BaseErrorCode {
    NOT_FOUND_IMAGE(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다.", 11101),
    MISMATCH_FILE_TYPE(HttpStatus.BAD_REQUEST, "파일의 확장자가 일치하지 않습니다.", 11102),
    INVALID_FORMAT_FILE(HttpStatus.BAD_REQUEST, "파일의 확장자가 올바르지 않습니다.", 11103),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", 11104),
    REQUIRE_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "이미지 타입은 필수입니다.", 11105),
    NOT_FOUND_UPLOAD_IMAGE(HttpStatus.NOT_FOUND, "업로드된 이미지를 찾을 수 없습니다.", 11106),
    FAILURE_UPLOAD_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패하였습니다.", 11107),
    TOO_MANY_UPLOAD_IMAGE(
        HttpStatus.BAD_REQUEST,
        "저장된 이미지 %s개 + 수정할 이미지 %s개 - 삭제할 이미지 %s개 의 개수가 %s 이미지 최대 저장 개수 %s개 보다 많습니다.", 11108
    ),
    FAILURE_DELETE_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제에 실패하였습니다.", 11109),
    NOT_FOUND_DELETE_IMAGE(HttpStatus.NOT_FOUND, "삭제할 이미지를 찾을 수 없습니다.", 11110),
    NOT_FOUND_GET_IMAGE(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다.", 11111),
    FAILURE_GET_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지를 가져오는데 실패하였습니다.", 11112),
    NOT_FOUND_OBJECT(HttpStatus.NOT_FOUND, "해당 객체를 찾을 수 없습니다.", 11113),
    NOT_FOUND_IMAGE_ID(HttpStatus.NOT_FOUND, "이미지 아이디를 찾을 수 없습니다.", 11114),
    REQUIRE_IMAGE_ID(HttpStatus.BAD_REQUEST, "이미지 아이디는 필수입니다.", 11115),
    REQUIRE_IMAGE_URL(HttpStatus.BAD_REQUEST, "이미지 URL은 필수입니다.", 11116),
    REQUIRE_IMAGE(HttpStatus.BAD_REQUEST, "이미지는 필수입니다.", 11117),
    NOT_EQUAL_IMAGE_AND_IMAGE_URL_QUANTITY(HttpStatus.BAD_REQUEST, "이미지와 이미지 URL의 개수가 일치하지 않습니다.", 11118),
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