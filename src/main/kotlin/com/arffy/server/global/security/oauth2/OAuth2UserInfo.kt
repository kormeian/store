package com.arffy.server.global.security.oauth2


abstract class OAuth2UserInfo(
    var attributes: MutableMap<String, Any>
) {
    abstract val oAuth2Id: String
    abstract val email: String
    abstract val name: String
}