package com.arffy.server.global.security.service

import com.arffy.server.domian.user.entity.AuthProvider
import com.arffy.server.domian.user.entity.Role
import com.arffy.server.domian.user.entity.User
import com.arffy.server.domian.user.reposiroty.UserRepository
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.global.security.exception.SecurityErrorCode
import com.arffy.server.global.security.oauth2.OAuth2UserInfo
import com.arffy.server.global.security.oauth2.OAuth2UserInfoFactory
import com.arffy.server.global.security.oauth2.UserPrincipal
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.util.*

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2UserService = DefaultOAuth2UserService()
        val oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest)
        return processOAuth2User(oAuth2UserRequest, oAuth2User)
    }

    protected fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        //OAuth2 로그인 플랫폼 구분
        val authProvider =
            AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId.uppercase(Locale.getDefault()))
        val oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            authProvider,
            oAuth2User.attributes
        )
        if (!StringUtils.hasText(oAuth2UserInfo.email)) {
            throw RestApiException(SecurityErrorCode.OAUTH2_EMAIL_NOT_FOUND)
        }
        var user: User? = userRepository.findByEmail(oAuth2UserInfo.email)

        if (user == null) {
            user = registerUser(
                authProvider,
                oAuth2UserInfo
            )
            oAuth2UserInfo.attributes["hasInfo"] = false
        } else {
            if (user.authProvider != authProvider) {
                throw RestApiException(SecurityErrorCode.INVALID_AUTH_PROVIDER)
            }
            updateUser(user, oAuth2UserInfo)
            oAuth2UserInfo.attributes["hasInfo"] =
                !(user.address.isNullOrBlank() || user.addressDetail.isNullOrBlank() ||
                        user.postCode.isNullOrBlank() || user.phoneNumber.isNullOrBlank())
        }

        return UserPrincipal.create(user, oAuth2UserInfo.attributes)
    }

    private fun registerUser(
        authProvider: AuthProvider,
        oAuth2UserInfo: OAuth2UserInfo
    ): User {
        val user = User(
            authProvider = authProvider,
            oauth2Id = oAuth2UserInfo.oAuth2Id,
            name = oAuth2UserInfo.name,
            email = oAuth2UserInfo.email,
            role = Role.ROLE_USER
        )

        return userRepository.save(user)
    }

    private fun updateUser(user: User, oAuth2UserInfo: OAuth2UserInfo): User {
        return userRepository.save(user.update(oAuth2UserInfo))
    }
}