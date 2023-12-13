package com.arffy.server.domian.qna.controller

import com.arffy.server.domian.qna.dto.QnaCommentResponse
import com.arffy.server.domian.qna.dto.QnaDetailResponseDto
import com.arffy.server.domian.qna.dto.QnaRequest
import com.arffy.server.domian.qna.dto.QnaResponse
import com.arffy.server.domian.qna.facade.QnaFacade
import com.arffy.server.domian.qna.service.QnaServiceImpl
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.security.CurrentUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "QnA API")
@RestController
@RequestMapping("/api/v1/qna")
class QnaController(
    private val qnaService: QnaServiceImpl,
    private val qnaFacade: QnaFacade,
) {

    @PostMapping
    @Operation(
        summary = "QnA 등록",
        description = "QnA 등록",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createQna(
        @RequestPart(required = false)
        multipartFileList: List<MultipartFile>?,

        @RequestPart
        qnaRequest: QnaRequest?,

        @CurrentUser @Parameter(hidden = true)
        user: User
    ): ResponseEntity<Long> {
        return ResponseEntity.ok(
            qnaFacade.createQna(
                multipartFileList,
                qnaRequest,
                user
            )
        )
    }

    @GetMapping("/{qnaId}")
    @Operation(
        summary = "QnA 상세 조회",
        description = "QnA 상세 조회",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getQnaById(
        @PathVariable("qnaId")
        qnaId: Long?,

        @CurrentUser @Parameter(hidden = true)
        user: User
    ): ResponseEntity<QnaDetailResponseDto> {
        return ResponseEntity.ok(
            qnaFacade.findQnaDetailResponseByIdAndUser(
                qnaId,
                user
            )
        )
    }

    @GetMapping("/my")
    @Operation(
        summary = "나의 QnA 목록 조회",
        description = "나의 QnA 목록 조회",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getMyQnaList(
        @CurrentUser @Parameter(hidden = true)
        user: User,
        pageable: Pageable,
    ): ResponseEntity<Page<QnaResponse>> {
        return ResponseEntity.ok(
            qnaService.findAllQnaResponseByPageableAndUserId(
                pageable,
                user.id!!
            )
        )
    }

    @GetMapping("/product/{productId}")
    @Operation(
        summary = "상품별 QnA 목록 조회",
        description = "상품별 QnA 목록 조회",
    )
    fun getQnaListByProductId(
        @PathVariable("productId")
        productId: Long?,

        @CurrentUser @Parameter(hidden = true)
        user: User?,
        pageable: Pageable,
    ): ResponseEntity<Page<QnaResponse>> {
        return ResponseEntity.ok(
            qnaService.findAllQnaResponseByPageableAndProductIdAndUserId(
                pageable,
                productId,
                user?.id
            )
        )
    }

    @GetMapping
    @Operation(
        summary = "전체 QnA 목록 조회(어드민)",
        description = "전체 QnA 목록 조회(어드민)",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getAllQnaList(
        @CurrentUser @Parameter(hidden = true)
        user: User,
        pageable: Pageable,
    ): ResponseEntity<Page<QnaResponse>> {
        return ResponseEntity.ok(
            qnaService.findAllQnaResponseByPageableAndAdmin(
                pageable,
                user
            )
        )
    }

    @DeleteMapping("/{qnaId}")
    @Operation(
        summary = "QnA 삭제",
        description = "QnA 삭제",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteQnaById(
        @PathVariable("qnaId")
        qnaId: Long?,
        @CurrentUser @Parameter(hidden = true)
        user: User
    ): ResponseEntity<Unit> {
        qnaFacade.deleteByIdAndUser(qnaId, user)
        return ResponseEntity.ok().build()
    }

    @GetMapping("{qnaId}/comment")
    @Operation(
        summary = "QnA 답변 조회",
        description = "QnA 답변 조회",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getQnaComment(
        @PathVariable("qnaId")
        qnaId: Long?,
        @CurrentUser @Parameter(hidden = true)
        user: User
    ): ResponseEntity<List<QnaCommentResponse>> {
        return ResponseEntity.ok(
            qnaFacade.findAllQnaCommentResponseByIdAndUser(
                qnaId,
                user
            )
        )
    }
}

