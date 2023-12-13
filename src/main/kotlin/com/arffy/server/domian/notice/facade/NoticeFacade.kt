package com.arffy.server.domian.notice.facade

import com.arffy.server.domian.notice.dto.NoticeResponse
import com.arffy.server.domian.notice.exception.NoticeErrorCode
import com.arffy.server.domian.notice.service.NoticeServiceImpl
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.infra.s3.constant.ImageType
import com.arffy.server.infra.s3.service.ImageService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class NoticeFacade(
    private val noticeService: NoticeServiceImpl,
    private val imageService: ImageService,
) {
    @Transactional
    fun findNoticeResponseById(
        id: Long?
    ): NoticeResponse {
        log.info { "NoticeFacade.findNoticeResponseById" }
        log.info { "noticeId = $id" }
        if (id == null) throw RestApiException(NoticeErrorCode.REQUIRED_NOTICE_ID)
        val notice = noticeService.findById(id)
        val noticeResponse = NoticeResponse.from(notice)
        noticeResponse.content = notice.content
        noticeResponse.imageList = imageService.getImageList(noticeResponse.noticeId, ImageType.NOTICE)
        return noticeResponse
    }
}