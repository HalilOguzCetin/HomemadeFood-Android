package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.ProducerApplicationRequest
import com.homemadefood.app.data.repository.ProducerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CustomerProducerApplicationViewModel(
    private val producerRepository:
    ProducerRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadApplicationJob: Job? =
        null

    private var submitApplicationJob: Job? =
        null

    private val _uiState =
        MutableStateFlow(
            CustomerProducerApplicationUiState()
        )

    val uiState:
            StateFlow<
                    CustomerProducerApplicationUiState
                    > =
        _uiState.asStateFlow()

    fun loadApplication() {
        loadApplicationJob?.cancel()

        loadApplicationJob =
            viewModelScope.launch {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    showError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                try {
                    val response =
                        producerRepository
                            .getMyApplication()

                    val responseBody =
                        response.body()

                    when {
                        response.isSuccessful &&
                                responseBody?.success == true &&
                                responseBody.data != null -> {

                            _uiState.value =
                                _uiState.value.copy(
                                    isLoading = false,

                                    application =
                                        responseBody.data,

                                    isFormVisible =
                                        false,

                                    errorMessage = null
                                )
                        }

                        response.code() == 404 -> {
                            _uiState.value =
                                _uiState.value.copy(
                                    isLoading = false,
                                    application = null,

                                    isFormVisible =
                                        true,

                                    errorMessage = null
                                )
                        }

                        else -> {
                            showError(
                                parseErrorMessage(
                                    response.errorBody()
                                        ?.string()
                                ) ?: "Üretici başvurusu alınamadı."
                            )
                        }
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Başvuru bilgisi yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun updateBusinessName(
        value: String
    ) {
        if (value.length > 150) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                businessName = value,
                errorMessage = null
            )
    }

    fun updateDescription(
        value: String
    ) {
        if (value.length > 1000) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                description = value,
                errorMessage = null
            )
    }

    fun updateAddress(
        value: String
    ) {
        if (value.length > 500) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                address = value,
                errorMessage = null
            )
    }

    fun updateLatitudeText(
        value: String
    ) {
        if (value.length > 20) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                latitudeText = value,
                errorMessage = null
            )
    }

    fun updateLongitudeText(
        value: String
    ) {
        if (value.length > 20) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                longitudeText = value,
                errorMessage = null
            )
    }

    fun updateDailyCapacityText(
        value: String
    ) {
        if (
            value.isNotEmpty() &&
            value.any { character ->
                !character.isDigit()
            }
        ) {
            return
        }

        if (value.length > 4) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                dailyCapacityText = value,
                errorMessage = null
            )
    }

    fun showReapplicationForm() {
        val application =
            _uiState.value.application
                ?: return

        if (!_uiState.value.isRejected) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                isFormVisible = true,

                businessName =
                    application.businessName,

                description =
                    application.description,

                address =
                    application.address,

                latitudeText =
                    application.latitude
                        .toString(),

                longitudeText =
                    application.longitude
                        .toString(),

                dailyCapacityText =
                    application.dailyCapacity
                        .toString(),

                errorMessage = null,
                successMessage = null
            )
    }

    fun hideReapplicationForm() {
        if (_uiState.value.application == null) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                isFormVisible = false,
                errorMessage = null
            )
    }

    fun submitApplication() {
        if (
            _uiState.value.isSubmitting ||
            _uiState.value.isLoading
        ) {
            return
        }

        val businessName =
            _uiState.value.businessName
                .trim()

        val description =
            _uiState.value.description
                .trim()

        val address =
            _uiState.value.address
                .trim()

        val latitude =
            parseDecimal(
                _uiState.value.latitudeText
            )

        val longitude =
            parseDecimal(
                _uiState.value.longitudeText
            )

        val dailyCapacity =
            _uiState.value.dailyCapacityText
                .toIntOrNull()

        when {
            businessName.length !in 2..150 -> {
                showError(
                    "İşletme adı 2 ile 150 karakter arasında olmalıdır."
                )

                return
            }

            description.length !in 10..1000 -> {
                showError(
                    "İşletme açıklaması 10 ile 1000 karakter arasında olmalıdır."
                )

                return
            }

            address.length !in 10..500 -> {
                showError(
                    "İşletme adresi 10 ile 500 karakter arasında olmalıdır."
                )

                return
            }

            latitude == null ||
                    latitude !in -90.0..90.0 -> {

                showError(
                    "Enlem -90 ile 90 arasında olmalıdır."
                )

                return
            }

            longitude == null ||
                    longitude !in -180.0..180.0 -> {

                showError(
                    "Boylam -180 ile 180 arasında olmalıdır."
                )

                return
            }

            dailyCapacity == null ||
                    dailyCapacity !in 1..1000 -> {

                showError(
                    "Günlük kapasite 1 ile 1000 arasında olmalıdır."
                )

                return
            }
        }

        submitApplicationJob?.cancel()

        submitApplicationJob =
            viewModelScope.launch {

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    showError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                _uiState.value =
                    _uiState.value.copy(
                        isSubmitting = true,
                        errorMessage = null,
                        successMessage = null
                    )

                try {
                    val response =
                        producerRepository.apply(
                            request =
                                ProducerApplicationRequest(
                                    businessName =
                                        businessName,

                                    description =
                                        description,

                                    address =
                                        address,

                                    latitude =
                                        latitude,

                                    longitude =
                                        longitude,

                                    dailyCapacity =
                                        dailyCapacity
                                )
                        )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        responseBody.data != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isSubmitting = false,
                                isFormVisible = false,

                                successMessage =
                                    responseBody.message
                                        .ifBlank {
                                            "Üretici başvurusu başarıyla gönderildi."
                                        },

                                errorMessage = null
                            )

                        loadApplication()
                    } else {
                        showError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Üretici başvurusu gönderilemedi."
                        )
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Başvuru gönderilirken bir hata oluştu."
                    )
                }
            }
    }

    fun clearMessages() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = null,
                successMessage = null
            )
    }

    private fun parseDecimal(
        value: String
    ): Double? {
        return value
            .trim()
            .replace(
                oldChar = ',',
                newChar = '.'
            )
            .toDoubleOrNull()
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                isSubmitting = false,
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
                .takeIf { message ->
                    message.isNotBlank()
                }
        }.getOrNull()
    }
}