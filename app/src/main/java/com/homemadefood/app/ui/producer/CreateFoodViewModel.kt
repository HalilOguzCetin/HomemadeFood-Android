package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.CreateFoodRequest
import com.homemadefood.app.data.repository.ProducerFoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CreateFoodViewModel(
    private val producerFoodRepository: ProducerFoodRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(CreateFoodUiState())

    val uiState: StateFlow<CreateFoodUiState> =
        _uiState.asStateFlow()

    fun onCategoryIdChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                categoryId = value.filter {
                    it.isDigit()
                },
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

    fun onPreparationTimeChange(value: String) {
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

    fun createFood() {
        val currentState = _uiState.value

        if (currentState.isSaving) {
            return
        }

        val categoryId =
            currentState.categoryId.toIntOrNull()

        val price =
            currentState.price
                .replace(",", ".")
                .toDoubleOrNull()

        val preparationTime =
            currentState.preparationTimeMinutes
                .toIntOrNull()

        when {
            categoryId == null || categoryId <= 0 -> {
                showError(
                    "Geçerli bir kategori seçmelisiniz."
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

            currentState.imageUrl.isBlank() -> {
                showError(
                    "Yemek görselinin adresini girmelisiniz."
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

            val token =
                sessionManager.token.first()

            if (token.isNullOrBlank()) {
                _uiState.value =
                    _uiState.value.copy(
                        isSaving = false,
                        errorMessage =
                            "Oturum bilgisi bulunamadı."
                    )

                return@launch
            }

            val request =
                CreateFoodRequest(
                    categoryId = categoryId,
                    name =
                        currentState.name.trim(),
                    description =
                        currentState.description.trim(),
                    price = price,
                    preparationTimeMinutes =
                        preparationTime,
                    imageUrl =
                        currentState.imageUrl.trim()
                )

            try {
                val response =
                    producerFoodRepository.createFood(
                        token = token,
                        request = request
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
                                "Yemek başarıyla eklendi.",
                            errorMessage = null
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isSaving = false,
                            errorMessage =
                                parseErrorMessage(
                                    response.errorBody()
                                        ?.string()
                                ) ?: "Yemek eklenemedi."
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
        _uiState.value = CreateFoodUiState()
    }

    private fun showError(
        message: String
    ) {
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