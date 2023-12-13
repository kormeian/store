package com.arffy.server.domian.qna.facade

import com.arffy.server.domian.product.Product
import com.arffy.server.domian.product.service.ProductServiceImpl
import com.arffy.server.domian.qna.dto.QnaCommentResponse
import com.arffy.server.domian.qna.dto.QnaDetailResponseDto
import com.arffy.server.domian.qna.dto.QnaRequest
import com.arffy.server.domian.qna.entity.Qna
import com.arffy.server.domian.qna.exception.QnaErrorCode
import com.arffy.server.domian.qna.service.QnaCommentServiceImpl
import com.arffy.server.domian.qna.service.QnaServiceImpl
import com.arffy.server.domian.user.entity.User
import com.arffy.server.domian.user.service.UserServiceImpl
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.infra.s3.constant.ImageType
import com.arffy.server.infra.s3.service.ImageService
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

private val log = KotlinLogging.logger {}

@Component
class QnaFacade(
    private val qnaService: QnaServiceImpl,
    private val qnaCommentService: QnaCommentServiceImpl,
    private val imageService: ImageService,
    private val userService: UserServiceImpl,
    private val productService: ProductServiceImpl
) {
    fun createQna(
        multipartFileList: List<MultipartFile>?,
        qnaRequest: QnaRequest?,
        user: User
    ): Long {
        validateQnaRequest(qnaRequest)
        log.info { "QnaFacade.createQna" }
        log.info { "userId = ${user.id}" }

        val imageType: ImageType =
            try {
                ImageType.from(
                    qnaRequest?.imageType
                )
            } catch (e: RestApiException) {
                throw RestApiException(
                    QnaErrorCode.IMAGE_TYPE_IS_NOT_MATCH
                )
            }

        if (imageType != ImageType.QNA) {
            throw RestApiException(
                QnaErrorCode.IMAGE_TYPE_IS_NOT_MATCH
            )
        }


        if (!multipartFileList.isNullOrEmpty()
            && multipartFileList.size > imageType.imageQuantity
        ) {
            throw RestApiException(
                QnaErrorCode.IMAGE_QUANTITY_IS_TOO_MANY
            )
        }

        val qnaId = qnaService.saveByQnaRequestAndUser(qnaRequest!!, user)
        if (multipartFileList.isNullOrEmpty()) return qnaId

        return try {
            imageService.save(qnaId, multipartFileList, 0, 0, imageType)
            qnaId
        } catch (e: RestApiException) {
            qnaService.deleteById(qnaId)
            imageService.deletes(qnaId, ImageType.QNA)
            throw e
        }
    }

    private fun validateQnaRequest(
        qnaRequest: QnaRequest?
    ) {
        if (qnaRequest == null) {
            throw RestApiException(QnaErrorCode.REQUIRED_QNA_REQUEST)
        }
        if (qnaRequest.title.isNullOrBlank()) {
            throw RestApiException(QnaErrorCode.REQUIRED_QNA_TITLE)
        }
        if (qnaRequest.content.isNullOrBlank()) {
            throw RestApiException(QnaErrorCode.REQUIRED_QNA_CONTENT)
        }
        if (qnaRequest.qnaType.isNullOrBlank()) {
            throw RestApiException(QnaErrorCode.REQUIRED_QNA_TYPE)
        }
        if (qnaRequest.imageType.isNullOrBlank()) {
            throw RestApiException(QnaErrorCode.REQUIRED_IMAGE_TYPE)
        }

    }

    @Transactional(readOnly = true)
    fun findQnaDetailResponseByIdAndUser(
        qnaId: Long?,
        user: User
    ): QnaDetailResponseDto {
        if (qnaId == null) throw RestApiException(QnaErrorCode.REQUIRED_QNA_ID)
        log.info { "QnaFacade.findQnaDetailResponseByIdAndUser" }
        log.info { "qnaId = $qnaId, userId: ${user.id}" }

        val qna = qnaService.findById(qnaId)
        var product: Product? = null
        if (qna.productId != null) {
            product = productService.findById(qna.productId!!)
        }
        authorizeCheck(qna, user)

        val imageDtoList = imageService.getImageList(qnaId, ImageType.QNA)

        return QnaDetailResponseDto.from(qna, product, imageDtoList)

    }

    @Transactional
    fun deleteByIdAndUser(
        qnaId: Long?,
        user: User
    ) {
        if (qnaId == null) throw RestApiException(QnaErrorCode.REQUIRED_QNA_ID)
        log.info { "QnaFacade.deleteByIdAndUser" }
        log.info { "qnaId = $qnaId, userId = ${user.id}" }
        qnaCommentService.deleteAllByQnaId(qnaId)
        qnaService.deleteByIdAndUserId(qnaId, user.id!!)
        imageService.deletes(qnaId, ImageType.QNA)
    }

    @Transactional(readOnly = true)
    fun findAllQnaCommentResponseByIdAndUser(
        qnaId: Long?,
        user: User
    ): List<QnaCommentResponse> {
        if (qnaId == null) throw RestApiException(QnaErrorCode.REQUIRED_QNA_ID)
        log.info { "QnaFacade.findAllQnaCommentResponseByIdAndUser" }
        log.info { "qnaId = $qnaId, userId = ${user.id}" }
        val qna = qnaService.findById(qnaId)
        authorizeCheck(qna, user)
        val qnaCommentResponseList = qnaCommentService.findAllByQnaId(qna.id!!).map { QnaCommentResponse.from(it) }
        qnaCommentResponseList.forEach {
            it.imageList = imageService.getImageList(it.qnaCommentId, ImageType.QNA_COMMENT)
        }
        return qnaCommentResponseList
    }

    @Transactional
    fun deleteAllByUserId(
        userId: Long
    ) {
        log.info { "QnaFacade.deleteAllByUserId" }
        log.info { "userId = $userId" }
        val user = userService.findById(userId)
        qnaService.findAllByUserId(userId).forEach {
            this.deleteByIdAndUser(it.id, user)
        }
    }

    private fun authorizeCheck(
        qna: Qna,
        user: User
    ) {
        if (!userService.isAdmin(user)) {
            if (qna.user.id != user.id) {
                throw RestApiException(QnaErrorCode.NOT_AUTHORIZED)
            }
        }
    }
}