package com.homemadefood.app.data.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String
)