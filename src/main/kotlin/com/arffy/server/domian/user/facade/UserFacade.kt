package com.arffy.server.domian.user.facade

import com.arffy.server.domian.cart.service.CartService
import com.arffy.server.domian.order.service.OrderServiceImpl
import com.arffy.server.domian.qna.facade.QnaFacade
import com.arffy.server.domian.user.exception.UserErrorCode
import com.arffy.server.domian.user.service.RefreshTokenServiceImpl
import com.arffy.server.domian.user.service.UserServiceImpl
import com.arffy.server.global.exception.RestApiException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder

private val log = KotlinLogging.logger {}

@Service
class UserFacade(
    private val userService: UserServiceImpl,
    private val orderService: OrderServiceImpl,
    private val cartService: CartService,
    private val qnaFacade: QnaFacade,
    private val refreshTokenService: RefreshTokenServiceImpl,
) {
    @Transactional
    fun deleteByUserId(
        userId: Long,
    ) {
        log.info { "UserFacade.deleteByUserId" }
        log.info { "userId = $userId" }
        val user = userService.findById(userId)
        cartService.deleteAllByUserId(user.id!!)
        qnaFacade.deleteAllByUserId(user.id!!)
        refreshTokenService.deleteByEmail(user.email)
        if (orderService.existsByUserId(userId)) {
            user.email += " (deleted)"
            userService.save(user)
        } else {
            userService.deleteById(user.id!!)
        }
        unlinkKaKao(user.oauth2Id)
    }

    @Value("\${spring.security.oauth2.client.registration.kakao.admin-key}")
    private lateinit var adminKey: String

    private fun unlinkKaKao(
        oauth2Id: String
    ): Long {
        log.info { "UserFacade.unlinkKaKao" }
        log.info { "oauth2Id = $oauth2Id" }
        val uri = UriComponentsBuilder.fromUriString("https://kapi.kakao.com/v1/user/unlink")
            .queryParam("target_id_type", "user_id")
            .queryParam("target_id", oauth2Id)
            .build()
        val requestEntity = RequestEntity.post(uri.toUri())
            .header("Authorization", "KakaoAK ${adminKey}")
            .build()
        val restTemplate = RestTemplateBuilder().build()
        return try {
            restTemplate.exchange(requestEntity, UnlinkResponse::class.java).body?.id!!
        } catch (e: Exception) {
            throw RestApiException(UserErrorCode.UNLINK_FAILED)
        }
    }

    class UnlinkResponse(
        val id: Long,
    )
}