package com.arffy.server.domian.qna.service

import com.arffy.server.domian.qna.entity.QnaComment
import com.arffy.server.domian.qna.repository.QnaCommentRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class QnaCommentServiceImpl(
    private val qnaCommentRepository: QnaCommentRepository
) : QnaCommentService {

    @Transactional
    override fun deleteAllByQnaId(
        qnaId: Long
    ) {
        log.info { "QnaCommentServiceImpl.deleteAllByQnaId" }
        log.info { "qnaId = $qnaId" }
        qnaCommentRepository.deleteAllByQnaId(qnaId)
    }

    @Transactional(readOnly = true)
    override fun findAllByQnaId(
        qnaId: Long
    ): List<QnaComment> {
        log.info { "QnaCommentServiceImpl.findAllByQnaId" }
        log.info { "qnaId = $qnaId" }
        return qnaCommentRepository.findByQnaIdOrderById(qnaId)
    }

    @Transactional(readOnly = true)
    override fun existsByQnaId(
        qnaId: Long
    ): Boolean {
        log.info { "QnaCommentServiceImpl.existsByQnaId" }
        log.info { "qnaId = $qnaId" }
        return qnaCommentRepository.existsByQnaId(qnaId)
    }
}