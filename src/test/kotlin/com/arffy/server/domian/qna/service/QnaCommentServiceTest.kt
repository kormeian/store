package com.arffy.server.domian.qna.service

import com.arffy.server.domian.qna.entity.QnaComment
import com.arffy.server.domian.qna.repository.QnaCommentRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class QnaCommentServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val qnaCommentRepository = mockk<QnaCommentRepository>(relaxed = true)
    val qnaCommentService = QnaCommentServiceImpl(qnaCommentRepository)

    Given("qnaId로 QnaComment 전체 삭제 요청") {
        val qnaId = 1L
        When("qnaId가 정상적으로 주어짐") {
            Then("QnaComment 전체 삭제") {
                qnaCommentService.deleteAllByQnaId(qnaId)
                verify(exactly = 1) { qnaCommentRepository.deleteAllByQnaId(qnaId) }
            }
        }
    }
    Given("qnaId로 QnaComment 전체 조회 요청") {
        val qnaId = 1L
        val qnaComment = mockk<QnaComment>()
        every { qnaComment.qna.id } returns qnaId
        When("qnaId가 정상적으로 주어짐") {
            Then("조회 성공") {
                every { qnaCommentRepository.findByQnaIdOrderById(qnaId) } returns listOf(qnaComment)
                val result = qnaCommentService.findAllByQnaId(qnaId)
                result.size shouldBe 1
                result[0].qna.id shouldBe qnaComment.qna.id
            }
            Then("조회 결과 없음") {
                every { qnaCommentRepository.findByQnaIdOrderById(any()) } returns listOf()
                val result = qnaCommentService.findAllByQnaId(qnaId)
                result.size shouldBe 0
            }
        }
    }
    Given("qnaId로 QnaComment 존재 여부 조회 요청") {
        val qnaId = 1L
        When("qnaId가 정상적으로 주어짐") {
            Then("존재함") {
                every { qnaCommentRepository.existsByQnaId(qnaId) } returns true
                val result = qnaCommentService.existsByQnaId(qnaId)
                result shouldBe true
            }
            Then("존재하지 않음") {
                every { qnaCommentRepository.existsByQnaId(qnaId) } returns false
                val result = qnaCommentService.existsByQnaId(qnaId)
                result shouldBe false
            }
        }
    }
})
