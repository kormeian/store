package com.arffy.server.domian.notice.controller

import com.arffy.server.domian.notice.dto.NoticeResponse
import com.arffy.server.domian.notice.facade.NoticeFacade
import com.arffy.server.domian.notice.service.NoticeServiceImpl
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "공지사항 API", description = "공지사항 API")
@RestController
@RequestMapping("/api/v1/notice")
class NoticeController(
    private val noticeService: NoticeServiceImpl,
    private val noticeFacade: NoticeFacade,
) {
    @GetMapping
    fun getNotice(
        pageable: Pageable
    ): ResponseEntity<Page<NoticeResponse>> {
        return ResponseEntity.ok(
            noticeService.findAllByPageable(
                pageable
            )
        )
    }

    @GetMapping("{noticeId}")
    fun getNoticeById(
        @PathVariable("noticeId")
        noticeId: Long?
    ): ResponseEntity<NoticeResponse> {
        return ResponseEntity.ok(
            noticeFacade.findNoticeResponseById(
                noticeId
            )
        )
    }
}