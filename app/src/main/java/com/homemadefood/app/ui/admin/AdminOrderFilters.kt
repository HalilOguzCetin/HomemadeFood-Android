package com.homemadefood.app.ui.admin

enum class AdminOrderStatusFilter(
    val backendValue: String?,
    val displayName: String
) {
    ALL(
        backendValue = null,
        displayName = "Tümü"
    ),

    PENDING(
        backendValue = "Pending",
        displayName = "Bekliyor"
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
        displayName = "Yolda"
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
    );

    companion object {
        fun fromBackendValue(
            value: String?
        ): AdminOrderStatusFilter? {
            if (value.isNullOrBlank()) {
                return null
            }

            return entries.firstOrNull { status ->
                status.backendValue.equals(
                    value.trim(),
                    ignoreCase = true
                )
            }
        }
    }
}