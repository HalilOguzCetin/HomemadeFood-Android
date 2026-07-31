package com.homemadefood.app.navigation

import com.homemadefood.app.data.model.UserRole

fun resolveStartDestination(
    isLoggedIn: Boolean,
    backendRole: String?
): String {

    if (!isLoggedIn) {
        return AppGraph.AUTH
    }

    return when (
        UserRole.fromBackendValue(
            backendRole
        )
    ) {
        UserRole.CUSTOMER ->
            AppGraph.CUSTOMER

        UserRole.PRODUCER ->
            AppGraph.PRODUCER

        UserRole.ADMIN ->
            AppGraph.ADMIN

        UserRole.UNKNOWN ->
            AppGraph.AUTH
    }
}