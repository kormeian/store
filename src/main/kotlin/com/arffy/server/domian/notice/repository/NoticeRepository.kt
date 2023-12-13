package com.arffy.server.domian.notice.repository

import com.arffy.server.domian.notice.Notice
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : JpaRepository<Notice, Long> {
    fun findAllByOrderByTopFlagDescCreatedAtDesc(
        pageable: Pageable
    ): Page<Notice>
}