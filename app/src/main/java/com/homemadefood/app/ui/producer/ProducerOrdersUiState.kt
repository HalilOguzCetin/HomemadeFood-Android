package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.ProducerOrderResponse

data class ProducerOrdersUiState(
    val isLoading: Boolean = true,

    val orders: List<ProducerOrderResponse> =
        emptyList(),

    val updatingOrderId: Int? = null,

    val successMessage: String? = null,
    val errorMessage: String? = null
)