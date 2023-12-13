package com.arffy.server.domian.qna.repository

import com.arffy.server.domian.qna.entity.Qna
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QnaRepository : JpaRepository<Qna, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(
        pageable: Pageable,
        userId: Long
    ): Page<Qna>

    fun findAllByProductIdOrderByCreatedAtDesc(
        pageable: Pageable,
        productId: Long
    ): Page<Qna>

    fun findAllByOrderByCreatedAtDesc(
        pageable: Pageable
    ): Page<Qna>

    fun findAllByUserId(
        userId: Long
    ): List<Qna>
}