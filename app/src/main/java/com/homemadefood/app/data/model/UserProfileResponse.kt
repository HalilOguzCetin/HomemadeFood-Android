package com.homemadefood.app.data.model

data class UserProfileResponse(
    val userId: Int,
    val fullName: String,
    val email: String,
    val role: String,

    val canUseProducerMode: Boolean = false,

    val producerProfileId: Int? = null,

    val producerVerificationStatus: String? = null
)