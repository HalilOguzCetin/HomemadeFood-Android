package com.homemadefood.app.ui.auth

data class AuthUiState(
    val isSessionChecking: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val registrationSuccessful: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
    val userRole: String? = null
)