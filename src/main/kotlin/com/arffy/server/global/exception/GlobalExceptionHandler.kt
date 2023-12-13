package com.arffy.server.global.exception

import com.arffy.server.global.exception.ErrorResponse.Companion.makeErrorResponse
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(RestApiException::class)
    fun handlePaymentException(e: RestApiException): ResponseEntity<Any> {
        log.error(e) { "${e.baseErrorCode.errorReason.message} is occurred" }
        return handleExceptionInternal(e.baseErrorCode.errorReason)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<Any> {
        log.error(e) { "Exception is occurred" }
        return handleExceptionInternal(GlobalErrorCode.INTERNAL_SERVER_GLOBAL_ERROR.errorReason)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<Any> {
        log.error(e) { "MethodArgumentNotValidException is occurred" }
        var message = ""

        for (error in e.bindingResult.fieldErrors) {
            message += error.field + " : " + error.defaultMessage + ", "
        }
        return handleExceptionInternal(GlobalErrorCode.VALIDATION_ERROR.errorReason, message)
    }

    private fun handleExceptionInternal(errorReason: ErrorReason): ResponseEntity<Any> {
        return ResponseEntity.status(errorReason.httpStatus)
            .body<Any>(makeErrorResponse(errorReason))
    }

    private fun handleExceptionInternal(errorReason: ErrorReason, message: String): ResponseEntity<Any> {
        return ResponseEntity.status(errorReason.httpStatus)
            .body<Any>(makeErrorResponse(errorReason, message))
    }


}

class ErrorResponse(
    val code: String,
    val message: String
) {
    companion object {
        fun makeErrorResponse(errorReason: ErrorReason): ErrorResponse {
            return ErrorResponse(errorReason.messageCode.toString(), errorReason.message)
        }

        fun makeErrorResponse(errorReason: ErrorReason, message: String): ErrorResponse {
            return ErrorResponse(errorReason.messageCode.toString(), message)
        }
    }
}

