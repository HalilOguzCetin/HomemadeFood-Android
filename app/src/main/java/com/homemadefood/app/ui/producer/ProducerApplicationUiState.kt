package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.ProducerApplicationStatusResponse

data class ProducerApplicationUiState(
    val isLoading: Boolean = true,

    val application:
    ProducerApplicationStatusResponse? = null,

    val errorMessage: String? = null
)