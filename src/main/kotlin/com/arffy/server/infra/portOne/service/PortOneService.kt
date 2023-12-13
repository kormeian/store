package com.arffy.server.infra.portOne.service

import com.siot.IamportRestClient.response.Payment
import com.siot.IamportRestClient.response.Prepare

interface PortOneService {
    fun postPrepare(
        merchantUid: String,
        amount: Int,
    ): Prepare

    fun getPayment(
        impUid: String,
    ): Payment
}