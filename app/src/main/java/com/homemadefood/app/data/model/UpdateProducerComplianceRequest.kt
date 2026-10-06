package com.homemadefood.app.data.model

data class UpdateProducerComplianceRequest(
    val taxStatus: String,
    val taxNumber: String? = null,
    val taxExemptionCertificateNumber: String? = null,
    val foodBusinessRegistrationNumber: String? = null,
    val foodRegistrationStatus: String
)
