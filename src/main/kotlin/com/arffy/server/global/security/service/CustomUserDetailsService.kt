package com.arffy.server.global.security.service

import com.arffy.server.domian.user.entity.User
import com.arffy.server.domian.user.reposiroty.UserRepository
import mu.KotlinLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("Can not find username.")
        log.info { "User: $user" }
        return UserAdapter(user)
    }
}


class UserAdapter(
    val user: User
) :
    org.springframework.security.core.userdetails.User(
        user.email,
        "",
        setOf(SimpleGrantedAuthority(user.role.name))
    )