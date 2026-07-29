package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.FoodResponse

data class EditFoodUiState(
    val foodId: Int? = null,

    val categoryId: String = "",
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val preparationTimeMinutes: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,

    val updatedFood: FoodResponse? = null,

    val successMessage: String? = null,
    val errorMessage: String? = null
)