package com.arffy.server.domian.qna.service

import com.arffy.server.domian.qna.dto.QnaRequest
import com.arffy.server.domian.qna.dto.QnaResponse
import com.arffy.server.domian.qna.entity.Qna
import com.arffy.server.domian.qna.entity.QnaType
import com.arffy.server.domian.qna.exception.QnaErrorCode
import com.arffy.server.domian.qna.repository.QnaRepository
import com.arffy.server.domian.user.entity.Role
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.exception.RestApiException
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class QnaServiceImpl(
    private val qnaRepository: QnaRepository,
) : QnaService {

    @Transactional
    override fun saveByQnaRequestAndUser(
        qnaRequest: QnaRequest,
        user: User
    ): Long {
        log.info { "QnaServiceImpl.saveByQnaRequestAndUser" }
        log.info { "userId = ${user.id}" }
        return qnaRepository.save(
            Qna(
                title = qnaRequest.title!!,
                user = user,
                content = qnaRequest.content!!,
                productId = qnaRequest.productId,
                qnaType = QnaType.from(qnaRequest.qnaType!!),
                isAnswered = false,
            )
        ).id ?: throw RestApiException(QnaErrorCode.FAILED_TO_CREATE_QNA)
    }

    @Transactional(readOnly = true)
    override fun findById(
        qnaId: Long
    ): Qna {
        log.info { "QnaServiceImpl.findById" }
        log.info { "qnaId = $qnaId" }
        return qnaRepository.findById(qnaId).orElseThrow {
            RestApiException(QnaErrorCode.NOT_FOUND_QNA)
        }
    }

    @Transactional
    override fun deleteById(
        id: Long
    ) {
        log.info { "QnaServiceImpl.deleteById" }
        log.info { "qnaId = $id" }
        if (!qnaRepository.existsById(id)) throw RestApiException(QnaErrorCode.NOT_FOUND_QNA)
        qnaRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun findAllQnaResponseByPageableAndUserId(
        pageable: Pageable,
        userId: Long
    ): Page<QnaResponse> {
        log.info { "QnaServiceImpl.findAllQnaResponseByPageableAndUserId" }
        log.info { "userId = $userId" }
        return qnaRepository.findAllByUserIdOrderByCreatedAtDesc(
            pageable,
            userId
        ).map { QnaResponse.from(it, true) }
    }

    @Transactional(readOnly = true)
    override fun findAllByUserId(
        userId: Long
    ): List<Qna> {
        log.info { "QnaServiceImpl.findAllByUserId" }
        log.info { "userId = $userId" }
        return qnaRepository.findAllByUserId(userId)
    }

    @Transactional(readOnly = true)
    override fun findAllQnaResponseByPageableAndProductIdAndUserId(
        pageable: Pageable,
        productId: Long?,
        userId: Long?
    ): Page<QnaResponse> {
        if (productId == null) throw RestApiException(QnaErrorCode.REQUIRED_PRODUCT_ID)
        log.info { "QnaServiceImpl.findAllQnaResponseByPageableAndProductIdAndUserId" }
        log.info { "productId = $productId, userId = $userId" }
        return qnaRepository.findAllByProductIdOrderByCreatedAtDesc(pageable, productId)
            .map { QnaResponse.from(it, userId == it.user.id) }
    }

    @Transactional(readOnly = true)
    override fun findAllQnaResponseByPageableAndAdmin(
        pageable: Pageable,
        admin: User
    ): Page<QnaResponse> {
        log.info { "QnaServiceImpl.findAllQnaResponseByPageableAndAdmin" }
        log.info { "adminId = ${admin.id}" }
        if (admin.role != Role.ROLE_ADMIN) {
            throw RestApiException(QnaErrorCode.NOT_AUTHORIZED)
        }
        return qnaRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map { QnaResponse.from(it, it.user.id == admin.id) }

    }

    @Transactional
    override fun deleteByIdAndUserId(
        qnaId: Long,
        userId: Long
    ) {
        log.info { "QnaServiceImpl.deleteByIdAndUserId" }
        log.info { "qnaId = $qnaId, userId = $userId" }
        val qna = findById(qnaId)
        if (qna.user.id != userId) {
            throw RestApiException(QnaErrorCode.NOT_AUTHORIZED)
        }
        deleteById(qnaId)
    }
}
