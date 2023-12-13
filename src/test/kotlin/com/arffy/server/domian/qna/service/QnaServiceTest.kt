package com.arffy.server.domian.qna.service

import com.arffy.server.domian.qna.dto.QnaRequest
import com.arffy.server.domian.qna.entity.Qna
import com.arffy.server.domian.qna.exception.QnaErrorCode
import com.arffy.server.domian.qna.repository.QnaRepository
import com.arffy.server.domian.user.entity.Role
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.exception.RestApiException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*

class QnaServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val qnaRepository = mockk<QnaRepository>(relaxed = true)
    val qnaService = QnaServiceImpl(qnaRepository)
    Given("qnaRequest dto로 qna 생성 요청") {
        val qnaRequest = QnaRequest(
            title = "title",
            content = "content",
            productId = 1L,
            qnaType = "PRODUCT",
            imageType = "QNA"
        )
        val user = mockk<User>(relaxed = true)
        val slot = slot<Qna>()
        When("qnaRequest와 user가 정상적으로 주어짐") {
            Then("저장 성공") {
                every { qnaRepository.save(capture(slot)) } returns mockk(relaxed = true)
                qnaService.saveByQnaRequestAndUser(qnaRequest, user)
                val result = slot.captured
                result.title shouldBe qnaRequest.title
            }
            Then("저장 실패 - ${QnaErrorCode.FAILED_TO_CREATE_QNA} 예외 발생") {
                val qna = mockk<Qna>(relaxed = true)
                every { qna.id } returns null
                every { qnaRepository.save(any()) } returns qna
                val result = shouldThrow<RestApiException> { qnaService.saveByQnaRequestAndUser(qnaRequest, user) }
                result.baseErrorCode shouldBe QnaErrorCode.FAILED_TO_CREATE_QNA
            }
        }
        When("qnaRequest의 qnaType이 정상적으로 주어지지 않음") {
            val qnaRequest2 = mockk<QnaRequest>(relaxed = true)
            Then("저장 실패 - ${QnaErrorCode.QNA_TYPE_IS_NOT_MATCHED} 예외 발생") {
                val result = shouldThrow<RestApiException> { qnaService.saveByQnaRequestAndUser(qnaRequest2, user) }
                result.baseErrorCode shouldBe QnaErrorCode.QNA_TYPE_IS_NOT_MATCHED
            }
        }
    }
    Given("qnaId로 qna 조회 요청") {
        val qnaId = 1L
        val qna = mockk<Qna>(relaxed = true)
        every { qna.id } returns qnaId
        When("qnaId가 정상적으로 주어짐") {
            Then("조회 성공") {
                every { qnaRepository.findById(qnaId) } returns Optional.of(qna)
                val result = qnaService.findById(qnaId)
                result.id shouldBe qnaId
            }
            Then("조회 실패 - ${QnaErrorCode.NOT_FOUND_QNA} 예외 발생") {
                every { qnaRepository.findById(qnaId) } returns Optional.empty()
                val result = shouldThrow<RestApiException> { qnaService.findById(qnaId) }
                result.baseErrorCode shouldBe QnaErrorCode.NOT_FOUND_QNA
            }
        }
    }
    Given("qnaId로 qna 삭제 요청") {
        val qnaId = 1L
        val qna = mockk<Qna>(relaxed = true)
        every { qna.id } returns qnaId
        When("qnaId가 정상적으로 주어짐") {
            Then("삭제 성공") {
                every { qnaRepository.existsById(qnaId) } returns true
                every { qnaRepository.deleteById(qnaId) } returns Unit
                qnaService.deleteById(qnaId)
                verify(exactly = 1) { qnaRepository.existsById(qnaId) }
                verify(exactly = 1) { qnaRepository.deleteById(qnaId) }
            }
            Then("삭제 실패 - ${QnaErrorCode.NOT_FOUND_QNA} 예외 발생") {
                every { qnaRepository.existsById(qnaId) } returns false
                val result = shouldThrow<RestApiException> { qnaService.deleteById(qnaId) }
                result.baseErrorCode shouldBe QnaErrorCode.NOT_FOUND_QNA
                verify(exactly = 0) { qnaRepository.deleteById(qnaId) }
            }
        }
    }
    Given("userId로 qnaResponse dto 리스트 조회 요청") {
        val userId = 1L
        val qna = mockk<Qna>(relaxed = true)
        val pageable = mockk<Pageable>(relaxed = true)
        When("userId가 정상적으로 주어짐") {
            Then("조회 성공") {
                every { qnaRepository.findAllByUserIdOrderByCreatedAtDesc(pageable, userId) } returns PageImpl(
                    listOf(
                        qna
                    )
                )
                val result = qnaService.findAllQnaResponseByPageableAndUserId(pageable, userId)
                result.content.size shouldBe 1
                result.content.first().qnaId shouldBe qna.id
            }
            Then("조회 결과 없음") {
                every {
                    qnaRepository.findAllByUserIdOrderByCreatedAtDesc(
                        pageable,
                        userId
                    )
                } returns PageImpl(emptyList())
                val result = qnaService.findAllQnaResponseByPageableAndUserId(pageable, userId)
                result.content.size shouldBe 0
            }
        }
    }
    Given("userId로 qna 리스트 조회 요청") {
        val userId = 1L
        val qna = mockk<Qna>(relaxed = true)
        When("userId가 정상적으로 주어짐") {
            Then("조회 성공") {
                every { qnaRepository.findAllByUserId(userId) } returns listOf(qna)
                val result = qnaService.findAllByUserId(userId)
                result.size shouldBe 1
                result.first().id shouldBe qna.id
            }
            Then("조회 결과 없음") {
                every { qnaRepository.findAllByUserId(userId) } returns emptyList()
                val result = qnaService.findAllByUserId(userId)
                result.size shouldBe 0
            }
        }
    }
    Given("productId와 userId로 qnaResponse dto 리스트 조회 요청") {
        val productId = 1L
        val userId = 1L
        val qna = mockk<Qna>(relaxed = true)
        every { qna.user.id } returns userId
        val qna2 = mockk<Qna>(relaxed = true)
        every { qna2.user.id } returns userId + 1
        val pageable = mockk<Pageable>(relaxed = true)
        When("productId와 userId가 정상적으로 주어짐") {
            Then("조회 성공") {
                every {
                    qnaRepository.findAllByProductIdOrderByCreatedAtDesc(
                        pageable,
                        productId,
                    )
                } returns PageImpl(listOf(qna, qna2))
                val result = qnaService.findAllQnaResponseByPageableAndProductIdAndUserId(pageable, productId, userId)
                result.content.size shouldBe 2
                result.content.first().isMine shouldBe true
                result.content[1].isMine shouldBe false
            }
            Then("조회 결과 없음") {
                every {
                    qnaRepository.findAllByProductIdOrderByCreatedAtDesc(
                        pageable,
                        productId,
                    )
                } returns PageImpl(emptyList())
                val result = qnaService.findAllQnaResponseByPageableAndProductIdAndUserId(pageable, productId, userId)
                result.content.size shouldBe 0
            }
        }
    }
    Given("admin으로 qnaResponse dto 리스트 조회 요청") {
        val admin = mockk<User>(relaxed = true)
        every { admin.role } returns Role.ROLE_ADMIN
        val qna = mockk<Qna>(relaxed = true)
        every { qna.user.id } returns admin.id!!
        val pageable = mockk<Pageable>(relaxed = true)
        When("admin이 정상적으로 주어짐") {
            Then("조회 성공") {
                every { qnaRepository.findAllByOrderByCreatedAtDesc(pageable) } returns PageImpl(listOf(qna))
                val result = qnaService.findAllQnaResponseByPageableAndAdmin(pageable, admin)
                result.content.size shouldBe 1
                result.content.first().isMine shouldBe true
            }
            Then("조회 결과 없음") {
                every { qnaRepository.findAllByOrderByCreatedAtDesc(pageable) } returns PageImpl(emptyList())
                val result = qnaService.findAllQnaResponseByPageableAndAdmin(pageable, admin)
                result.content.size shouldBe 0
            }
        }
        When("admin이 정상적으로 주어지지 않음") {
            Then("조회 실패 - ${QnaErrorCode.NOT_AUTHORIZED} 예외 발생") {
                every { admin.role } returns Role.ROLE_USER
                val result =
                    shouldThrow<RestApiException> { qnaService.findAllQnaResponseByPageableAndAdmin(pageable, admin) }
                result.baseErrorCode shouldBe QnaErrorCode.NOT_AUTHORIZED
            }
        }
    }

})
