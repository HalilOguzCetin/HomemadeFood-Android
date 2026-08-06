package com.homemadefood.app.ui.auth

import com.homemadefood.app.data.model.AppMode

data class AuthUiState(
    val isSessionChecking: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val registrationSuccessful: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,

    val userRole: String? = null,

    val canUseProducerMode: Boolean = false,

    val producerProfileId: Int? = null,

    val producerVerificationStatus: String? = null,

    val activeMode: AppMode? = null
)