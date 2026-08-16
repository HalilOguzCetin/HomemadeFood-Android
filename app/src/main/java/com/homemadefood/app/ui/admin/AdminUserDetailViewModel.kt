package com.homemadefood.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AdminRepository
import com.homemadefood.app.data.remote.ApiErrorParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class AdminUserDetailViewModel(
    private val adminRepository: AdminRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadUserJob: Job? = null
    private var updateStatusJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            AdminUserDetailUiState()
        )

    val uiState:
            StateFlow<AdminUserDetailUiState> =
        _uiState.asStateFlow()

    fun loadUser(
        userId: Int
    ) {
        if (userId <= 0) {
            _uiState.value =
                AdminUserDetailUiState(
                    isLoading = false,
                    errorMessage =
                        "Geçersiz kullanıcı bilgisi."
                )

            return
        }

        loadUserJob?.cancel()

        loadUserJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        user = null,
                        successMessage = null,
                        errorMessage = null
                    )

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    showLoadError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                try {
                    val response =
                        adminRepository.getUserById(
                            userId = userId
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
                                isLoading = false,
                                user = responseBody.data,
                                errorMessage = null
                            )
                    } else {
                        showLoadError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Kullanıcı detayı alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showLoadError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showLoadError(
                        "Kullanıcı detayı yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun updateUserStatus(
        isActive: Boolean
    ) {
        if (
            _uiState.value.isUpdatingStatus ||
            _uiState.value.user == null
        ) {
            return
        }

        val currentUser =
            _uiState.value.user
                ?: return

        if (currentUser.isActive == isActive) {
            return
        }

        updateStatusJob?.cancel()

        updateStatusJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isUpdatingStatus = true,
                        successMessage = null,
                        errorMessage = null
                    )

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    showUpdateError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                try {
                    val response =
                        adminRepository.updateUserStatus(
                            userId = currentUser.userId,
                            isActive = isActive
                        )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true
                    ) {
                        val updatedUser =
                            currentUser.copy(
                                isActive = isActive,

                                isProducerAvailable =
                                    if (!isActive) {
                                        false
                                    } else {
                                        currentUser
                                            .isProducerAvailable
                                    }
                            )



                        val fallbackMessage =
                            if (isActive) {
                                "Kullanıcı hesabı aktifleştirildi."
                            } else {
                                "Kullanıcı hesabı pasifleştirildi."
                            }

                        _uiState.value =
                            _uiState.value.copy(
                                user = updatedUser,
                                isUpdatingStatus = false,

                                successMessage =
                                    responseBody.message
                                        .ifBlank {
                                            fallbackMessage
                                        },

                                errorMessage = null
                            )
                    } else {
                        showUpdateError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Hesap durumu güncellenemedi."
                        )
                    }
                } catch (_: IOException) {
                    showUpdateError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showUpdateError(
                        "Hesap durumu güncellenirken bir hata oluştu."
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

    private fun showLoadError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                user = null,
                errorMessage = message,
                successMessage = null
            )
    }

    private fun showUpdateError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isUpdatingStatus = false,
                errorMessage = message,
                successMessage = null
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