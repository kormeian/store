package com.arffy.server.domian.user.controller

import com.arffy.server.domian.user.dto.UserModifyRequest
import com.arffy.server.domian.user.dto.UserResponse
import com.arffy.server.domian.user.entity.User
import com.arffy.server.domian.user.facade.UserFacade
import com.arffy.server.domian.user.service.UserServiceImpl
import com.arffy.server.global.security.CurrentUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "사용자 API")
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserServiceImpl,
    private val userFacade: UserFacade,
) {

    @PatchMapping
    @Operation(
        summary = "사용자 정보 수정",
        description = "사용자 정보 수정",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun modifyUser(
        @Parameter(hidden = true)
        @CurrentUser
        user: User,
        @RequestBody
        userModifyRequest: UserModifyRequest?
    ): ResponseEntity<Long> {
        return ResponseEntity.ok(
            userService.updateByUserModifyRequest(
                user,
                userModifyRequest
            )
        )
    }

    @GetMapping("/my")
    @Operation(
        summary = "나의 정보 조회",
        description = "나의 정보 조회",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getMyInfo(
        @Parameter(hidden = true) @CurrentUser
        user: User
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(
            userService.findUserResponseByUser(
                user
            )
        )
    }

    @DeleteMapping
    @Operation(
        summary = "사용자 삭제",
        description = "사용자 삭제",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteUser(
        @Parameter(hidden = true) @CurrentUser
        user: User
    ): ResponseEntity<Unit> {
        userFacade.deleteByUserId(user.id!!)
        return ResponseEntity.ok().build()
    }

}