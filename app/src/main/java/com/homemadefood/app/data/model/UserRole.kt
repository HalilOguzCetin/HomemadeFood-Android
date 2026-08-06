package com.homemadefood.app.data.model

enum class UserRole {
    CUSTOMER,
    ADMIN,
    UNKNOWN;

    companion object {

        fun fromBackendValue(
            value: String?
        ): UserRole {
            val normalizedValue =
                value?.trim()

            return when {
                normalizedValue.equals(
                    "Customer",
                    ignoreCase = true
                ) -> CUSTOMER

                normalizedValue.equals(
                    "Admin",
                    ignoreCase = true
                ) -> ADMIN

                else -> UNKNOWN
            }
        }
    }
}