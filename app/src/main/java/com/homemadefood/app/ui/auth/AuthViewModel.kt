package com.homemadefood.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.AppMode
import com.homemadefood.app.data.model.UserProfileResponse
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
                    applyAuthenticatedProfile(
                        profile = profile,
                        message = "Oturumunuz açık."
                    )
                } else if (
                    response.code() == 401 ||
                    response.code() == 403
                ) {
                    /*
                     * Token geçersizse, süresi dolmuşsa
                     * veya kullanıcı pasif hâle geldiyse
                     * bütün oturum bilgilerini temizle.
                     */
                    clearInvalidSession()
                } else {
                    _uiState.value =
                        AuthUiState(
                            isSessionChecking = false,
                            message =
                                "Oturum şu anda doğrulanamadı. Daha sonra tekrar deneyin.",
                            isError = true
                        )
                }
            } catch (_: IOException) {
                /*
                 * Sunucuya ulaşılamaması tokenın
                 * geçersiz olduğu anlamına gelmez.
                 * Bu yüzden burada tokenı silmiyoruz.
                 */
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
        /*
         * Kullanıcı giriş butonuna art arda
         * bassa bile ikinci istek gönderilmez.
         */
        if (_uiState.value.isLoading) {
            return
        }

        val normalizedEmail =
            email
                .trim()
                .lowercase()

        if (
            normalizedEmail.isBlank() ||
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
                        email = normalizedEmail,
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

                    val savedActiveMode =
                        sessionManager.activeMode.first()

                    _uiState.value =
                        AuthUiState(
                            isLoading = false,
                            isLoggedIn = true,

                            message =
                                responseBody.message,

                            isError = false,

                            userRole =
                                loginData.role.trim(),

                            canUseProducerMode =
                                loginData
                                    .canUseProducerMode,

                            producerProfileId =
                                loginData
                                    .producerProfileId,

                            producerVerificationStatus =
                                loginData
                                    .producerVerificationStatus,

                            activeMode =
                                savedActiveMode
                        )
                } else {
                    val errorJson =
                        response.errorBody()
                            ?.string()

                    val errorMessage =
                        when (response.code()) {

                            /*
                             * Kullanıcı bulunamadı, şifre yanlış,
                             * hesap pasif veya hesap kilitli olsa
                             * bile aynı mesaj gösterilir.
                             */
                            401 ->
                                "E-posta veya şifre hatalı."

                            /*
                             * Backend IP tabanlı giriş sınırını
                             * aştığında 429 döndürür.
                             */
                            429 -> {
                                val retryAfterSeconds =
                                    response.headers()[
                                        "Retry-After"
                                    ]?.toIntOrNull()

                                if (
                                    retryAfterSeconds != null &&
                                    retryAfterSeconds > 0
                                ) {
                                    "Çok fazla giriş denemesi yapıldı. " +
                                            "Yaklaşık $retryAfterSeconds saniye sonra tekrar deneyin."
                                } else {
                                    "Çok fazla giriş denemesi yapıldı. " +
                                            "Lütfen kısa bir süre sonra tekrar deneyin."
                                }
                            }

                            /*
                             * E-posta biçimi gibi doğrulama
                             * hatalarında backend mesajı alınabilir.
                             */
                            400 ->
                                parseErrorMessage(
                                    errorJson
                                ) ?: "Gönderilen bilgiler doğrulanamadı."

                            else ->
                                "Giriş işlemi şu anda tamamlanamadı. " +
                                        "Lütfen daha sonra tekrar deneyin."
                        }

                    showError(errorMessage)
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
        if (_uiState.value.isLoading) {
            return
        }

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
                        email =
                            email
                                .trim()
                                .lowercase(),
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

                            registrationSuccessful =
                                true,

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

    /**
     * Kullanıcı üretici başvurusu onaylandıktan
     * sonra backend profilini tekrar getirir.
     *
     * Böylece uygulamadan çıkış yapıp yeniden
     * giriş yapılmasına gerek kalmaz.
     */
    fun refreshProfile() {
        if (_uiState.value.isLoading) {
            return
        }

        viewModelScope.launch {
            val savedToken =
                sessionManager.token.first()

            if (savedToken.isNullOrBlank()) {
                clearInvalidSession()
                return@launch
            }

            val previousState =
                _uiState.value

            _uiState.value =
                previousState.copy(
                    isLoading = true,
                    message = null,
                    isError = false
                )

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
                    applyAuthenticatedProfile(
                        profile = profile,
                        message =
                            "Hesap bilgileriniz güncellendi."
                    )
                } else if (
                    response.code() == 401 ||
                    response.code() == 403
                ) {
                    clearInvalidSession()
                } else {
                    _uiState.value =
                        previousState.copy(
                            isLoading = false,

                            message =
                                "Hesap bilgileri güncellenemedi.",

                            isError = true
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Sunucuya bağlanılamadı.",

                        isError = true
                    )
            } catch (_: Exception) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Hesap bilgileri güncellenirken bir hata oluştu.",

                        isError = true
                    )
            }
        }
    }

    fun switchToCustomerMode() {
        val currentState =
            _uiState.value

        if (
            !currentState.isLoggedIn ||
            !currentState.userRole.equals(
                "Customer",
                ignoreCase = true
            )
        ) {
            showStateError(
                "Bu hesap müşteri modunu kullanamaz."
            )

            return
        }

        viewModelScope.launch {
            sessionManager.setActiveMode(
                AppMode.CUSTOMER
            )

            _uiState.value =
                _uiState.value.copy(
                    activeMode =
                        AppMode.CUSTOMER,

                    message =
                        "Müşteri moduna geçildi.",

                    isError = false
                )
        }
    }

    fun switchToProducerMode() {
        val currentState =
            _uiState.value

        if (
            !currentState.isLoggedIn ||
            !currentState.userRole.equals(
                "Customer",
                ignoreCase = true
            ) ||
            !currentState.canUseProducerMode
        ) {
            showStateError(
                "Üretici modunu kullanma yetkiniz bulunmuyor."
            )

            return
        }

        viewModelScope.launch {
            sessionManager.setActiveMode(
                AppMode.PRODUCER
            )

            /*
             * SessionManager ayrıca güvenlik
             * kontrolü yaptığı için gerçek kaydedilen
             * modu tekrar okuyoruz.
             */
            val savedMode =
                sessionManager.activeMode.first()

            _uiState.value =
                _uiState.value.copy(
                    activeMode =
                        savedMode,

                    message =
                        if (
                            savedMode ==
                            AppMode.PRODUCER
                        ) {
                            "Üretici moduna geçildi."
                        } else {
                            "Üretici moduna geçilemedi."
                        },

                    isError =
                        savedMode !=
                                AppMode.PRODUCER
                )
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

    private suspend fun applyAuthenticatedProfile(
        profile: UserProfileResponse,
        message: String
    ) {
        sessionManager.updateProfile(
            profile
        )

        val savedActiveMode =
            sessionManager.activeMode.first()

        _uiState.value =
            AuthUiState(
                isSessionChecking = false,
                isLoading = false,
                isLoggedIn = true,
                message = message,
                isError = false,

                userRole =
                    profile.role.trim(),

                canUseProducerMode =
                    profile.canUseProducerMode,

                producerProfileId =
                    profile.producerProfileId,

                producerVerificationStatus =
                    profile
                        .producerVerificationStatus,

                activeMode =
                    savedActiveMode
            )
    }

    private suspend fun clearInvalidSession() {
        sessionManager.clearSession()

        _uiState.value =
            AuthUiState(
                isSessionChecking = false,
                message =
                    "Oturumunuz sona erdi. Lütfen tekrar giriş yapın.",
                isError = true
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            AuthUiState(
                isSessionChecking = false,
                isLoading = false,
                message = message,
                isError = true
            )
    }

    private fun showStateError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
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