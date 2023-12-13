package com.arffy.server.domian.qna.repository

import com.arffy.server.domian.qna.entity.QnaComment
import org.springframework.data.jpa.repository.JpaRepository

interface QnaCommentRepository : JpaRepository<QnaComment, Long> {
    fun findByQnaIdOrderById(
        qnaId: Long
    ): List<QnaComment>

    fun deleteAllByQnaId(
        qnaId: Long
    )

    fun existsByQnaId(
        qnaId: Long
    ): Boolean
}