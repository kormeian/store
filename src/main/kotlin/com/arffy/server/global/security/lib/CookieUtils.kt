package com.arffy.server.global.security.lib

import org.springframework.util.SerializationUtils
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object CookieUtils {
    fun getCookie(
        request: HttpServletRequest,
        name: String
    ): Optional<Cookie> {
        val cookies = request.cookies
        if (cookies != null && cookies.isNotEmpty()) {
            for (cookie in cookies) {
                if (cookie.name == name) {
                    return Optional.of(cookie)
                }
            }
        }
        return Optional.empty()
    }

    fun readServletCookie(request: HttpServletRequest, name: String): Optional<String> {
        return Arrays.stream(request.cookies)
            .filter { cookie: Cookie -> name == cookie.name }
            .map { obj: Cookie -> obj.value }
            .findAny()
    }

    fun addCookie(
        response: HttpServletResponse, name: String?,
        value: String?, maxAge: Int
    ) {
        val cookie = Cookie(name, value)
        cookie.path = "/"
        cookie.isHttpOnly = false
        cookie.maxAge = maxAge
        response.addCookie(cookie)
    }

    fun deleteCookie(
        request: HttpServletRequest,
        response: HttpServletResponse, name: String
    ) {
        val cookies = request.cookies
        if (cookies != null && cookies.isNotEmpty()) {
            for (cookie in cookies) {
                if (cookie.name == name) {
                    cookie.value = ""
                    cookie.path = "/"
                    cookie.maxAge = 0
                    response.addCookie(cookie)
                }
            }
        }
    }

    fun serialize(`object`: Any?): String {
        return Base64.getUrlEncoder()
            .encodeToString(SerializationUtils.serialize(`object`))
    }

    fun <T> deserialize(cookie: Cookie, cls: Class<T>): T {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.value)))
    }
}