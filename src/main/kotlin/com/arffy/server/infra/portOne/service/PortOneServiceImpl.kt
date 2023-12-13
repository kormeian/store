package com.arffy.server.infra.portOne.service

import com.arffy.server.global.exception.RestApiException
import com.arffy.server.infra.portOne.exception.PortOneErrorCode
import com.siot.IamportRestClient.IamportClient
import com.siot.IamportRestClient.exception.IamportResponseException
import com.siot.IamportRestClient.request.PrepareData
import com.siot.IamportRestClient.response.IamportResponse
import com.siot.IamportRestClient.response.Payment
import com.siot.IamportRestClient.response.Prepare
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal

private val log = KotlinLogging.logger {}

@Service
class PortOneServiceImpl(
    val iamportClient: IamportClient
) : PortOneService {
    override fun getPayment(
        impUid: String
    ): Payment {
        log.info { "PortOneServiceImpl.getPayment" }
        log.info { "impUid = $impUid" }
        try {
            val payment: IamportResponse<Payment> = iamportClient.paymentByImpUid(impUid)
            if (payment.code != 0) {
                throw RestApiException(PortOneErrorCode.BAD_REQUEST, payment.message ?: "잘못된 요청입니다.")
            }
            return payment.response
        } catch (e: IamportResponseException) {
            log.error { "getPayment error : ${e.message}" }
            throw extracted(e)
        } catch (e: RestApiException) {
            log.error { "getPayment error : ${e.message}" }
            throw e
        }
    }

    override fun postPrepare(
        merchantUid: String,
        amount: Int
    ): Prepare {
        log.info { "PortOneServiceImpl.postPrepare" }
        log.info { "merchantUid = $merchantUid, amount = $amount" }
        try {
            val prepare = iamportClient.postPrepare(
                PrepareData(
                    merchantUid,
                    BigDecimal(amount)
                )
            )
            if (prepare.code != 0) {
                throw RestApiException(PortOneErrorCode.BAD_REQUEST, prepare.message ?: "잘못된 요청입니다.")
            }
            return prepare.response
        } catch (e: IamportResponseException) {
            log.error { "postPrepare error : ${e.message}" }
            throw extracted(e)
        } catch (e: RestApiException) {
            log.error { "postPrepare error : ${e.message}" }
            throw e
        }
    }

    private fun extracted(e: IamportResponseException): RestApiException {
        return when (e.httpStatusCode) {
            401 -> {
                RestApiException(PortOneErrorCode.NOT_AUTHORIZED)
            }

            else -> {
                RestApiException(PortOneErrorCode.INTERNAL_SERVER_ERROR, e.message ?: "포트원 서버 내부 에러")
            }
        }
    }
}