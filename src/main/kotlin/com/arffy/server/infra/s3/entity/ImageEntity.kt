package com.arffy.server.infra.s3.entity

import com.arffy.server.domian.BaseEntity
import com.arffy.server.infra.s3.constant.ImageType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "image")
class ImageEntity(
    val imageUrl: String,
    val divideId: Long,

    @Enumerated(EnumType.STRING)
    val imageType: ImageType
) : BaseEntity()
