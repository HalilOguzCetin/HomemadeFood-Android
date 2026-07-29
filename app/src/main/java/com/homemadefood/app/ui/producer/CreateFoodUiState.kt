package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.FoodResponse

data class CreateFoodUiState(
    val categoryId: String = "",
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val preparationTimeMinutes: String = "",
    val imageUrl: String = "",

    val isSaving: Boolean = false,

    val createdFood: FoodResponse? = null,

    val successMessage: String? = null,
    val errorMessage: String? = null
)