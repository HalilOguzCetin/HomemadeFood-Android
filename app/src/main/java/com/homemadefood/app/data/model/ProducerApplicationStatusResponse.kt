package com.homemadefood.app.data.model

data class ProducerApplicationStatusResponse(
    val producerProfileId: Int,
    val businessName: String,
    val description: String,
    val address: String,

    val latitude: Double,
    val longitude: Double,

    val dailyCapacity: Int,
    val remainingCapacity: Int,

    val isAvailable: Boolean,
    val isApproved: Boolean,

    val verificationStatus: String,

    val createdAt: String,
    val approvedAt: String?,
    val rejectedAt: String?,
    val rejectionReason: String?
)