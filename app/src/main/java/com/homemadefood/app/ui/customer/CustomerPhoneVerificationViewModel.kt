package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CustomerPhoneVerificationViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            CustomerPhoneVerificationUiState()
        )

    val uiState:
            StateFlow<CustomerPhoneVerificationUiState> =
        _uiState.asStateFlow()

    fun updatePhone(
        value: String
    ) {
        if (
            _uiState.value.isBusy ||
            _uiState.value.isCodeSent
        ) {
            return
        }

        if (value.length > 24) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                phone = value,
                message = null,
                isError = false
            )
    }

    fun updateCode(
        value: String
    ) {
        if (
            _uiState.value.isBusy ||
            !_uiState.value.isCodeSent
        ) {
            return
        }

        val filtered =
            value.filter {
                it.isDigit()
            }

        if (filtered.length > 6) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                code = filtered,
                message = null,
                isError = false
            )
    }

    fun requestCode() {
        val currentState =
            _uiState.value

        if (
            currentState.isBusy ||
            !CustomerPhoneVerificationUiState
                .isLikelyTurkishMobilePhone(
                    currentState.phone
                )
        ) {
            if (!currentState.isBusy) {
                showError(
                    "Geçerli bir Türkiye mobil telefon numarası girin."
                )
            }

            return
        }

        val requestedPhone =
            currentState.phone.trim()

        viewModelScope.launch {
            _uiState.value =
                currentState.copy(
                    isRequestingCode = true,
                    message = null,
                    isError = false
                )

            try {
                val response =
                    authRepository
                        .requestPhoneVerification(
                            phone =
                                requestedPhone
                        )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            phone =
                                requestedPhone,

                            code = "",

                            isRequestingCode =
                                false,

                            isCodeSent =
                                true,

                            codeRequestVersion =
                                _uiState.value
                                    .codeRequestVersion + 1,

                            message =
                                responseBody.message
                                    .takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "Doğrulama kodu gönderildi.",

                            isError =
                                false
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isRequestingCode = false,
                            message =
                                resolveErrorMessage(
                                    errorJson =
                                        response
                                            .errorBody()
                                            ?.string(),

                                    fallback =
                                        responseBody
                                            ?.message
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: "Doğrulama kodu gönderilemedi."
                                ),
                            isError = true
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    _uiState.value.copy(
                        isRequestingCode = false,
                        message =
                            "Sunucuya bağlanılamadı.",
                        isError = true
                    )
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isRequestingCode = false,
                        message =
                            "Telefon doğrulama kodu gönderilirken bir hata oluştu.",
                        isError = true
                    )
            }
        }
    }

    fun verifyCode() {
        val currentState =
            _uiState.value

        if (
            currentState.isBusy ||
            !currentState.isCodeSent
        ) {
            return
        }

        if (currentState.code.length != 6) {
            showError(
                "Doğrulama kodu 6 haneli olmalıdır."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value =
                currentState.copy(
                    isVerifying = true,
                    message = null,
                    isError = false
                )

            try {
                val response =
                    authRepository
                        .verifyPhone(
                            phone =
                                currentState.phone,

                            code =
                                currentState.code
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
                    /*
                     * SessionManager telefon alanı saklamasa bile
                     * profile response'daki ad/e-posta/producer
                     * capability bilgileri tekrar senkronize edilir.
                     * Profil ekranı geri dönünce backend'den yeniden
                     * yüklenerek doğrulanmış telefonu gösterir.
                     */
                    sessionManager
                        .updateProfile(
                            profile
                        )

                    _uiState.value =
                        _uiState.value.copy(
                            isVerifying = false,
                            isVerificationCompleted =
                                true,
                            message =
                                responseBody.message
                                    .takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "Telefon numarası başarıyla doğrulandı.",
                            isError = false
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isVerifying = false,
                            message =
                                resolveErrorMessage(
                                    errorJson =
                                        response
                                            .errorBody()
                                            ?.string(),

                                    fallback =
                                        responseBody
                                            ?.message
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: "Telefon doğrulama kodu geçersiz veya süresi dolmuş."
                                ),
                            isError = true
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    _uiState.value.copy(
                        isVerifying = false,
                        message =
                            "Sunucuya bağlanılamadı.",
                        isError = true
                    )
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isVerifying = false,
                        message =
                            "Telefon doğrulanırken bir hata oluştu.",
                        isError = true
                    )
            }
        }
    }

    fun editPhoneNumber() {
        if (_uiState.value.isBusy) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                code = "",
                isCodeSent = false,
                message = null,
                isError = false
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                message = message,
                isError = true
            )
    }

    private fun resolveErrorMessage(
        errorJson: String?,
        fallback: String
    ): String {
        if (errorJson.isNullOrBlank()) {
            return fallback
        }

        return runCatching {
            val json =
                JSONObject(errorJson)

            val errors =
                json.optJSONObject("errors")
                    ?: json.optJSONObject("Errors")

            if (errors != null) {
                val keys =
                    errors.keys()

                while (keys.hasNext()) {
                    val key =
                        keys.next()

                    val value =
                        errors.opt(key)

                    val firstError =
                        when (value) {
                            is org.json.JSONArray ->
                                value
                                    .optString(0)
                                    .takeIf {
                                        it.isNotBlank()
                                    }

                            is String ->
                                value.takeIf {
                                    it.isNotBlank()
                                }

                            else ->
                                null
                        }

                    if (firstError != null) {
                        return@runCatching firstError
                    }
                }
            }

            json.optString("message")
                .takeIf {
                    it.isNotBlank()
                }
                ?: json
                    .optString("title")
                    .takeIf {
                        it.isNotBlank()
                    }
                ?: fallback
        }.getOrDefault(
            fallback
        )
    }
}