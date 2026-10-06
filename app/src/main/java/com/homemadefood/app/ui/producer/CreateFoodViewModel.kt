package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.FoodProductionOptions
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.ProducerFoodRepository
import com.homemadefood.app.data.upload.FoodImageMultipartFactory
import com.homemadefood.app.data.remote.ApiErrorParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class CreateFoodViewModel(
    private val producerFoodRepository:
    ProducerFoodRepository,

    private val categoryRepository:
    CategoryRepository,

    private val sessionManager:
    SessionManager,

    private val foodImageMultipartFactory:
    FoodImageMultipartFactory
) : ViewModel() {

    private var categoryLoadJob: Job? = null

    private val _uiState =
        MutableStateFlow(CreateFoodUiState())

    val uiState: StateFlow<CreateFoodUiState> =
        _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        categoryLoadJob?.cancel()

        categoryLoadJob =
            viewModelScope.launch {
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
                                selectedCategoryId =
                                    selectedCategory?.id,
                                selectedCategoryName =
                                    selectedCategory
                                        ?.name
                                        .orEmpty(),
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
    }

    fun onCategorySelected(
        categoryId: Int
    ) {
        if (_uiState.value.isSaving) {
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
                successMessage = null,
                errorMessage = null
            )
    }

    fun onNameChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                name = value,
                successMessage = null,
                errorMessage = null
            )
    }

    fun onDescriptionChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                description = value,
                successMessage = null,
                errorMessage = null
            )
    }

    fun onIngredientsChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                ingredients = value.take(2000),
                successMessage = null,
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
                successMessage = null,
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
                successMessage = null,
                errorMessage = null
            )
    }

    fun onUnitTypeSelected(
        value: String
    ) {
        val normalized =
            FoodProductionOptions.unitTypes
                .firstOrNull {
                    it.backendValue.equals(
                        value,
                        ignoreCase = true
                    )
                }
                ?.backendValue
                ?: return

        _uiState.value =
            _uiState.value.copy(
                selectedUnitType = normalized,
                successMessage = null,
                errorMessage = null
            )
    }

    fun onDailyCapacityChange(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                dailyCapacity =
                    value.filter {
                        it.isDigit()
                    }.take(5),
                successMessage = null,
                errorMessage = null
            )
    }

    fun onMinimumOrderLeadTimeChange(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                minimumOrderLeadTimeMinutes =
                    value.filter {
                        it.isDigit()
                    }.take(5),
                successMessage = null,
                errorMessage = null
            )
    }

    fun onAllergenToggle(
        allergenCode: String
    ) {
        val normalized =
            FoodProductionOptions.allergens
                .firstOrNull {
                    it.backendValue.equals(
                        allergenCode,
                        ignoreCase = true
                    )
                }
                ?.backendValue
                ?: return

        val current =
            _uiState.value
                .selectedAllergenCodes

        val updated =
            if (
                current.any {
                    it.equals(
                        normalized,
                        ignoreCase = true
                    )
                }
            ) {
                current.filterNot {
                    it.equals(
                        normalized,
                        ignoreCase = true
                    )
                }.toSet()
            } else {
                current + normalized
            }

        _uiState.value =
            _uiState.value.copy(
                selectedAllergenCodes = updated,
                successMessage = null,
                errorMessage = null
            )
    }

    fun onImageSelected(uri: String) {
        if (uri.isBlank()) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedImageUri = uri,
                successMessage = null,
                errorMessage = null
            )
    }

    fun onImageRemoved() {
        _uiState.value =
            _uiState.value.copy(
                selectedImageUri = null,
                successMessage = null,
                errorMessage = null
            )
    }

    fun createFood() {
        val currentState =
            _uiState.value

        if (currentState.isSaving) {
            return
        }

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

        val dailyCapacity =
            currentState
                .dailyCapacity
                .toIntOrNull()

        val minimumOrderLeadTime =
            currentState
                .minimumOrderLeadTimeMinutes
                .toIntOrNull()

        val unitType =
            currentState.selectedUnitType

        val imageUri =
            currentState.selectedImageUri

        when {
            currentState.isCategoriesLoading -> {
                showError(
                    "Kategoriler yüklenirken bekleyin."
                )
                return
            }

            currentState.categoryErrorMessage != null -> {
                showError(
                    "Kategori listesi hazır değil. Kategorileri tekrar yükleyin."
                )
                return
            }

            categoryId == null ||
                    categoryId <= 0 -> {
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

            currentState.ingredients.isBlank() -> {
                showError(
                    "İçindekiler bilgisi boş bırakılamaz."
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

            FoodProductionOptions.unitTypes
                .none {
                    it.backendValue.equals(
                        unitType,
                        ignoreCase = true
                    )
                } -> {
                showError(
                    "Geçerli bir satış birimi seçmelisiniz."
                )
                return
            }

            dailyCapacity == null ||
                    dailyCapacity !in 1..10000 -> {
                showError(
                    "Günlük kapasite 1 ile 10000 arasında olmalıdır."
                )
                return
            }

            minimumOrderLeadTime == null ||
                    minimumOrderLeadTime !in 0..10080 -> {
                showError(
                    "Minimum sipariş ön süresi 0 ile 10080 dakika arasında olmalıdır."
                )
                return
            }

            imageUri.isNullOrBlank() -> {
                showError(
                    "Yemek fotoğrafı seçmelisiniz."
                )
                return
            }
        }

        viewModelScope.launch {
            _uiState.value =
                currentState.copy(
                    isSaving = true,
                    createdFood = null,
                    successMessage = null,
                    errorMessage = null
                )

            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                showError(
                    "Oturum bilgisi bulunamadı. Yeniden giriş yapın."
                )
                return@launch
            }

            val imagePart =
                try {
                    foodImageMultipartFactory
                        .createPart(imageUri)
                } catch (
                    exception: IllegalArgumentException
                ) {
                    showError(
                        exception.message
                            ?: "Seçilen fotoğraf yüklemeye hazırlanamadı."
                    )
                    return@launch
                } catch (_: SecurityException) {
                    showError(
                        "Seçilen fotoğrafa erişilemiyor. Lütfen fotoğrafı yeniden seçin."
                    )
                    return@launch
                } catch (_: IOException) {
                    showError(
                        "Seçilen fotoğraf okunamadı. Lütfen başka bir fotoğraf deneyin."
                    )
                    return@launch
                } catch (_: Exception) {
                    showError(
                        "Fotoğraf hazırlanırken bir hata oluştu."
                    )
                    return@launch
                }

            try {
                val response =
                    producerFoodRepository
                        .createFood(
                            categoryId = categoryId,
                            name =
                                currentState.name.trim(),
                            description =
                                currentState
                                    .description
                                    .trim(),
                            ingredients =
                                currentState
                                    .ingredients
                                    .trim(),
                            price = price,
                            preparationTimeMinutes =
                                preparationTime,
                            image = imagePart,
                            unitType = unitType,
                            dailyCapacity =
                                dailyCapacity,
                            minimumOrderLeadTimeMinutes =
                                minimumOrderLeadTime,
                            allergenCodes =
                                currentState
                                    .selectedAllergenCodes
                                    .toList()
                        )

                val responseBody =
                    response.body()

                val createdFood =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    createdFood != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            isSaving = false,
                            createdFood = createdFood,
                            successMessage =
                                "Yemek fotoğrafıyla birlikte başarıyla eklendi.",
                            errorMessage = null
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response
                                .errorBody()
                                ?.string()
                        )
                            ?: when (response.code()) {
                                413 ->
                                    "Yemek fotoğrafı sunucunun izin verdiği boyuttan büyük."

                                else ->
                                    "Yemek eklenemedi."
                            }
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı. Backend bağlantısını kontrol edin."
                )
            } catch (_: Exception) {
                showError(
                    "Yemek eklenirken bir hata oluştu."
                )
            }
        }
    }

    fun clearResult() {
        _uiState.value =
            _uiState.value.copy(
                createdFood = null,
                successMessage = null,
                errorMessage = null
            )
    }

    fun resetForm() {
        categoryLoadJob?.cancel()
        _uiState.value = CreateFoodUiState()
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

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isSaving = false,
                successMessage = null,
                errorMessage = message
            )
    }

    private fun parseErrorMessage(
        errorJson: String?
    ): String? {
        return ApiErrorParser
            .parse(
                errorJson
            )
            .message
    }
}