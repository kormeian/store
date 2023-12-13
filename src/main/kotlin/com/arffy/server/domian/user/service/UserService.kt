package com.arffy.server.domian.user.service

import com.arffy.server.domian.user.dto.UserModifyRequest
import com.arffy.server.domian.user.dto.UserResponse
import com.arffy.server.domian.user.entity.User

interface UserService {
    fun isAdmin(
        user: User
    ): Boolean

    fun updateByUserModifyRequest(
        user: User,
        userModifyRequest: UserModifyRequest?,
    ): Long

    fun findUserResponseByUser(
        user: User
    ): UserResponse

    fun findByEmail(
        email: String
    ): User

    fun findById(
        userId: Long
    ): User

    fun save(
        user: User
    ): User

    fun deleteById(
        userId: Long
    )
}