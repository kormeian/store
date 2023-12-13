package com.arffy.server.domian.notice.dto

import com.arffy.server.domian.notice.Notice
import com.arffy.server.infra.s3.dto.ImageDto
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class NoticeResponse(
    @field:Schema(description = "공지사항 ID", example = "1")
    var noticeId: Long,

    @field:Schema(description = "공지사항 제목", example = "공지사항 제목")
    val title: String,

    @field:Schema(description = "공지사항 내용", example = "공지사항 내용")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var content: String? = null,

    @field:Schema(description = "상단 고정 여부", example = "true")
    val topFlag: Boolean,

    @field:Schema(description = "공지사항 생성일", example = "2021-01-01 00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,

    @field:Schema(description = "공지사항 이미지 리스트")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var imageList: List<ImageDto>? = null,
) {
    companion object {
        fun from(notice: Notice): NoticeResponse {
            return NoticeResponse(
                noticeId = notice.id!!,
                title = notice.title,
                topFlag = notice.topFlag,
                createdAt = notice.createdAt
            )
        }
    }
}

