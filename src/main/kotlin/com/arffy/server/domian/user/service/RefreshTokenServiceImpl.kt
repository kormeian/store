package com.arffy.server.domian.user.service

import com.arffy.server.domian.user.entity.RefreshToken
import com.arffy.server.domian.user.reposiroty.RefreshTokenRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class RefreshTokenServiceImpl(
    private val refreshTokenRepository: RefreshTokenRepository,
) : RefreshTokenService {
    @Transactional
    override fun findByEmail(
        email: String
    ): RefreshToken? {
        log.info { "RefreshTokenServiceImpl.findByEmail" }
        log.info { "email = $email" }
        return refreshTokenRepository.findByEmail(email)
    }

    @Transactional
    override fun deleteById(
        id: Long
    ) {
        log.info { "RefreshTokenServiceImpl.deleteById" }
        log.info { "refreshTokenId = $id" }
        refreshTokenRepository.deleteById(id)
    }

    @Transactional
    override fun save(
        refreshToken: RefreshToken
    ): RefreshToken {
        val saveRefreshToken = refreshTokenRepository.save(refreshToken)
        log.info { "RefreshTokenServiceImpl.save" }
        log.info { "refreshTokenId = ${saveRefreshToken.id}" }
        return saveRefreshToken
    }

    @Transactional
    override fun deleteByAccessToken(
        accessToken: String
    ): String {
        log.info { "RefreshTokenServiceImpl.deleteByAccessToken" }
        log.info { "accessToken = $accessToken" }
        refreshTokenRepository.deleteByAccessToken(accessToken)
        return accessToken
    }

    @Transactional
    override fun deleteByEmail(
        email: String
    ): String {
        log.info { "RefreshTokenServiceImpl.deleteByEmail" }
        log.info { "email = $email" }
        refreshTokenRepository.deleteByEmail(email)
        return email
    }
}