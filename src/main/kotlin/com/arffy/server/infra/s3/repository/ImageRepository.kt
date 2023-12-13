package com.arffy.server.infra.s3.repository

import com.arffy.server.infra.s3.constant.ImageType
import com.arffy.server.infra.s3.entity.ImageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : JpaRepository<ImageEntity, Long> {
    fun findByIdIn(ids: List<Long>): List<ImageEntity>?
    fun findByImageTypeAndDivideIdOrderByIdAsc(imageType: ImageType, divideId: Long): List<ImageEntity>?
}
