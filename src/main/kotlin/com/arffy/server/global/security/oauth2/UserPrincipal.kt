package com.arffy.server.global.security.oauth2

import com.arffy.server.domian.user.entity.User
import com.arffy.server.domian.user.exception.UserErrorCode
import com.arffy.server.global.exception.RestApiException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class UserPrincipal(
    private val id: Long,
    private val email: String,
    private val authorities: MutableCollection<out GrantedAuthority>,
) : OAuth2User,
    UserDetails {
    private var attributes: MutableMap<String, Any>? = null

    override fun getName(): String {
        return email
    }

    override fun getAttributes(): MutableMap<String, Any>? {
        return attributes
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun getHasInfo(): Boolean {
        return attributes?.get("hasInfo") as Boolean
    }

    companion object {
        fun create(
            user: User,
            attributes: MutableMap<String, Any>
        ): UserPrincipal {
            val authorities: MutableCollection<GrantedAuthority> =
                mutableListOf(SimpleGrantedAuthority(user.role.name))
            val userPrincipal = UserPrincipal(
                user.id ?: throw RestApiException(UserErrorCode.NOT_FOUND_USER),
                user.email,
                authorities
            )
            userPrincipal.attributes = attributes
            return userPrincipal
        }
    }

}