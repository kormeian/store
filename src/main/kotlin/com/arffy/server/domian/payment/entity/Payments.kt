package com.arffy.server.domian.payment.entity

import com.arffy.server.domian.BaseEntity
import com.arffy.server.domian.order.entity.Orders
import com.siot.IamportRestClient.response.Payment
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
class Payments(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", nullable = false)
    val orders: Orders,

    @Enumerated(EnumType.STRING)
    val payMethod: PayMethodType? = null,
    val channel: String? = null,
    @Enumerated(EnumType.STRING)
    val pgProvider: PgProviderType? = null,
    val embPgProvider: String? = null,
    val pgTid: String? = null,
    val escrow: Boolean? = null,
    val applyNum: String? = null,
    val bankCode: String? = null,
    val bankName: String? = null,
    val cardCode: String? = null,
    val cardName: String? = null,
    val cardNumber: String? = null,
    val cardQuota: Int? = null,
    val cardType: Int? = null,
    val vbankCode: String? = null,
    val vbankName: String? = null,
    val vbankNum: String? = null,
    val vbankHolder: String? = null,
    val vbankDate: LocalDateTime? = null,
    val vbankIssuedAt: LocalDateTime? = null,

    val name: String? = null,
    val amount: Int? = null,
    val cancelAmount: Int? = null,
    val currency: String? = null,

    val buyerName: String? = null,
    val buyerEmail: String? = null,
    val buyerTel: String? = null,
    val buyerAddr: String? = null,
    val buyerPostCode: String? = null,

    val customData: String? = null,
    val status: String? = null,

    val startedAt: LocalDateTime? = null,
    val paidAt: LocalDateTime? = null,
    val failedAt: LocalDateTime? = null,
    val cancelledAt: LocalDateTime? = null,

    val failReason: String? = null,
    val cancelReason: String? = null,
    val receiptUrl: String? = null,
    val cashReceiptIssued: Boolean? = null,
    val customerUid: String? = null,
    val customerUidUsage: String? = null,
    @Enumerated(EnumType.STRING)
    val callbackFrom: Callback? = null,
) : BaseEntity() {
    companion object {
        fun from(payment: Payment, order: Orders, callback: Callback): Payments {
            return Payments(
                orders = order,
                payMethod = PayMethodType.from(payment.payMethod),
                channel = payment.channel,
                pgProvider = PgProviderType.from(payment.pgProvider),
                embPgProvider = payment.embPgProvider,
                pgTid = payment.pgTid,
                escrow = payment.isEscrow,
                applyNum = payment.applyNum,
                bankCode = payment.bankCode,
                bankName = payment.bankName,
                cardCode = payment.cardCode,
                cardName = payment.cardName,
                cardNumber = payment.cardNumber,
                cardQuota = payment.cardQuota,
                cardType = payment.cardType,
                vbankCode = payment.vbankCode,
                vbankName = payment.vbankName,
                vbankNum = payment.vbankNum,
                vbankHolder = payment.vbankHolder,
                vbankDate = if (payment.vbankDate == null) null
                else payment.vbankDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                vbankIssuedAt = if (payment.vbankIssuedAt == null) null
                else LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(payment.vbankIssuedAt),
                    ZoneId.systemDefault()
                ),
                name = payment.name,
                amount = if (payment.amount == null) null
                else payment.amount.intValueExact(),
                cancelAmount = if (payment.cancelAmount == null) null
                else payment.cancelAmount.intValueExact(),
                currency = payment.currency,
                buyerName = payment.buyerName,
                buyerEmail = payment.buyerEmail,
                buyerTel = payment.buyerTel,
                buyerAddr = payment.buyerAddr,
                buyerPostCode = payment.buyerPostcode,
                customData = payment.customData,
                status = payment.status,
                startedAt = if (payment.startedAt == null) null
                else LocalDateTime.ofInstant(Instant.ofEpochMilli(payment.startedAt), ZoneId.systemDefault()),
                paidAt = if (payment.paidAt == null) null
                else payment.paidAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                failedAt = if (payment.failedAt == null) null
                else payment.failedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                cancelledAt = if (payment.cancelledAt == null) null
                else payment.cancelledAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                failReason = payment.failReason,
                cancelReason = payment.cancelReason,
                receiptUrl = payment.receiptUrl,
                cashReceiptIssued = payment.isCashReceiptIssued,
                customerUid = payment.customerUid,
                customerUidUsage = payment.customerUidUsage,
                callbackFrom = callback
            )
        }
    }
}

enum class Callback {
    PORT_ONE, CLIENT
}

enum class PayMethodType(
    val text: String,
) {
    CARD("신용카드"),
    TRANS("실시간계좌이체"),
    VBANK("가상계좌"),
    PHONE("휴대폰소액결제"),
    SAMSUNG("삼성페이"),
    KPAY("KPAY앱"),
    KAKAOPAY("카카오페이"),
    PAYCO("페이코"),
    LPAY("LPAY"),
    SSGPAY("SSG페이"),
    TOSSPAY("토스간편결제"),
    CULTURELAND("문화상품권"),
    SMARTCULTURE("스마트문상"),
    HAPPYMONEY("해피머니"),
    BOOKNLIFE("도서문화상품권"),
    POINT("포인트 결제"),
    WECHAT("위쳇페이"),
    ALIPAY("알리페이"),
    UNIONPAY("유니온페이"),
    TENPAY("텐페이");

    companion object {
        fun from(payMethod: String): PayMethodType? {
            return values().find { it.name.equals(payMethod, true) }
        }
    }
}


enum class PgProviderType(
    val text: String,
) {
    HTML5_INICIS("이니시스웹표준"),
    INICIS("이니시스ActiveX결제창"),
    KCP("NHN KCP"),
    KCP_BILLING("NHN KCP 정기결제"),
    UPLUS("토스페이먼츠(구 LG U+)"),
    NICE("나이스페이"),
    JTNET("JTNET"),
    KICC("한국정보통신"),
    BLUEWALNUT("블루월넛"),
    KAKAOPAY("카카오페이"),
    DANAL("다날휴대폰소액결제"),
    DANAL_TPAY("다날일반결제"),
    MOBILIANS("모빌리언스 휴대폰소액결제"),
    CHAI("차이 간편결제"),
    SYRUP("시럽페이"),
    PAYCO("페이코"),
    PAYPAL("페이팔"),
    EXIMBAY("엑심베이"),
    NAVERPAY("네이버페이-결제형"),
    NAVERCO("네이버페이-주문형"),
    SMILEPAY("스마일페이"),
    ALIPAY("알리페이"),
    PAYMENTWALL("페이먼트월"),
    PAYPLE("페이플"),
    TOSSPAY("토스간편결제"),
    SMARTRO("스마트로"),
    SETTLE("세틀뱅크");

    companion object {
        fun from(pgProvider: String): PgProviderType? {
            return values().find { it.name.equals(pgProvider, true) }
        }
    }
}