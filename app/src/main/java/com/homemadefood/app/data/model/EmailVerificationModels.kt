package com.homemadefood.app.data.model

data class VerifyEmailRequest(
    val email: String,
    val code: String
)

data class ResendEmailVerificationRequest(
    val email: String
)

data class EmailVerificationResponse(
    val email: String
)