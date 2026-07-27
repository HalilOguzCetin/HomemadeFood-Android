package com.homemadefood.app.ui.food

import com.homemadefood.app.data.model.FoodResponse

data class FoodDetailUiState(
    val isLoading: Boolean = false,
    val food: FoodResponse? = null,
    val errorMessage: String? = null,

    val isFavorite: Boolean = false,
    val isFavoriteChecking: Boolean = false,
    val isFavoriteActionLoading: Boolean = false,
    val favoriteMessage: String? = null,
    val isFavoriteError: Boolean = false,

    val isCartActionLoading: Boolean = false,
    val cartMessage: String? = null,
    val isCartError: Boolean = false
)