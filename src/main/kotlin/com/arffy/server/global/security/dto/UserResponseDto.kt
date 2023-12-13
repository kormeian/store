package com.arffy.server.global.security.dto


class UserResponseDto {
    class TokenInfo(
        val grantType: String,
        val accessToken: String,
        val accessTokenExpirationTime: Long,
        val refreshToken: String,
        val refreshTokenExpirationTime: Long,
    )
}