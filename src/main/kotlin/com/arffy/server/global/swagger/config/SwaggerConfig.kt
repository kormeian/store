package com.arffy.server.global.swagger.config

import com.arffy.server.global.exception.ApiErrorCodeExample
import com.arffy.server.global.exception.BaseErrorCode
import com.arffy.server.global.exception.ErrorReason
import com.arffy.server.global.exception.ErrorResponse
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors.groupingBy
import kotlin.reflect.KClass


@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .components(Components())
        .info(apiInfo())

    @Bean
    fun customize(): OperationCustomizer? {
        return OperationCustomizer { operation: Operation, handlerMethod: HandlerMethod ->
            val apiErrorCodeExample = handlerMethod.getMethodAnnotation(
                ApiErrorCodeExample::class.java
            )
            // ApiErrorCodeExample 어노테이션 단 메소드 적용
            if (apiErrorCodeExample != null) {
                generateErrorCodeResponseExample(operation, apiErrorCodeExample.value)
            }
            operation
        }
    }

    private fun generateErrorCodeResponseExample(
        operation: Operation, type: KClass<out BaseErrorCode>
    ) {
        val responses: ApiResponses = operation.responses
        val errorCodes: Array<BaseErrorCode> = type.java.enumConstants as Array<BaseErrorCode>
        val statusWithExampleHolders: Map<Int, List<ExampleHolder>> = Arrays.stream(errorCodes)
            .map { baseErrorCode ->
                try {
                    val errorReason: ErrorReason = baseErrorCode.errorReason
                    return@map ExampleHolder(
                        getSwaggerExample(
                            baseErrorCode.explainError,
                            errorReason
                        ),
                        errorReason.message,
                        errorReason.httpStatus.value()
                    )
                } catch (e: NoSuchFieldException) {
                    throw RuntimeException(e)
                }
            }
            .collect(groupingBy(ExampleHolder::code))
        addExamplesToResponses(responses, statusWithExampleHolders)
    }


    class ExampleHolder(
        val holder: Example? = null,
        val name: String? = null,
        val code: Int = 0
    )

    private fun getSwaggerExample(value: String, errorReason: ErrorReason): Example {
        val errorResponse = ErrorResponse(errorReason.messageCode.toString(), errorReason.message)
        val example = Example()
        example.description(value)
        example.value = errorResponse
        return example
    }

    private fun addExamplesToResponses(
        responses: ApiResponses, statusWithExampleHolders: Map<Int, List<ExampleHolder>>
    ) {
        statusWithExampleHolders.forEach { (status: Int, v: List<ExampleHolder>) ->
            val content = Content()
            val mediaType = MediaType()
            val apiResponse = ApiResponse()
            v.forEach(
                Consumer { exampleHolder: ExampleHolder ->
                    mediaType.addExamples(
                        exampleHolder.name, exampleHolder.holder
                    )
                })
            content.addMediaType("application/json", mediaType)
            apiResponse.content = content
            responses.addApiResponse(status.toString(), apiResponse)
        }
    }

    private fun apiInfo() = Info()
        .title("arffy")
        .description("arffy api docs")
        .version("1.0.0")
}