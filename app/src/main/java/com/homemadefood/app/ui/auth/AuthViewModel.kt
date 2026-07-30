package com.homemadefood.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            AuthUiState(
                isSessionChecking = true
            )
        )

    val uiState: StateFlow<AuthUiState> =
        _uiState.asStateFlow()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            val savedToken =
                sessionManager.token.first()

            if (savedToken.isNullOrBlank()) {
                _uiState.value =
                    AuthUiState(
                        isSessionChecking = false
                    )

                return@launch
            }

            try {
                val response =
                    authRepository.getProfile(
                        token = savedToken
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
                    _uiState.value =
                        AuthUiState(
                            isSessionChecking = false,
                            isLoggedIn = true,
                            message =
                                "Oturumunuz açık.",
                            isError = false,
                            userRole =
                                profile.role.trim()
                        )
                } else {
                    sessionManager.clearSession()

                    _uiState.value =
                        AuthUiState(
                            isSessionChecking = false
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    AuthUiState(
                        isSessionChecking = false,
                        message =
                            "Sunucuya bağlanılamadı. Backend'in açık olduğunu kontrol edin.",
                        isError = true
                    )
            } catch (_: Exception) {
                _uiState.value =
                    AuthUiState(
                        isSessionChecking = false,
                        message =
                            "Oturum kontrolü sırasında bir hata oluştu.",
                        isError = true
                    )
            }
        }
    }

    fun login(
        email: String,
        password: String
    ) {
        if (
            email.isBlank() ||
            password.isBlank()
        ) {
            showError(
                "E-posta ve şifre alanları zorunludur."
            )

            return
        }

        viewModelScope.launch {
            _uiState.value =
                AuthUiState(
                    isLoading = true
                )

            try {
                val response =
                    authRepository.login(
                        email = email,
                        password = password
                    )

                val responseBody =
                    response.body()

                val loginData =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    loginData != null
                ) {
                    sessionManager.saveSession(
                        loginData
                    )

                    _uiState.value =
                        AuthUiState(
                            isLoading = false,
                            isLoggedIn = true,
                            message =
                                responseBody.message,
                            isError = false,
                            userRole =
                                loginData.role.trim()
                        )
                } else {
                    val errorMessage =
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        )

                    showError(
                        errorMessage
                            ?: "Giriş işlemi başarısız oldu."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı. Backend'in açık olduğunu kontrol edin."
                )
            } catch (_: Exception) {
                showError(
                    "Giriş sırasında beklenmeyen bir hata oluştu."
                )
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String
    ) {
        if (
            fullName.isBlank() ||
            email.isBlank() ||
            password.isBlank() ||
            phone.isBlank()
        ) {
            showError(
                "Kayıt alanlarının tamamını doldurun."
            )

            return
        }

        viewModelScope.launch {
            _uiState.value =
                AuthUiState(
                    isLoading = true
                )

            try {
                val response =
                    authRepository.register(
                        fullName = fullName,
                        email = email,
                        password = password,
                        phone = phone
                    )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        AuthUiState(
                            isLoading = false,
                            registrationSuccessful = true,
                            message =
                                responseBody.message,
                            isError = false
                        )
                } else {
                    val errorMessage =
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        )

                    showError(
                        errorMessage
                            ?: "Kayıt işlemi başarısız oldu."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı. Backend'in açık olduğunu kontrol edin."
                )
            } catch (_: Exception) {
                showError(
                    "Kayıt sırasında beklenmeyen bir hata oluştu."
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()

            _uiState.value =
                AuthUiState(
                    message =
                        "Oturum kapatıldı."
                )
        }
    }

    fun clearMessage() {
        _uiState.value =
            _uiState.value.copy(
                message = null,
                isError = false
            )
    }

    fun resetRegistrationState() {
        _uiState.value =
            _uiState.value.copy(
                registrationSuccessful = false
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            AuthUiState(
                isLoading = false,
                message = message,
                isError = true
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