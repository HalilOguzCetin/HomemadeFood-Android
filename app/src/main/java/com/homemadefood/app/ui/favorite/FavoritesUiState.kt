package com.homemadefood.app.ui.favorite

import com.homemadefood.app.data.model.FavoriteResponse

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favorites: List<FavoriteResponse> =
        emptyList(),
    val errorMessage: String? = null,
    val actionMessage: String? = null,
    val removingFoodId: Int? = null
)