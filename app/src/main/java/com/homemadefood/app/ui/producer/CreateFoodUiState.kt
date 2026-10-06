package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.model.FoodProductionOptions
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

    val selectedUnitType: String = "Portion",
    val dailyCapacity: String = "1",
    val minimumOrderLeadTimeMinutes: String = "0",
    val selectedAllergenCodes: Set<String> = emptySet(),

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
                    !selectedImageUri.isNullOrBlank()
}
