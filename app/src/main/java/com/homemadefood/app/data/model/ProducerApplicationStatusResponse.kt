package com.homemadefood.app.data.model

data class ProducerApplicationStatusResponse(
    val producerProfileId: Int,
    val businessName: String,
    val description: String,
    val businessImageUrl: String? = null,
    val address: String,

    val city: String = "",
    val district: String = "",
    val neighborhood: String = "",
    val street: String = "",
    val buildingNo: String = "",
    val floor: String? = null,
    val apartmentNo: String? = null,
    val addressNote: String? = null,

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