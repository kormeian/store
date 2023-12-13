package com.arffy.server.global.security.oauth2

class KakaoOAuth2User(attributes: Map<String?, Any?>) :
    OAuth2UserInfo(attributes["kakao_account"] as MutableMap<String, Any>) {
    private val id: Long

    init {
        id = attributes["id"] as Long
    }

    override val oAuth2Id: String
        get() = id.toString()
    override val email: String
        get() = attributes["email"] as String
    override val name: String
        get() = (attributes["profile"] as Map<*, *>?)!!["nickname"] as String
//        get() = attributes["name"] as String
}