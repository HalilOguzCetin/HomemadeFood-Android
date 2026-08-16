package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AuthRepository
import com.homemadefood.app.data.remote.ApiErrorParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class CustomerProfileViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            CustomerProfileUiState()
        )

    val uiState:
            StateFlow<CustomerProfileUiState> =
        _uiState.asStateFlow()

    fun loadProfile() {
        if (_uiState.value.isSaving) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

            try {
                val response =
                    authRepository
                        .getProfile()

                val responseBody =
                    response.body()

                val profile =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    profile != null
                ) {
                    /*
                     * Backend profilini DataStore ile de eşitleriz.
                     * Böylece FullName gibi session alanları
                     * uygulamadan çıkış yapmadan güncel kalır.
                     */
                    sessionManager
                        .updateProfile(
                            profile
                        )

                    _uiState.value =
                        CustomerProfileUiState(
                            isLoading = false,
                            profile = profile,
                            fullName =
                                profile.fullName
                        )
                } else {
                    showLoadError(
                        parseErrorMessage(
                            response
                                .errorBody()
                                ?.string()
                        )
                            ?: responseBody
                                ?.message
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                            ?: "Profil bilgileri alınamadı."
                    )
                }
            } catch (_: IOException) {
                showLoadError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showLoadError(
                    "Profil bilgileri yüklenirken bir hata oluştu."
                )
            }
        }
    }

    fun startEditing() {
        val profile =
            _uiState.value.profile
                ?: return

        _uiState.value =
            _uiState.value.copy(
                isEditing = true,
                fullName =
                    profile.fullName,
                errorMessage = null,
                successMessage = null
            )
    }

    fun cancelEditing() {
        val profile =
            _uiState.value.profile
                ?: return

        _uiState.value =
            _uiState.value.copy(
                isEditing = false,
                fullName =
                    profile.fullName,
                errorMessage = null,
                successMessage = null
            )
    }

    fun updateFullName(
        value: String
    ) {
        if (!_uiState.value.isEditing) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                fullName = value,
                errorMessage = null,
                successMessage = null
            )
    }

    fun saveProfile() {
        val currentState =
            _uiState.value

        if (
            currentState.isSaving ||
            !currentState.isEditing
        ) {
            return
        }

        val normalizedFullName =
            currentState.fullName
                .trim()

        if (
            normalizedFullName.length !in
            2..100
        ) {
            _uiState.value =
                currentState.copy(
                    errorMessage =
                        "Ad soyad 2 ile 100 karakter arasında olmalıdır."
                )
            return
        }

        if (
            normalizedFullName ==
            currentState.profile
                ?.fullName
                ?.trim()
        ) {
            _uiState.value =
                currentState.copy(
                    errorMessage =
                        "Kaydedilecek bir değişiklik bulunmuyor."
                )
            return
        }

        viewModelScope.launch {
            _uiState.value =
                currentState.copy(
                    isSaving = true,
                    errorMessage = null,
                    successMessage = null
                )

            try {
                val response =
                    authRepository
                        .updateProfile(
                            fullName =
                                normalizedFullName
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
                    sessionManager
                        .updateProfile(
                            profile
                        )

                    _uiState.value =
                        CustomerProfileUiState(
                            isLoading = false,
                            isSaving = false,
                            isEditing = false,
                            profile = profile,
                            fullName =
                                profile.fullName,
                            successMessage =
                                responseBody.message
                                    .takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "Profil bilgileriniz güncellendi."
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
                                    ?: responseBody
                                        ?.message
                                        ?.takeIf {
                                            it.isNotBlank()
                                        }
                                    ?: "Profil bilgileri güncellenemedi."
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
                            "Profil güncellenirken bir hata oluştu."
                    )
            }
        }
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