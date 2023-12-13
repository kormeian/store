package com.arffy.server.infra.s3.exception

import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.RestApiException


class ImageDeleteException : RestApiException {
    val imageDeleteErrorReason: BaseErrorCode

    constructor(imageDeleteErrorReason: BaseErrorCode) : super(imageDeleteErrorReason) {
        this.imageDeleteErrorReason = imageDeleteErrorReason
    }

    constructor(imageDeleteErrorReason: BaseErrorCode, message: String) : super(imageDeleteErrorReason, message) {
        this.imageDeleteErrorReason = imageDeleteErrorReason
    }
}
