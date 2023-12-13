package com.arffy.server.domian.qna.entity

import com.arffy.server.domian.BaseEntity
import com.arffy.server.domian.qna.exception.QnaErrorCode
import com.arffy.server.domian.user.entity.User
import com.arffy.server.global.exception.RestApiException
import javax.persistence.*

@Entity
class Qna(

    @Column(nullable = false)
    val title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val content: String,

    val productId: Long?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val qnaType: QnaType,

    @Column(nullable = false)
    val isAnswered: Boolean,

    ) : BaseEntity()

enum class QnaType {
    PRODUCT,
    DELIVERY,
    ETC
    ;

    companion object {
        fun from(qnaType: String): QnaType {
            return values().find { it.name == qnaType } ?: throw RestApiException(QnaErrorCode.QNA_TYPE_IS_NOT_MATCHED)
        }
    }
}