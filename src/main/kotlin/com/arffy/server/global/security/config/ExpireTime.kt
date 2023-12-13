package com.arffy.server.global.security.config


object ExpireTime {
    const val ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L

    //    const val ACCESS_TOKEN_EXPIRE_TIME = 1000L
    const val REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L
}