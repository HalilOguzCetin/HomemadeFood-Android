package com.homemadefood.app.data.model

data class PendingProducerResponse(
    val producerProfileId: Int,
    val userId: Int,

    val fullName: String,
    val email: String,

    val businessName: String,
    val description: String,
    val address: String,

    val dailyCapacity: Int,

    val verificationStatus: String,
    val createdAt: String
)