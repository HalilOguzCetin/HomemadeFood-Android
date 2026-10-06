package com.homemadefood.app.data.model

data class ProducerComplianceResponse(
    val producerProfileId: Int,
    val taxStatus: String,
    val taxNumber: String? = null,
    val taxExemptionCertificateNumber: String? = null,
    val foodBusinessRegistrationNumber: String? = null,
    val foodRegistrationStatus: String,
    val paymentAccountStatus: String,
    val complianceStatus: String,
    val isOfficiallyVerified: Boolean,
    val lastReviewedAt: String? = null,
    val reviewNote: String? = null,
    val createdAt: String,
    val updatedAt: String
)
