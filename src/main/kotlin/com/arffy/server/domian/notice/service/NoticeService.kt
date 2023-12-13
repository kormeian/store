package com.arffy.server.domian.notice.service

import com.arffy.server.domian.notice.Notice
import com.arffy.server.domian.notice.dto.NoticeResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NoticeService {
    fun findAllByPageable(
        pageable: Pageable
    ): Page<NoticeResponse>

    fun findById(
        id: Long
    ): Notice
}