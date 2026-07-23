package com.homemadefood.app.data.model

data class UserProfileResponse(
    val userId: String,
    val fullName: String,
    val email: String,
    val role: String
)