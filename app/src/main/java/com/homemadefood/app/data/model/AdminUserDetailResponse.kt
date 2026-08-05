package com.homemadefood.app.data.model

data class AdminUserDetailResponse(
    val userId: Int,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: String,

    val addressCount: Int,
    val orderCount: Int,
    val reviewCount: Int,
    val favoriteCount: Int,

    val producerProfileId: Int?,
    val businessName: String?,
    val producerVerificationStatus: String?,
    val isProducerApproved: Boolean?,
    val isProducerAvailable: Boolean?,
    val dailyCapacity: Int?,
    val remainingCapacity: Int?
)