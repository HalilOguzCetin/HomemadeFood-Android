package com.homemadefood.app.ui.admin

enum class AdminUserRoleFilter(
    val backendValue: String?,
    val displayName: String
) {
    ALL(
        backendValue = null,
        displayName = "Tümü"
    ),

    CUSTOMER(
        backendValue = "Customer",
        displayName = "Customer"
    ),

    PRODUCER(
        backendValue = "Producer",
        displayName = "Producer"
    ),

    ADMIN(
        backendValue = "Admin",
        displayName = "Admin"
    )
}

enum class AdminUserStatusFilter(
    val backendValue: Boolean?,
    val displayName: String
) {
    ALL(
        backendValue = null,
        displayName = "Tümü"
    ),

    ACTIVE(
        backendValue = true,
        displayName = "Aktif"
    ),

    PASSIVE(
        backendValue = false,
        displayName = "Pasif"
    )
}