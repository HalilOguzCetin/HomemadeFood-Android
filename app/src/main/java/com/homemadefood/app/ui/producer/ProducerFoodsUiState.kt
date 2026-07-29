package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.FoodResponse

data class ProducerFoodsUiState(
    val isLoading: Boolean = true,

    val foods: List<FoodResponse> =
        emptyList(),

    val errorMessage: String? = null
)