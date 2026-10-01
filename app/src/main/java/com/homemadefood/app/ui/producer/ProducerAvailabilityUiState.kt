package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.ProducerAvailabilityResponse

data class ProducerAvailabilityUiState(
    val isLoading: Boolean = true,

    val isSavingBusinessHours:
    Boolean = false,

    val isUpdatingMode:
    Boolean = false,

    val availability:
    ProducerAvailabilityResponse? = null,

    val successMessage:
    String? = null,

    val errorMessage:
    String? = null
) {

    val isBusy: Boolean
        get() =
            isLoading ||
                    isSavingBusinessHours ||
                    isUpdatingMode
}
