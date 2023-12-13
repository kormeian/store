package com.arffy.server.domian.user.service

import com.arffy.server.domian.user.dto.UserModifyRequest
import com.arffy.server.domian.user.dto.UserResponse
import com.arffy.server.domian.user.entity.Role
import com.arffy.server.domian.user.entity.User
import com.arffy.server.domian.user.exception.UserErrorCode
import com.arffy.server.domian.user.reposiroty.UserRepository
import com.arffy.server.global.exception.RestApiException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun isAdmin(
        user: User
    ): Boolean {
        return user.role == Role.ROLE_ADMIN
    }

    @Transactional
    override fun updateByUserModifyRequest(
        user: User,
        userModifyRequest: UserModifyRequest?,
    ): Long {
        log.info { "UserServiceImpl.updateByUserModifyRequest" }
        log.info { "userId = ${user.id}" }
        validateUserModifyRequest(userModifyRequest)
        return userRepository.save(UserModifyRequest.toEntity(user, userModifyRequest!!)).id ?: throw RestApiException(
            UserErrorCode.NOT_FOUND_USER
        )
    }

    override fun findUserResponseByUser(
        user: User
    ): UserResponse {
        log.info { "UserServiceImpl.findUserResponseByUser" }
        log.info { "userId = ${user.id}" }
        return UserResponse.from(user)
    }

    @Transactional(readOnly = true)
    override fun findByEmail(
        email: String
    ): User {
        log.info { "UserServiceImpl.findByEmail" }
        log.info { "email = $email" }
        return userRepository.findByEmail(email) ?: throw RestApiException(UserErrorCode.NOT_FOUND_USER)
    }

    @Transactional(readOnly = true)
    override fun findById(
        userId: Long
    ): User {
        log.info { "UserServiceImpl.findById" }
        log.info { "userId = $userId" }
        return userRepository.findById(userId).orElseThrow { RestApiException(UserErrorCode.NOT_FOUND_USER) }
    }

    @Transactional
    override fun save(
        user: User
    ): User {
        val saveUser = userRepository.save(user)
        log.info { "UserServiceImpl.save" }
        log.info { "userId = ${saveUser.id}" }
        return saveUser
    }

    @Transactional
    override fun deleteById(
        userId: Long
    ) {
        log.info { "UserServiceImpl.deleteById" }
        log.info { "userId = $userId" }
        userRepository.deleteById(userId)
    }

    private fun validateUserModifyRequest(userModifyRequest: UserModifyRequest?) {
        if (userModifyRequest == null) throw RestApiException(UserErrorCode.REQUIRED_USER_MODIFY_REQUEST)
        if (userModifyRequest.name.isNullOrBlank()) throw RestApiException(UserErrorCode.REQUIRED_USER_NAME)
        if (userModifyRequest.phoneNumber.isNullOrBlank()) throw RestApiException(UserErrorCode.REQUIRED_USER_PHONE_NUMBER)
        if (userModifyRequest.address.isNullOrBlank()) throw RestApiException(UserErrorCode.REQUIRED_USER_ADDRESS)
        if (userModifyRequest.postCode.isNullOrBlank()) throw RestApiException(UserErrorCode.REQUIRED_USER_POST_CODE)
    }
}