package com.homemadefood.app.data.model

enum class ProducerApplicationStatus(
    val backendValue: String,
    val displayName: String
) {
    PENDING(
        backendValue = "Pending",
        displayName = "Bekleyen"
    ),

    APPROVED(
        backendValue = "Approved",
        displayName = "Onaylanan"
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

                APPROVED ->
                    "Onaylandı"

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