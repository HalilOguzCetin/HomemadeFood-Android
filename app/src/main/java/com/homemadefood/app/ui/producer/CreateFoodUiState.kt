package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.model.FoodResponse

data class CreateFoodUiState(
    val categories: List<CategoryResponse> = emptyList(),
    val selectedCategoryId: Int? = null,
    val selectedCategoryName: String = "",
    val isCategoriesLoading: Boolean = true,
    val categoryErrorMessage: String? = null,

    val name: String = "",
    val description: String = "",
    val ingredients: String = "",
    val price: String = "",
    val preparationTimeMinutes: String = "",

    // Photo Picker'dan seçilen yerel görsel URI'si.
    // Bu değer backend'e ImageUrl olarak gönderilmez.
    val selectedImageUri: String? = null,

    val isSaving: Boolean = false,

    val createdFood: FoodResponse? = null,

    val successMessage: String? = null,
    val errorMessage: String? = null
) {
    val canSave: Boolean
        get() =
            !isSaving &&
                    !isCategoriesLoading &&
                    categoryErrorMessage == null &&
                    selectedCategoryId != null &&
                    name.isNotBlank() &&
                    description.isNotBlank() &&
                    ingredients.isNotBlank() &&
                    !selectedImageUri.isNullOrBlank()
}