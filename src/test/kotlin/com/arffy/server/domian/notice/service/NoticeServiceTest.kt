package com.arffy.server.domian.notice.service

import com.arffy.server.domian.notice.Notice
import com.arffy.server.domian.notice.exception.NoticeErrorCode
import com.arffy.server.domian.notice.repository.NoticeRepository
import com.arffy.server.global.exception.RestApiException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.*

class NoticeServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val noticeRepository = mockk<NoticeRepository>(relaxed = true)
    val noticeService = NoticeServiceImpl(noticeRepository)
    val notice = Notice(title = "title", content = "content", topFlag = false)

    Given("모든 공지사항 조회") {
        notice.id = 1L
        notice.createdAt = LocalDateTime.MIN
        When("pageable로 공지사항 목록을 조회한다") {
            val list = listOf(notice)
            every { noticeRepository.findAllByOrderByTopFlagDescCreatedAtDesc(any()) } returns PageImpl(list)
            Then("목록 조회 성공") {
                val result = noticeService.findAllByPageable(Pageable.unpaged())
                result.content.size shouldBe list.size
            }
        }
    }
    Given("상세 조회를 위한 공지사항 조회") {
        notice.id = 1L
        When("id로 공지사항 조회") {
            every { noticeRepository.findById(any()) } returns Optional.of(notice)
            Then("조회 성공") {
                val result = noticeService.findById(1L)
                result.id shouldBe notice.id
            }
        }
        When("해당 공지를 찾지못함") {
            every { noticeRepository.findById(any()) } returns Optional.empty()
            Then("${NoticeErrorCode.NOT_FOUND_NOTICE} 에러 반환") {
                val result = shouldThrow<RestApiException> { noticeService.findById(notice.id!!) }
                result.baseErrorCode shouldBe NoticeErrorCode.NOT_FOUND_NOTICE
            }
        }
    }
})
