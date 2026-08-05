package com.homemadefood.app.data.model

data class AdminUserListItemResponse(
    val userId: Int,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: String,
    val producerProfileId: Int?,
    val businessName: String?,
    val producerVerificationStatus: String?
)