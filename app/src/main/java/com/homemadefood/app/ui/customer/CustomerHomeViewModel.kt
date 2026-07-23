package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CustomerHomeViewModel(
    private val categoryRepository:
    CategoryRepository,

    private val foodRepository:
    FoodRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            CustomerHomeUiState(
                isCategoriesLoading = true,
                isFoodsLoading = true
            )
        )

    val uiState:
            StateFlow<CustomerHomeUiState> =
        _uiState.asStateFlow()

    init {
        loadCategories()
        loadFoods()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isCategoriesLoading = true,
                    errorMessage = null
                )

            try {
                val response =
                    categoryRepository
                        .getCategories()

                val responseBody =
                    response.body()

                val categories =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    categories != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            isCategoriesLoading = false,

                            categories =
                                categories.filter {
                                    it.isActive != false
                                },

                            errorMessage = null
                        )
                } else {
                    showCategoryError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Kategoriler alınamadı."
                    )
                }
            } catch (_: IOException) {
                showCategoryError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showCategoryError(
                    "Kategoriler yüklenirken bir hata oluştu."
                )
            }
        }
    }

    fun loadFoods(
        categoryId: Int? =
            _uiState.value.selectedCategoryId,

        search: String =
            _uiState.value.searchQuery
    ) {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isFoodsLoading = true,
                    errorMessage = null
                )

            try {
                val normalizedSearch =
                    search
                        .trim()
                        .takeIf {
                            it.isNotBlank()
                        }

                val response =
                    foodRepository.getFoods(
                        categoryId = categoryId,
                        search = normalizedSearch
                    )

                val responseBody =
                    response.body()

                val foods =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    foods != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            isFoodsLoading = false,

                            foods =
                                foods.filter {
                                    it.isAvailable
                                },

                            errorMessage = null
                        )
                } else {
                    showFoodError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Yemekler alınamadı."
                    )
                }
            } catch (_: IOException) {
                showFoodError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showFoodError(
                    "Yemekler yüklenirken bir hata oluştu."
                )
            }
        }
    }

    fun updateSearchQuery(
        query: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                searchQuery = query
            )
    }

    fun searchFoods() {
        loadFoods()
    }

    fun selectCategory(
        categoryId: Int?
    ) {
        _uiState.value =
            _uiState.value.copy(
                selectedCategoryId =
                    categoryId
            )

        loadFoods(
            categoryId = categoryId
        )
    }

    fun clearFilters() {
        _uiState.value =
            _uiState.value.copy(
                selectedCategoryId = null,
                searchQuery = ""
            )

        loadFoods(
            categoryId = null,
            search = ""
        )
    }

    private fun showCategoryError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isCategoriesLoading = false,
                errorMessage = message
            )
    }

    private fun showFoodError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isFoodsLoading = false,
                errorMessage = message
            )
    }

    private fun parseErrorMessage(
        errorJson: String?
    ): String? {
        if (errorJson.isNullOrBlank()) {
            return null
        }

        return runCatching {
            JSONObject(errorJson)
                .optString("message")
                .takeIf {
                    it.isNotBlank()
                }
        }.getOrNull()
    }
}