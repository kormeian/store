package com.arffy.server.domian.user.reposiroty

import com.arffy.server.domian.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(
        email: String
    ): User?

}