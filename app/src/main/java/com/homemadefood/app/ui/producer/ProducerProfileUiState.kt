package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.ProducerApplicationStatusResponse

data class ProducerProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    val profile:
    ProducerApplicationStatusResponse? = null,

    val isEditing: Boolean = false,

    val businessName: String = "",
    val description: String = "",
    val address: String = "",
    val latitudeText: String = "",
    val longitudeText: String = "",
    val dailyCapacityText: String = "",
    val isAvailable: Boolean = false,

    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val canEdit: Boolean
        get() =
            profile?.isApproved == true &&
                    profile.verificationStatus
                        .trim()
                        .equals(
                            "Approved",
                            ignoreCase = true
                        )
}