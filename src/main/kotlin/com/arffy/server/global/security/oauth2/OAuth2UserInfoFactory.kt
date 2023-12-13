package com.arffy.server.global.security.oauth2

import com.arffy.server.domian.user.entity.AuthProvider
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.global.security.exception.SecurityErrorCode

object OAuth2UserInfoFactory {
    fun getOAuth2UserInfo(
        authProvider: AuthProvider?,
        attributes: Map<String?, Any?>
    ): OAuth2UserInfo {
        return when (authProvider) {
//            AuthProvider.GOOGLE -> GoogleOAuth2User(attributes)
//            AuthProvider.NAVER -> NaverOAuth2User(attributes)
            AuthProvider.KAKAO -> KakaoOAuth2User(attributes)
            else -> throw RestApiException(SecurityErrorCode.INVALID_AUTH_PROVIDER)
        }
    }
}