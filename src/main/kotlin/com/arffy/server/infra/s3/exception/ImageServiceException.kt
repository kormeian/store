package com.arffy.server.infra.s3.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.RestApiException


class ImageServiceException : RestApiException {
    val imageServiceErrorReason: BaseErrorCode

    constructor(imageSaveErrorReason: BaseErrorCode) : super(imageSaveErrorReason) {
        this.imageServiceErrorReason = imageSaveErrorReason
    }

    constructor(imageSaveErrorReason: BaseErrorCode, message: String) : super(imageSaveErrorReason, message) {
        this.imageServiceErrorReason = imageSaveErrorReason
    }
}
