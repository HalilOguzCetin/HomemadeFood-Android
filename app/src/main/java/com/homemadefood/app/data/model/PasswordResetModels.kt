package com.homemadefood.app.data.model

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)

data class PasswordResetResponse(
    val email: String
)