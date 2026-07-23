package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.model.FoodResponse

data class CustomerHomeUiState(
    val isCategoriesLoading: Boolean = false,

    val categories: List<CategoryResponse> =
        emptyList(),

    val selectedCategoryId: Int? = null,

    val searchQuery: String = "",

    val isFoodsLoading: Boolean = false,

    val foods: List<FoodResponse> =
        emptyList(),

    val errorMessage: String? = null
)