package com.arffy.server.infra.s3.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.RestApiException


class ImageSaveException : RestApiException {
    val imageSaveErrorReason: BaseErrorCode

    constructor(imageSaveErrorReason: BaseErrorCode) : super(imageSaveErrorReason) {
        this.imageSaveErrorReason = imageSaveErrorReason
    }

    constructor(imageSaveErrorReason: BaseErrorCode, message: String) : super(imageSaveErrorReason, message) {
        this.imageSaveErrorReason = imageSaveErrorReason
    }

}
