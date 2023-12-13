package com.arffy.server.domian.qna.service

import com.arffy.server.domian.qna.entity.QnaComment

interface QnaCommentService {
    fun deleteAllByQnaId(
        qnaId: Long
    )

    fun findAllByQnaId(
        qnaId: Long
    ): List<QnaComment>

    fun existsByQnaId(
        qnaId: Long
    ): Boolean
}