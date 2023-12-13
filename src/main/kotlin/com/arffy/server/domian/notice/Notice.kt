package com.arffy.server.domian.notice

import com.arffy.server.domian.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Notice(

    @Column(nullable = false)
    val title: String,

    var content: String? = null,

    @Column(nullable = false)
    val topFlag: Boolean,

    ) : BaseEntity()