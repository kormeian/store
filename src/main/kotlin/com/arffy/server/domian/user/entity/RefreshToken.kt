package com.arffy.server.domian.user.entity

import com.arffy.server.domian.BaseEntity
import com.arffy.server.global.jpaConverter.ColumnEncryptor
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity

@Entity
class RefreshToken(
    @Column(nullable = false)
    var token: String,

    @Column(nullable = false)
    var accessToken: String,

    @Column(nullable = false)
    @Convert(converter = ColumnEncryptor::class)
    val email: String,
) : BaseEntity()