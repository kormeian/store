package com.arffy.server.domian.user.service

import com.arffy.server.domian.user.dto.UserModifyRequest
import com.arffy.server.domian.user.entity.AuthProvider
import com.arffy.server.domian.user.entity.Role
import com.arffy.server.domian.user.entity.User
import com.arffy.server.domian.user.exception.UserErrorCode
import com.arffy.server.domian.user.reposiroty.UserRepository
import com.arffy.server.global.exception.RestApiException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class UserServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val userRepository = mockk<UserRepository>(relaxed = true)
    val userService = UserServiceImpl(userRepository)

    Given("user가 admin인지 확인 요청") {
        val user = mockk<User>(relaxed = true)
        every { user.role } returns Role.ROLE_USER
        val adminUser = mockk<User>(relaxed = true)
        every { adminUser.role } returns Role.ROLE_ADMIN
        When("user의 role이 ROLE_ADMIN일 때") {
            Then("true를 반환한다") {
                userService.isAdmin(
                    adminUser
                ) shouldBe true
            }
        }
        When("user의 role이 ROLE_ADMIN이 아닐 때") {
            Then("false를 반환한다") {
                userService.isAdmin(
                    user
                ) shouldBe false
            }
        }
    }
    Given("userModifyRequest dto로 user의 정보 수정 요청") {
        val userModifyRequest = mockk<UserModifyRequest>(relaxed = true)
        every { userModifyRequest.name } returns "name"
        every { userModifyRequest.phoneNumber } returns "01012345678"
        every { userModifyRequest.address } returns "address"
        every { userModifyRequest.addressDetail } returns "addressDetail"
        every { userModifyRequest.postCode } returns "postCode"
        val user = User(
            name = "n",
            email = "email",
            phoneNumber = "0",
            role = Role.ROLE_USER,
            address = "a",
            addressDetail = "a",
            postCode = "p",
            authProvider = AuthProvider.KAKAO,
            oauth2Id = "oauth2Id"
        )
        When("user와 userModifyRequest가 정상적으로 주어짐") {
            Then("정상적으로 수정된 user의 id를 반환한다") {
                val slot = slot<User>()
                every { userRepository.save(capture(slot)) } returns mockk(relaxed = true)
                userService.updateByUserModifyRequest(
                    user,
                    userModifyRequest
                )
                val result = slot.captured
                result.name shouldBe userModifyRequest.name
            }
        }
        When("userModifyRequset가 정상적으로 주어지지 않음") {
            Then("userModifyRequest가 null임 - ${UserErrorCode.REQUIRED_USER_MODIFY_REQUEST} 예외 발생") {
                val result = shouldThrow<RestApiException> {
                    userService.updateByUserModifyRequest(
                        user,
                        null
                    )
                }
                result.baseErrorCode shouldBe UserErrorCode.REQUIRED_USER_MODIFY_REQUEST
            }
            Then("userModifyRequest의 name이 null임 - ${UserErrorCode.REQUIRED_USER_NAME} 예외 발생") {
                every { userModifyRequest.name } returns null
                val result = shouldThrow<RestApiException> {
                    userService.updateByUserModifyRequest(
                        user,
                        userModifyRequest
                    )
                }
                result.baseErrorCode shouldBe UserErrorCode.REQUIRED_USER_NAME
            }
            Then("userModifyRequest의 name이 빈 문자열임 - ${UserErrorCode.REQUIRED_USER_NAME} 예외 발생") {
                every { userModifyRequest.name } returns ""
                val result = shouldThrow<RestApiException> {
                    userService.updateByUserModifyRequest(
                        user,
                        userModifyRequest
                    )
                }
                result.baseErrorCode shouldBe UserErrorCode.REQUIRED_USER_NAME
            }
            Then("userModifyRequest의 phoneNumber이 null임 - ${UserErrorCode.REQUIRED_USER_PHONE_NUMBER} 예외 발생") {
                every { userModifyRequest.phoneNumber } returns null
                val result = shouldThrow<RestApiException> {
                    userService.updateByUserModifyRequest(
                        user,
                        userModifyRequest
                    )
                }
                result.baseErrorCode shouldBe UserErrorCode.REQUIRED_USER_PHONE_NUMBER
            }
            Then("userModifyRequest의 phoneNumber이 빈 문자열임 - ${UserErrorCode.REQUIRED_USER_PHONE_NUMBER} 예외 발생") {
                every { userModifyRequest.phoneNumber } returns ""
                val result = shouldThrow<RestApiException> {
                    userService.updateByUserModifyRequest(
                        user,
                        userModifyRequest
                    )
                }
                result.baseErrorCode shouldBe UserErrorCode.REQUIRED_USER_PHONE_NUMBER
            }
            Then("userModifyRequest의 address가 null임 - ${UserErrorCode.REQUIRED_USER_ADDRESS} 예외 발생") {
                every { userModifyRequest.address } returns null
                val result = shouldThrow<RestApiException> {
                    userService.updateByUserModifyRequest(
                        user,
                        userModifyRequest
                    )
                }
                result.baseErrorCode shouldBe UserErrorCode.REQUIRED_USER_ADDRESS
            }
            Then("userModifyRequest의 address가 빈 문자열임 - ${UserErrorCode.REQUIRED_USER_ADDRESS} 예외 발생") {
                every { userModifyRequest.address } returns ""
                val result = shouldThrow<RestApiException> {
                    userService.updateByUserModifyRequest(
                        user,
                        userModifyRequest
                    )
                }
                result.baseErrorCode shouldBe UserErrorCode.REQUIRED_USER_ADDRESS
            }
            Then("userModifyRequest의 postCode가 null임 - ${UserErrorCode.REQUIRED_USER_POST_CODE} 예외 발생") {
                every { userModifyRequest.postCode } returns null
                val result = shouldThrow<RestApiException> {
                    userService.updateByUserModifyRequest(
                        user,
                        userModifyRequest
                    )
                }
                result.baseErrorCode shouldBe UserErrorCode.REQUIRED_USER_POST_CODE
            }
            Then("userModifyRequest의 postCode가 빈 문자열임 - ${UserErrorCode.REQUIRED_USER_POST_CODE} 예외 발생") {
                every { userModifyRequest.postCode } returns ""
                val result = shouldThrow<RestApiException> {
                    userService.updateByUserModifyRequest(
                        user,
                        userModifyRequest
                    )
                }
                result.baseErrorCode shouldBe UserErrorCode.REQUIRED_USER_POST_CODE
            }
        }
    }
})
