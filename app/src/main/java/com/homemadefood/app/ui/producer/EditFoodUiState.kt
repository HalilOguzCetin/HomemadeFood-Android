package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.model.FoodResponse

data class EditFoodUiState(
    val foodId: Int? = null,

    val categories: List<CategoryResponse> = emptyList(),
    val selectedCategoryId: Int? = null,
    val selectedCategoryName: String = "",
    val isCategoriesLoading: Boolean = true,
    val categoryErrorMessage: String? = null,

    val name: String = "",
    val description: String = "",
    val price: String = "",
    val preparationTimeMinutes: String = "",

    // Backend'de kayıtlı mevcut fotoğraf.
    // Kullanıcı yeni fotoğraf seçmezse bu görsel korunur.
    val imageUrl: String = "",

    // Photo Picker'dan seçilen yeni yerel fotoğraf URI'si.
    // Null ise update sırasında mevcut backend fotoğrafı korunur.
    val selectedImageUri: String? = null,

    val isAvailable: Boolean = true,

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,

    val updatedFood: FoodResponse? = null,

    val successMessage: String? = null,
    val errorMessage: String? = null
) {
    val canSave: Boolean
        get() =
            !isLoading &&
                    !isSaving &&
                    !isCategoriesLoading &&
                    categoryErrorMessage == null &&
                    selectedCategoryId != null &&
                    (
                            imageUrl.isNotBlank() ||
                                    !selectedImageUri.isNullOrBlank()
                            )
}