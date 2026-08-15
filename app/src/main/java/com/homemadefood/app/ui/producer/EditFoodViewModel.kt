package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.UpdateFoodRequest
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.ProducerFoodRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class EditFoodViewModel(
    private val producerFoodRepository:
    ProducerFoodRepository,

    private val categoryRepository:
    CategoryRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var categoryLoadJob: Job? = null

    private val _uiState =
        MutableStateFlow(EditFoodUiState())

    val uiState: StateFlow<EditFoodUiState> =
        _uiState.asStateFlow()

    fun loadFood(foodId: Int) {
        if (
            _uiState.value.foodId == foodId &&
            _uiState.value.name.isNotBlank()
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                EditFoodUiState(
                    foodId = foodId,
                    isLoading = true,
                    isCategoriesLoading = true
                )

            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        isCategoriesLoading = false,
                        errorMessage =
                            "Oturum bilgisi bulunamadı."
                    )
                return@launch
            }

            loadCategoriesInternal()

            try {
                val response =
                    producerFoodRepository
                        .getFoodById(
                            foodId = foodId
                        )

                val responseBody =
                    response.body()

                val food =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    food != null
                ) {
                    val current =
                        _uiState.value

                    val category =
                        current.categories
                            .firstOrNull {
                                it.id == food.categoryId
                            }

                    _uiState.value =
                        current.copy(
                            foodId = food.id,
                            selectedCategoryId =
                                food.categoryId,
                            selectedCategoryName =
                                category?.name
                                    ?: food.categoryName,
                            name = food.name,
                            description = food.description,
                            price = food.price.toString(),
                            preparationTimeMinutes =
                                food
                                    .preparationTimeMinutes
                                    .toString(),
                            imageUrl = food.imageUrl,
                            isAvailable = food.isAvailable,
                            isLoading = false,
                            errorMessage = null
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage =
                                parseErrorMessage(
                                    response
                                        .errorBody()
                                        ?.string()
                                )
                                    ?: "Yemek bilgisi alınamadı."
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage =
                            "Sunucuya bağlanılamadı."
                    )
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage =
                            "Yemek bilgisi yüklenirken hata oluştu."
                    )
            }
        }
    }

    fun loadCategories() {
        categoryLoadJob?.cancel()

        categoryLoadJob =
            viewModelScope.launch {
                loadCategoriesInternal()
            }
    }

    private suspend fun loadCategoriesInternal() {
        _uiState.value =
            _uiState.value.copy(
                isCategoriesLoading = true,
                categoryErrorMessage = null
            )

        try {
            val response =
                categoryRepository
                    .getCategories()

            val responseBody =
                response.body()

            if (
                response.isSuccessful &&
                responseBody?.success == true
            ) {
                val categories =
                    responseBody.data
                        .orEmpty()
                        .filter {
                            it.isActive != false
                        }
                        .sortedBy {
                            it.name.lowercase()
                        }

                val current =
                    _uiState.value

                val selectedCategory =
                    categories
                        .firstOrNull {
                            it.id ==
                                    current
                                        .selectedCategoryId
                        }

                _uiState.value =
                    current.copy(
                        categories = categories,
                        selectedCategoryName =
                            selectedCategory?.name
                                ?: current
                                    .selectedCategoryName,
                        isCategoriesLoading = false,
                        categoryErrorMessage =
                            if (categories.isEmpty()) {
                                "Aktif kategori bulunamadı."
                            } else {
                                null
                            }
                    )
            } else {
                showCategoryError(
                    parseErrorMessage(
                        response
                            .errorBody()
                            ?.string()
                    )
                        ?: "Kategoriler alınamadı."
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

    fun onCategorySelected(
        categoryId: Int
    ) {
        if (
            _uiState.value.isSaving ||
            _uiState.value.isLoading
        ) {
            return
        }

        val category =
            _uiState.value
                .categories
                .firstOrNull {
                    it.id == categoryId
                }
                ?: return

        _uiState.value =
            _uiState.value.copy(
                selectedCategoryId = category.id,
                selectedCategoryName = category.name,
                categoryErrorMessage = null,
                errorMessage = null
            )
    }

    fun onNameChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                name = value,
                errorMessage = null
            )
    }

    fun onDescriptionChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                description = value,
                errorMessage = null
            )
    }

    fun onPriceChange(value: String) {
        val filteredValue =
            value.filter {
                it.isDigit() ||
                        it == '.' ||
                        it == ','
            }

        _uiState.value =
            _uiState.value.copy(
                price = filteredValue,
                errorMessage = null
            )
    }

    fun onPreparationTimeChange(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                preparationTimeMinutes =
                    value.filter {
                        it.isDigit()
                    },
                errorMessage = null
            )
    }

    fun onImageUrlChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                imageUrl = value,
                errorMessage = null
            )
    }

    fun onAvailabilityChange(value: Boolean) {
        _uiState.value =
            _uiState.value.copy(
                isAvailable = value,
                errorMessage = null
            )
    }

    fun updateFood() {
        val currentState =
            _uiState.value

        if (
            currentState.isSaving ||
            currentState.isLoading ||
            currentState.isCategoriesLoading
        ) {
            return
        }

        val foodId =
            currentState.foodId

        val categoryId =
            currentState.selectedCategoryId

        val price =
            currentState.price
                .replace(",", ".")
                .toDoubleOrNull()

        val preparationTime =
            currentState
                .preparationTimeMinutes
                .toIntOrNull()

        when {
            foodId == null || foodId <= 0 -> {
                showError(
                    "Düzenlenecek yemek bulunamadı."
                )
                return
            }

            currentState.categoryErrorMessage != null -> {
                showError(
                    "Kategori listesi hazır değil. Kategorileri tekrar yükleyin."
                )
                return
            }

            categoryId == null || categoryId <= 0 -> {
                showError(
                    "Bir kategori seçmelisiniz."
                )
                return
            }

            currentState.name.isBlank() -> {
                showError(
                    "Yemek adı boş bırakılamaz."
                )
                return
            }

            currentState.description.isBlank() -> {
                showError(
                    "Yemek açıklaması boş bırakılamaz."
                )
                return
            }

            price == null || price <= 0 -> {
                showError(
                    "Geçerli bir fiyat girmelisiniz."
                )
                return
            }

            preparationTime == null ||
                    preparationTime <= 0 -> {
                showError(
                    "Geçerli bir hazırlama süresi girmelisiniz."
                )
                return
            }
        }

        viewModelScope.launch {
            _uiState.value =
                currentState.copy(
                    isSaving = true,
                    successMessage = null,
                    errorMessage = null
                )

            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                _uiState.value =
                    _uiState.value.copy(
                        isSaving = false,
                        errorMessage =
                            "Oturum bilgisi bulunamadı."
                    )
                return@launch
            }

            val request =
                UpdateFoodRequest(
                    categoryId = categoryId,
                    name =
                        currentState.name.trim(),
                    description =
                        currentState
                            .description
                            .trim(),
                    price = price,
                    preparationTimeMinutes =
                        preparationTime,
                    imageUrl =
                        currentState.imageUrl.trim(),
                    isAvailable =
                        currentState.isAvailable
                )

            try {
                val response =
                    producerFoodRepository
                        .updateFood(
                            foodId = foodId,
                            request = request
                        )

                val responseBody =
                    response.body()

                val updatedFood =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    updatedFood != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            isSaving = false,
                            updatedFood = updatedFood,
                            selectedCategoryId =
                                updatedFood.categoryId,
                            selectedCategoryName =
                                updatedFood.categoryName,
                            successMessage =
                                "Yemek başarıyla güncellendi.",
                            errorMessage = null
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isSaving = false,
                            errorMessage =
                                parseErrorMessage(
                                    response
                                        .errorBody()
                                        ?.string()
                                )
                                    ?: "Yemek güncellenemedi."
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    _uiState.value.copy(
                        isSaving = false,
                        errorMessage =
                            "Sunucuya bağlanılamadı."
                    )
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isSaving = false,
                        errorMessage =
                            "Yemek güncellenirken hata oluştu."
                    )
            }
        }
    }

    fun resetState() {
        categoryLoadJob?.cancel()
        _uiState.value = EditFoodUiState()
    }

    private fun showCategoryError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isCategoriesLoading = false,
                categoryErrorMessage = message
            )
    }

    private fun showError(message: String) {
        _uiState.value =
            _uiState.value.copy(
                isSaving = false,
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