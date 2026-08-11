package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.FoodResponse

data class CreateFoodUiState(
    val categoryId: String = "",
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val preparationTimeMinutes: String = "",

    // Photo Picker'dan seçilen yerel görsel URI'si.
    // Bu değer backend'e ImageUrl olarak gönderilmez.
    val selectedImageUri: String? = null,

    val isSaving: Boolean = false,

    val createdFood: FoodResponse? = null,

    val successMessage: String? = null,
    val errorMessage: String? = null
)