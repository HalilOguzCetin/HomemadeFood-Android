package com.homemadefood.app.data.model

enum class ProducerApplicationStatus(
    val backendValue: String,
    val displayName: String
) {
    PENDING(
        backendValue = "Pending",
        displayName = "Bekleyen"
    ),

    UNDER_REVIEW(
        backendValue = "UnderReview",
        displayName = "İnceleniyor"
    ),

    ADDITIONAL_DOCUMENT_REQUIRED(
        backendValue = "AdditionalDocumentRequired",
        displayName = "Ek Belge Gerekli"
    ),

    APPROVED(
        backendValue = "Approved",
        displayName = "Onaylanan"
    ),

    SUSPENDED(
        backendValue = "Suspended",
        displayName = "Askıya Alındı"
    ),

    REJECTED(
        backendValue = "Rejected",
        displayName = "Reddedilen"
    );

    companion object {
        fun fromBackendValue(
            value: String?
        ): ProducerApplicationStatus? {
            val normalizedValue =
                value?.trim()

            return entries.firstOrNull { status ->
                status.backendValue.equals(
                    normalizedValue,
                    ignoreCase = true
                )
            }
        }

        fun detailDisplayNameFor(
            value: String?
        ): String {
            return when (
                fromBackendValue(value)
            ) {
                PENDING ->
                    "Onay Bekliyor"

                UNDER_REVIEW ->
                    "İnceleniyor"

                ADDITIONAL_DOCUMENT_REQUIRED ->
                    "Ek Bilgi / Belge Gerekli"

                APPROVED ->
                    "Onaylandı"

                SUSPENDED ->
                    "Askıya Alındı"

                REJECTED ->
                    "Reddedildi"

                null ->
                    value
                        ?.trim()
                        ?.takeIf { it.isNotBlank() }
                        ?: "Bilinmeyen Durum"
            }
        }
    }
}
