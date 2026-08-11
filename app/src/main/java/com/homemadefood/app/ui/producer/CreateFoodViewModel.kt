package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.ProducerFoodRepository
import com.homemadefood.app.data.upload.FoodImageMultipartFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CreateFoodViewModel(
    private val producerFoodRepository: ProducerFoodRepository,
    private val sessionManager: SessionManager,
    private val foodImageMultipartFactory:
    FoodImageMultipartFactory
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

    fun onPreparationTimeChange(value: String) {
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

        val imageUri =
            currentState.selectedImageUri

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
                                currentState.description.trim(),
                            price = price,
                            preparationTimeMinutes =
                                preparationTime,
                            image = imagePart
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
                            response.errorBody()
                                ?.string()
                        ) ?: when (response.code()) {
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
        _uiState.value = CreateFoodUiState()
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