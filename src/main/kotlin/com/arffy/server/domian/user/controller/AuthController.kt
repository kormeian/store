package com.arffy.server.domian.user.controller

import com.arffy.server.domian.user.facade.RefreshTokenFacade
import com.arffy.server.domian.user.service.RefreshTokenServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "인증 API")
class AuthController(
    private val refreshTokenService: RefreshTokenServiceImpl,
    private val refreshTokenFacade: RefreshTokenFacade,
) {

    @PostMapping("/refresh")
    @Parameter(name = "token", description = "액세스 토큰", example = "eyJhbGciOi~~~")
    @Operation(summary = "액세스 토큰 갱신", description = "액세스 토큰 갱신")
    fun refreshAccessToken(
        @RequestParam
        token: String
    ): ResponseEntity<String> {
        return ResponseEntity.ok().body(
            refreshTokenFacade.refreshAccessToken(
                token
            )
        )
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃")
    @Parameter(name = "token", description = "액세스 토큰", example = "eyJhbGciOi~~~")
    fun logout(
        @RequestParam
        token: String
    ): ResponseEntity<String> {
        return ResponseEntity.ok().body(
            refreshTokenService.deleteByAccessToken(
                token
            )
        )
    }

    @PostMapping("/test")
    @Operation(summary = "테스트 계정 로그인", description = "테스트 계정 로그인")
    fun testAccountLogin(): ResponseEntity<String> {
        return ResponseEntity.ok().body(
            refreshTokenFacade.testAccountLogin()
        )
    }
}