package com.homemadefood.app.ui.admin

import com.homemadefood.app.data.model.PendingProducerResponse

data class AdminApplicationsUiState(
    val isLoading: Boolean = true,

    val applications: List<PendingProducerResponse> =
        emptyList(),

    val updatingApplicationId: Int? = null,

    val successMessage: String? = null,

    val errorMessage: String? = null
)