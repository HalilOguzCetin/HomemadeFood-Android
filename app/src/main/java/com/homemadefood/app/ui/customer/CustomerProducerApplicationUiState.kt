package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.ProducerApplicationStatusResponse

data class CustomerProducerApplicationUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,

    val application:
    ProducerApplicationStatusResponse? = null,

    val isFormVisible: Boolean = false,

    val businessName: String = "",
    val description: String = "",
    val address: String = "",
    val latitudeText: String = "",
    val longitudeText: String = "",
    val dailyCapacityText: String = "",

    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val isRejected: Boolean
        get() =
            application
                ?.verificationStatus
                ?.trim()
                ?.equals(
                    "Rejected",
                    ignoreCase = true
                ) == true
}