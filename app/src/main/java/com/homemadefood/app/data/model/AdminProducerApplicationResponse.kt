package com.homemadefood.app.data.model

data class AdminProducerApplicationResponse(
    val producerProfileId: Int,
    val userId: Int,

    val fullName: String,
    val email: String,
    val userRole: String,

    val businessName: String,
    val description: String,

    /*
     * Üreticinin işletme/vitrin görselidir.
     * Food.ImageUrl ile aynı kavram değildir.
     *
     * Nullable/default tutulması eski başvuruların
     * görselsiz kalabilmesini güvenli şekilde destekler.
     */
    val businessImageUrl: String? = null,

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
    val approvedByAdminId: Int?,

    val rejectedAt: String?,
    val rejectedByAdminId: Int?,

    val rejectionReason: String?
)