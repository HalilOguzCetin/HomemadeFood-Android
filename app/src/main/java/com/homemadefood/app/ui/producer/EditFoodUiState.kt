package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.model.FoodProductionOptions
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
    val ingredients: String = "",
    val price: String = "",
    val preparationTimeMinutes: String = "",

    val selectedUnitType: String = "Portion",
    val dailyCapacity: String = "1",
    val minimumOrderLeadTimeMinutes: String = "0",
    val selectedAllergenCodes: Set<String> = emptySet(),

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
                    name.isNotBlank() &&
                    description.isNotBlank() &&
                    ingredients.isNotBlank() &&
                    price.replace(",", ".")
                        .toDoubleOrNull()
                        ?.let { it > 0 } == true &&
                    preparationTimeMinutes
                        .toIntOrNull()
                        ?.let { it > 0 } == true &&
                    FoodProductionOptions.unitTypes
                        .any {
                            it.backendValue.equals(
                                selectedUnitType,
                                ignoreCase = true
                            )
                        } &&
                    dailyCapacity
                        .toIntOrNull()
                        ?.let { it in 1..10000 } == true &&
                    minimumOrderLeadTimeMinutes
                        .toIntOrNull()
                        ?.let { it in 0..10080 } == true &&
                    (
                            imageUrl.isNotBlank() ||
                                    !selectedImageUri.isNullOrBlank()
                            )
}
