package com.arffy.server.global.exception

import org.springframework.http.HttpStatus
import java.util.*
import kotlin.reflect.KClass


open class RestApiException(
    val baseErrorCode: BaseErrorCode,
    val errorMessage: String = baseErrorCode.errorReason.message
) : RuntimeException()

class ErrorReason(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
)


enum class GlobalErrorCode(
    val httpStatus: HttpStatus,
    val message: String,
    val messageCode: Int
) : BaseErrorCode {
    INTERNAL_SERVER_GLOBAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다.", 50000),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "잘못된 입력입니다.", 10001),
    UNSUPPORTED_ENCODING_ERROR(HttpStatus.BAD_REQUEST, "지원하지 않는 인코딩입니다.", 10003),
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


interface BaseErrorCode {
    val errorReason: ErrorReason

    @get:Throws(NoSuchFieldException::class)
    val explainError: String
}

//@Target(ElementType.METHOD)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiErrorCodeExample(val value: KClass<out BaseErrorCode>)