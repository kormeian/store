package com.arffy.server.domian.user.service

import com.arffy.server.domian.user.entity.RefreshToken
import com.arffy.server.domian.user.reposiroty.RefreshTokenRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class RefreshTokenServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
    val refreshTokenService = RefreshTokenServiceImpl(refreshTokenRepository)

    Given("email로 token 조회 요청") {
        val email = "email"
        When("email이 정상적으로 주어졌을때") {
            Then("해당 토큰 정보 반환") {
                every { refreshTokenRepository.findByEmail(email) } returns mockk(relaxed = true)
                refreshTokenService.findByEmail(email)
                verify(exactly = 1) { refreshTokenRepository.findByEmail(email) }
            }
            Then("해당 토큰 정보가 없으면 null 반환") {
                every { refreshTokenRepository.findByEmail(email) } returns null
                refreshTokenService.findByEmail(email) shouldBe null
            }
        }
    }
    Given("id로 token 삭제 요청") {
        val id = 1L
        When("id가 정상적으로 주어졌을때") {
            Then("해당 토큰 정보 삭제") {
                every { refreshTokenRepository.deleteById(id) } returns Unit
                refreshTokenService.deleteById(id)
                verify(exactly = 1) { refreshTokenRepository.deleteById(id) }
            }
        }
    }
    Given("token 저장 요청") {
        val refreshToken = mockk<RefreshToken>(relaxed = true)
        When("RefreshToken Entity가 정상적으로 주어졌을때") {
            Then("해당 토큰 정보 저장") {
                val slot = slot<RefreshToken>()
                every { refreshTokenRepository.save(capture(slot)) } returns refreshToken
                refreshTokenService.save(refreshToken)
                slot.captured shouldBe refreshToken
                verify(exactly = 1) { refreshTokenRepository.save(refreshToken) }
            }
        }
    }
    Given("accessToken으로 token 삭제 요청") {
        val accessToken = "accessToken"
        When("accessToken이 정상적으로 주어졌을때") {
            Then("정상적으로 삭제") {
                every { refreshTokenRepository.deleteByAccessToken(accessToken) } returns Unit
                val result = refreshTokenService.deleteByAccessToken(accessToken)
                result shouldBe accessToken
                verify(exactly = 1) { refreshTokenRepository.deleteByAccessToken(accessToken) }
            }
        }
    }
    Given("email로 token 삭제 요청") {
        val email = "email"
        When("email이 정상적으로 주어졌을때") {
            Then("정상적으로 삭제") {
                every { refreshTokenRepository.deleteByEmail(email) } returns Unit
                val result = refreshTokenService.deleteByEmail(email)
                result shouldBe email
                verify(exactly = 1) { refreshTokenRepository.deleteByEmail(email) }
            }
        }
    }
})
