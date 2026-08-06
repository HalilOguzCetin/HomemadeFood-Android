package com.homemadefood.app.data.model

enum class AppMode {
    CUSTOMER,
    PRODUCER;

    companion object {

        fun fromStoredValue(
            value: String?
        ): AppMode? {
            if (value.isNullOrBlank()) {
                return null
            }

            return entries.firstOrNull { mode ->
                mode.name.equals(
                    value.trim(),
                    ignoreCase = true
                )
            }
        }
    }
}