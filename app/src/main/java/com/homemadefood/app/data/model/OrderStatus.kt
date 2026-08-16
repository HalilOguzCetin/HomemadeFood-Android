package com.homemadefood.app.data.model

enum class OrderStatus(
    val backendValue: String,
    val displayName: String
) {
    PENDING(
        backendValue = "Pending",
        displayName = "Onay Bekliyor"
    ),

    ACCEPTED(
        backendValue = "Accepted",
        displayName = "Kabul Edildi"
    ),

    PREPARING(
        backendValue = "Preparing",
        displayName = "Hazırlanıyor"
    ),

    READY(
        backendValue = "Ready",
        displayName = "Hazır"
    ),

    OUT_FOR_DELIVERY(
        backendValue = "OutForDelivery",
        displayName = "Teslimatta"
    ),

    DELIVERED(
        backendValue = "Delivered",
        displayName = "Teslim Edildi"
    ),

    REJECTED(
        backendValue = "Rejected",
        displayName = "Reddedildi"
    ),

    CANCELLED(
        backendValue = "Cancelled",
        displayName = "İptal Edildi"
    ),

    UNKNOWN(
        backendValue = "",
        displayName = "Bilinmeyen Durum"
    );

    companion object {

        fun fromBackendValue(
            value: String?
        ): OrderStatus {

            val normalizedValue =
                value?.trim()

            return entries.firstOrNull { status ->
                status.backendValue.equals(
                    normalizedValue,
                    ignoreCase = true
                )
            } ?: UNKNOWN
        }

        fun displayNameFor(
            value: String?
        ): String {
            val status =
                fromBackendValue(value)

            return if (status == UNKNOWN) {
                value
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
                    ?: UNKNOWN.displayName
            } else {
                status.displayName
            }
        }
    }
}