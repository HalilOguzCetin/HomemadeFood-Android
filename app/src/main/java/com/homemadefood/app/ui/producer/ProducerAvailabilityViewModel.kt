package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.ProducerAvailabilityMode
import com.homemadefood.app.data.model.UpdateProducerBusinessHourRequest
import com.homemadefood.app.data.remote.ApiErrorParser
import com.homemadefood.app.data.repository.ProducerAvailabilityRepository
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProducerAvailabilityViewModel(
    private val producerAvailabilityRepository:
    ProducerAvailabilityRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadAvailabilityJob:
            Job? = null

    private var saveBusinessHoursJob:
            Job? = null

    private var updateModeJob:
            Job? = null

    private val _uiState =
        MutableStateFlow(
            ProducerAvailabilityUiState()
        )

    val uiState:
            StateFlow<ProducerAvailabilityUiState> =
        _uiState.asStateFlow()

    fun loadAvailability() {
        loadAvailabilityJob?.cancel()

        loadAvailabilityJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        successMessage = null,
                        errorMessage = null
                    )

                if (!hasSession()) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        producerAvailabilityRepository
                            .getMyAvailability()

                    val responseBody =
                        response.body()

                    val availability =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        availability != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                availability =
                                    availability,
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
                                        ?: "İşletme çalışma bilgileri alınamadı."
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
                                "İşletme çalışma bilgileri yüklenirken bir hata oluştu."
                        )
                }
            }
    }

    fun updateBusinessHours(
        businessHours:
        List<UpdateProducerBusinessHourRequest>
    ) {
        if (
            businessHours.size != 7 ||
            _uiState.value.isSavingBusinessHours
        ) {
            if (businessHours.size != 7) {
                _uiState.value =
                    _uiState.value.copy(
                        errorMessage =
                            "Haftanın 7 günü için çalışma saati bilgisi gereklidir.",
                        successMessage = null
                    )
            }

            return
        }

        saveBusinessHoursJob?.cancel()

        saveBusinessHoursJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isSavingBusinessHours = true,
                        successMessage = null,
                        errorMessage = null
                    )

                if (!hasSession()) {
                    _uiState.value =
                        _uiState.value.copy(
                            isSavingBusinessHours = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        producerAvailabilityRepository
                            .updateMyBusinessHours(
                                businessHours =
                                    businessHours
                            )

                    val responseBody =
                        response.body()

                    val availability =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        availability != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isSavingBusinessHours = false,
                                availability =
                                    availability,
                                successMessage =
                                    responseBody.message
                                        .ifBlank {
                                            "Çalışma saatleri güncellendi."
                                        },
                                errorMessage = null
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                isSavingBusinessHours = false,
                                errorMessage =
                                    parseErrorMessage(
                                        response
                                            .errorBody()
                                            ?.string()
                                    )
                                        ?: "Çalışma saatleri güncellenemedi."
                            )
                    }
                } catch (_: IOException) {
                    _uiState.value =
                        _uiState.value.copy(
                            isSavingBusinessHours = false,
                            errorMessage =
                                "Sunucuya bağlanılamadı."
                        )
                } catch (_: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isSavingBusinessHours = false,
                            errorMessage =
                                "Çalışma saatleri güncellenirken bir hata oluştu."
                        )
                }
            }
    }

    fun updateAvailabilityMode(
        mode: ProducerAvailabilityMode
    ) {
        if (_uiState.value.isUpdatingMode) {
            return
        }

        updateModeJob?.cancel()

        updateModeJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isUpdatingMode = true,
                        successMessage = null,
                        errorMessage = null
                    )

                if (!hasSession()) {
                    _uiState.value =
                        _uiState.value.copy(
                            isUpdatingMode = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        producerAvailabilityRepository
                            .updateMyAvailabilityMode(
                                mode = mode
                            )

                    val responseBody =
                        response.body()

                    val availability =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        availability != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isUpdatingMode = false,
                                availability =
                                    availability,
                                successMessage =
                                    responseBody.message
                                        .ifBlank {
                                            "İşletme çalışma modu güncellendi."
                                        },
                                errorMessage = null
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                isUpdatingMode = false,
                                errorMessage =
                                    parseErrorMessage(
                                        response
                                            .errorBody()
                                            ?.string()
                                    )
                                        ?: "İşletme çalışma modu güncellenemedi."
                            )
                    }
                } catch (_: IOException) {
                    _uiState.value =
                        _uiState.value.copy(
                            isUpdatingMode = false,
                            errorMessage =
                                "Sunucuya bağlanılamadı."
                        )
                } catch (_: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isUpdatingMode = false,
                            errorMessage =
                                "İşletme çalışma modu güncellenirken bir hata oluştu."
                        )
                }
            }
    }

    fun clearMessage() {
        _uiState.value =
            _uiState.value.copy(
                successMessage = null,
                errorMessage = null
            )
    }

    private suspend fun hasSession():
            Boolean {

        return sessionManager
            .isLoggedIn
            .first()
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
