package com.arffy.server.domian.qna.entity

import com.arffy.server.domian.BaseEntity
import com.arffy.server.domian.user.entity.User
import javax.persistence.*

@Entity
class QnaComment(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_id", nullable = false)
    val qna: Qna,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val comment: String,

    ) : BaseEntity()