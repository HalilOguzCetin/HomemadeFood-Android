package com.homemadefood.app.data.model

data class RequestPhoneVerificationRequest(
    val phone: String
)

data class VerifyPhoneRequest(
    val phone: String,
    val code: String
)

data class PhoneVerificationRequestResponse(
    val phone: String
)