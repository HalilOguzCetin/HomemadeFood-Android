package com.homemadefood.app.navigation

import com.homemadefood.app.data.model.AppMode
import com.homemadefood.app.data.model.UserRole

fun resolveStartDestination(
    isLoggedIn: Boolean,
    backendRole: String?,
    canUseProducerMode: Boolean,
    activeMode: AppMode?
): String {

    if (!isLoggedIn) {
        return AppGraph.AUTH
    }

    return when (
        UserRole.fromBackendValue(
            backendRole
        )
    ) {
        UserRole.CUSTOMER -> {
            if (
                canUseProducerMode &&
                activeMode == AppMode.PRODUCER
            ) {
                AppGraph.PRODUCER
            } else {
                AppGraph.CUSTOMER
            }
        }

        UserRole.ADMIN ->
            AppGraph.ADMIN

        UserRole.UNKNOWN ->
            AppGraph.AUTH
    }
}