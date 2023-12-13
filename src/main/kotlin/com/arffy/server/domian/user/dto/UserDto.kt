package com.arffy.server.domian.user.dto

import com.arffy.server.domian.user.entity.User
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

class UserModifyRequest(
    @field:Schema(description = "사용자 이름", example = "홍길동")
    val name: String?,

    @field:Schema(description = "사용자 전화번호", example = "+82 10-1234-5678")
    val phoneNumber: String?,

    @field:Schema(description = "사용자 주소", example = "서울특별시 강남구")
    val address: String?,

    @field:Schema(description = "사용자 상세 주소", example = "역삼동 123-456")
    val addressDetail: String?,

    @field:Schema(description = "사용자 우편번호", example = "12345")
    val postCode: String?,
) {
    companion object {
        fun toEntity(
            user: User,
            userModifyRequest: UserModifyRequest
        ): User {
            user.name = userModifyRequest.name!!
            user.phoneNumber = userModifyRequest.phoneNumber!!.replace("-", "").replace("+82 ", "0")
            user.address = userModifyRequest.address
            user.addressDetail = userModifyRequest.addressDetail
            user.postCode = userModifyRequest.postCode
            return user
        }
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class UserResponse(
    @field:Schema(description = "사용자 이름", example = "홍길동")
    val name: String,

    @field:Schema(description = "사용자 전화번호", example = "+82 10-1234-5678", nullable = true)
    val phoneNumber: String? = null,

    @field:Schema(description = "사용자 이메일", example = "asdf1234@gmail.com")
    val email: String,

    @field:Schema(description = "사용자 주소", example = "서울특별시 강남구", nullable = true)
    val address: String? = null,

    @field:Schema(description = "사용자 상세 주소", example = "역삼동 123-456", nullable = true)
    val addressDetail: String? = null,

    @field:Schema(description = "사용자 우편번호", example = "12345", nullable = true)
    val postCode: String?,
) {
    companion object {
        fun from(user: User): UserResponse = UserResponse(
            name = user.name,
            phoneNumber = user.phoneNumber,
            email = user.email,
            address = user.address,
            addressDetail = user.addressDetail,
            postCode = user.postCode,
        )
    }
}