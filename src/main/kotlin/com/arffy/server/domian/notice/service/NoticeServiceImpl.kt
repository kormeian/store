package com.arffy.server.domian.notice.service

import com.arffy.server.domian.notice.Notice
import com.arffy.server.domian.notice.dto.NoticeResponse
import com.arffy.server.domian.notice.exception.NoticeErrorCode
import com.arffy.server.domian.notice.repository.NoticeRepository
import com.arffy.server.global.exception.RestApiException
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class NoticeServiceImpl(
    private val noticeRepository: NoticeRepository,
) : NoticeService {

    @Transactional(readOnly = true)
    override fun findAllByPageable(
        pageable: Pageable
    ): Page<NoticeResponse> {
        log.info { "NoticeServiceImpl.findAllByPageable" }
        return noticeRepository.findAllByOrderByTopFlagDescCreatedAtDesc(pageable).map {
            NoticeResponse.from(it)
        }
    }

    @Transactional(readOnly = true)
    override fun findById(
        id: Long
    ): Notice {
        log.info { "NoticeServiceImpl.findById" }
        log.info { "noticeId = $id" }
        return noticeRepository.findById(id).orElseThrow {
            throw RestApiException(NoticeErrorCode.NOT_FOUND_NOTICE)
        }
    }
}