package com.arffy.server.domian.user.entity

import com.arffy.server.domian.BaseEntity
import com.arffy.server.global.jpaConverter.ColumnEncryptor
import com.arffy.server.global.security.oauth2.OAuth2UserInfo
import javax.persistence.*

@Entity
class User(

    @Column(nullable = false, length = 50)
    @Convert(converter = ColumnEncryptor::class)
    var name: String,

    @Column(nullable = false, length = 255)
    @Convert(converter = ColumnEncryptor::class)
    var email: String,

    @Column(length = 255)
    @Convert(converter = ColumnEncryptor::class)
    var phoneNumber: String? = null,

    @Convert(converter = ColumnEncryptor::class)
    var address: String? = null,

    @Convert(converter = ColumnEncryptor::class)
    var addressDetail: String? = null,

    @Convert(converter = ColumnEncryptor::class)
    var postCode: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val role: Role,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val authProvider: AuthProvider,

    @Column(nullable = false, length = 20)
    var oauth2Id: String,

    ) : BaseEntity() {
    fun update(oAuth2UserInfo: OAuth2UserInfo): User {
        oauth2Id = oAuth2UserInfo.oAuth2Id
        return this
    }

    fun hasInfo(): Boolean {
        return !(address.isNullOrBlank() || addressDetail.isNullOrBlank() || postCode.isNullOrBlank())
    }
}

enum class Role(
    val description: String
) {
    ROLE_ADMIN("관리자"),
    ROLE_USER("사용자"),
    ROLE_GUEST("손님"),
}

enum class AuthProvider {
    KAKAO
}

