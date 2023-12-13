package com.arffy.server.domian.qna.service

import com.arffy.server.domian.qna.dto.QnaRequest
import com.arffy.server.domian.qna.dto.QnaResponse
import com.arffy.server.domian.qna.entity.Qna
import com.arffy.server.domian.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface QnaService {
    fun saveByQnaRequestAndUser(
        qnaRequest: QnaRequest,
        user: User
    ): Long

    fun deleteById(
        id: Long,
    )

    fun findById(
        qnaId: Long
    ): Qna

    fun findAllQnaResponseByPageableAndUserId(
        pageable: Pageable,
        userId: Long
    ): Page<QnaResponse>

    fun findAllByUserId(
        userId: Long
    ): List<Qna>

    fun findAllQnaResponseByPageableAndProductIdAndUserId(
        pageable: Pageable,
        productId: Long?,
        userId: Long?
    ): Page<QnaResponse>

    fun findAllQnaResponseByPageableAndAdmin(
        pageable: Pageable,
        admin: User
    ): Page<QnaResponse>

    fun deleteByIdAndUserId(
        qnaId: Long,
        userId: Long
    )
}