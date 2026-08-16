package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.UserProfileResponse

data class CustomerProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isEditing: Boolean = false,

    val profile: UserProfileResponse? = null,

    val fullName: String = "",

    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val canSave: Boolean
        get() {
            val normalizedFullName =
                fullName.trim()

            return isEditing &&
                    !isSaving &&
                    normalizedFullName.length in 2..100 &&
                    normalizedFullName !=
                    profile?.fullName?.trim()
        }
}