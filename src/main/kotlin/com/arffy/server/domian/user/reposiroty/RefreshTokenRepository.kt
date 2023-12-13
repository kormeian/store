package com.arffy.server.domian.user.reposiroty

import com.arffy.server.domian.user.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByEmail(
        email: String
    ): RefreshToken?

    fun findByAccessToken(
        accessToken: String
    ): RefreshToken?

    fun deleteByAccessToken(
        accessToken: String
    )

    fun deleteByEmail(
        email: String
    )
}