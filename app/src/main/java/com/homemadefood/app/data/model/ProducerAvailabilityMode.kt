package com.homemadefood.app.data.model

enum class ProducerAvailabilityMode(
    val backendValue: String,
    val displayName: String
) {
    SCHEDULED(
        backendValue = "Scheduled",
        displayName = "Otomatik"
    ),

    FORCE_OPEN(
        backendValue = "ForceOpen",
        displayName = "Şimdi Açık"
    ),

    FORCE_CLOSED(
        backendValue = "ForceClosed",
        displayName = "Şimdi Kapalı"
    );

    companion object {

        fun fromBackendValue(
            value: String?
        ): ProducerAvailabilityMode? {

            return entries.firstOrNull {
                it.backendValue.equals(
                    value,
                    ignoreCase = true
                )
            }
        }
    }
}