package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.ProducerStorefrontMenuResponse

data class StorefrontMenuUiState(
    val isLoading: Boolean = true,

    val menu:
    ProducerStorefrontMenuResponse? =
        null,

    val errorMessage: String? = null
)