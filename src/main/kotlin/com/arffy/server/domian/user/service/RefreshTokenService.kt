package com.arffy.server.domian.user.service

import com.arffy.server.domian.user.entity.RefreshToken

interface RefreshTokenService {
    fun findByEmail(
        email: String
    ): RefreshToken?

    fun deleteById(
        id: Long
    )

    fun save(
        refreshToken: RefreshToken
    ): RefreshToken

    fun deleteByAccessToken(
        accessToken: String
    ): String

    fun deleteByEmail(
        email: String
    ): String
}