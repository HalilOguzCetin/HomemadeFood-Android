package com.homemadefood.app.data.model

data class LoginResponse(
    val userId: Int,
    val fullName: String,
    val email: String,
    val role: String,
    val token: String
)