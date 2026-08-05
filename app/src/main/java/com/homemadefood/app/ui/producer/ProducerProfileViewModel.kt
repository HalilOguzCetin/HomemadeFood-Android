package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.data.repository.ProducerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class ProducerProfileViewModel(
    private val producerRepository:
    ProducerRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadProfileJob: Job? = null
    private var saveProfileJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            ProducerProfileUiState()
        )

    val uiState:
            StateFlow<ProducerProfileUiState> =
        _uiState.asStateFlow()

    fun loadProfile() {
        loadProfileJob?.cancel()

        loadProfileJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null,
                        successMessage = null
                    )

                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    showLoadError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                try {
                    val response =
                        producerRepository
                            .getMyProfile(
                                token = token
                            )

                    val responseBody =
                        response.body()

                    val profile =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        profile != null
                    ) {
                        applyLoadedProfile(
                            profile = profile
                        )
                    } else {
                        showLoadError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Üretici profili alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showLoadError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showLoadError(
                        "Üretici profili yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun startEditing() {
        val profile =
            _uiState.value.profile

        if (profile == null) {
            showActionError(
                "Üretici profil bilgisi bulunamadı."
            )

            return
        }

        if (!_uiState.value.canEdit) {
            showActionError(
                "Yalnızca onaylanmış üretici profilleri düzenlenebilir."
            )

            return
        }

        _uiState.value =
            _uiState.value.copy(
                isEditing = true,

                businessName =
                    profile.businessName,

                description =
                    profile.description,

                address =
                    profile.address,

                latitudeText =
                    profile.latitude.toString(),

                longitudeText =
                    profile.longitude.toString(),

                dailyCapacityText =
                    profile.dailyCapacity
                        .toString(),

                isAvailable =
                    profile.isAvailable,

                errorMessage = null,
                successMessage = null
            )
    }

    fun cancelEditing() {
        if (_uiState.value.isSaving) {
            return
        }

        val profile =
            _uiState.value.profile

        _uiState.value =
            _uiState.value.copy(
                isEditing = false,

                businessName =
                    profile?.businessName
                        .orEmpty(),

                description =
                    profile?.description
                        .orEmpty(),

                address =
                    profile?.address
                        .orEmpty(),

                latitudeText =
                    profile?.latitude
                        ?.toString()
                        .orEmpty(),

                longitudeText =
                    profile?.longitude
                        ?.toString()
                        .orEmpty(),

                dailyCapacityText =
                    profile?.dailyCapacity
                        ?.toString()
                        .orEmpty(),

                isAvailable =
                    profile?.isAvailable
                        ?: false,

                errorMessage = null
            )
    }

    fun updateBusinessName(
        value: String
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        if (value.length <= 150) {
            _uiState.value =
                _uiState.value.copy(
                    businessName = value,
                    errorMessage = null
                )
        }
    }

    fun updateDescription(
        value: String
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        if (value.length <= 1000) {
            _uiState.value =
                _uiState.value.copy(
                    description = value,
                    errorMessage = null
                )
        }
    }

    fun updateAddress(
        value: String
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        if (value.length <= 500) {
            _uiState.value =
                _uiState.value.copy(
                    address = value,
                    errorMessage = null
                )
        }
    }

    fun updateLatitudeText(
        value: String
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        if (isValidDecimalInput(value)) {
            _uiState.value =
                _uiState.value.copy(
                    latitudeText = value,
                    errorMessage = null
                )
        }
    }

    fun updateLongitudeText(
        value: String
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        if (isValidDecimalInput(value)) {
            _uiState.value =
                _uiState.value.copy(
                    longitudeText = value,
                    errorMessage = null
                )
        }
    }

    fun updateDailyCapacityText(
        value: String
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        if (
            value.isEmpty() ||
            value.all { character ->
                character.isDigit()
            }
        ) {
            _uiState.value =
                _uiState.value.copy(
                    dailyCapacityText = value,
                    errorMessage = null
                )
        }
    }

    fun updateAvailability(
        value: Boolean
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                isAvailable = value,
                errorMessage = null
            )
    }

    fun saveProfile() {
        if (
            _uiState.value.isSaving ||
            !_uiState.value.isEditing
        ) {
            return
        }

        val businessName =
            _uiState.value
                .businessName
                .trim()

        val description =
            _uiState.value
                .description
                .trim()

        val address =
            _uiState.value
                .address
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
            _uiState.value
                .dailyCapacityText
                .toIntOrNull()

        when {
            businessName.length !in 2..150 -> {
                showActionError(
                    "İşletme adı 2 ile 150 karakter arasında olmalıdır."
                )

                return
            }

            description.length !in 10..1000 -> {
                showActionError(
                    "İşletme açıklaması 10 ile 1000 karakter arasında olmalıdır."
                )

                return
            }

            address.length !in 10..500 -> {
                showActionError(
                    "Adres 10 ile 500 karakter arasında olmalıdır."
                )

                return
            }

            latitude == null ||
                    latitude !in -90.0..90.0 -> {

                showActionError(
                    "Enlem -90 ile 90 arasında olmalıdır."
                )

                return
            }

            longitude == null ||
                    longitude !in -180.0..180.0 -> {

                showActionError(
                    "Boylam -180 ile 180 arasında olmalıdır."
                )

                return
            }

            dailyCapacity == null ||
                    dailyCapacity !in 1..1000 -> {

                showActionError(
                    "Günlük kapasite 1 ile 1000 arasında olmalıdır."
                )

                return
            }
        }

        saveProfileJob?.cancel()

        saveProfileJob =
            viewModelScope.launch {
                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    showSaveError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                _uiState.value =
                    _uiState.value.copy(
                        isSaving = true,
                        errorMessage = null,
                        successMessage = null
                    )

                try {
                    val response =
                        producerRepository
                            .updateMyProfile(
                                token = token,
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
                                    dailyCapacity,
                                isAvailable =
                                    _uiState.value
                                        .isAvailable
                            )

                    val responseBody =
                        response.body()

                    val updatedProfile =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        updatedProfile != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isSaving = false,
                                isEditing = false,

                                profile =
                                    updatedProfile,

                                businessName =
                                    updatedProfile
                                        .businessName,

                                description =
                                    updatedProfile
                                        .description,

                                address =
                                    updatedProfile
                                        .address,

                                latitudeText =
                                    updatedProfile
                                        .latitude
                                        .toString(),

                                longitudeText =
                                    updatedProfile
                                        .longitude
                                        .toString(),

                                dailyCapacityText =
                                    updatedProfile
                                        .dailyCapacity
                                        .toString(),

                                isAvailable =
                                    updatedProfile
                                        .isAvailable,

                                successMessage =
                                    responseBody.message
                                        .ifBlank {
                                            "Üretici profili başarıyla güncellendi."
                                        },

                                errorMessage = null
                            )
                    } else {
                        showSaveError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Üretici profili güncellenemedi."
                        )
                    }
                } catch (_: IOException) {
                    showSaveError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showSaveError(
                        "Üretici profili güncellenirken bir hata oluştu."
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

    private fun applyLoadedProfile(
        profile: ProducerApplicationStatusResponse
    ) {
        _uiState.value =
            ProducerProfileUiState(
                isLoading = false,
                profile = profile,

                businessName =
                    profile.businessName,

                description =
                    profile.description,

                address =
                    profile.address,

                latitudeText =
                    profile.latitude.toString(),

                longitudeText =
                    profile.longitude.toString(),

                dailyCapacityText =
                    profile.dailyCapacity
                        .toString(),

                isAvailable =
                    profile.isAvailable
            )
    }

    private fun showLoadError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                errorMessage = message
            )
    }

    private fun showActionError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = message,
                successMessage = null
            )
    }

    private fun showSaveError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isSaving = false,
                errorMessage = message,
                successMessage = null
            )
    }

    private fun isValidDecimalInput(
        value: String
    ): Boolean {
        if (value.isEmpty() || value == "-") {
            return true
        }

        val normalized =
            value.replace(
                oldChar = ',',
                newChar = '.'
            )

        return normalized.toDoubleOrNull() != null
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