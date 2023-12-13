package com.arffy.server.domian.payment.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema


@JsonInclude(JsonInclude.Include.NON_NULL)
class ConfirmProcessResponse(
    val reason: String? = null,
)


class ConfirmRequest(
    val imp_uid: String,
    val merchant_uid: String,
    val amount: Int,
)

class VerifyRequest(
    @field:Schema(description = "포트원 고유번호", example = "imp_uid")
    val imp_uid: String?,
    @field:Schema(description = "주문 번호", example = "ORDyyyyMMdd_000001")
    val merchant_uid: String?,
    @field:Schema(description = "결제 상태", example = "PAID")
    val status: String?,
)