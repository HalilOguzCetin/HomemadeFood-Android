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

            /*
             * Eski sürümden kalmış düz metin JWT varsa
             * güvenlik amacıyla tamamen kaldırılır.
             */
            sessionManager
                .purgeLegacyPlaintextToken()

            val hasSession =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!hasSession) {
                _uiState.value =
                    AuthUiState(
                        isSessionChecking = false
                    )

                return@launch
            }

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

                    val errorCode =
                        parseErrorCode(
                            errorJson
                        )

                    /*
                     * Backend, yalnızca şifre doğru olduğu
                     * hâlde hesap doğrulanmamışsa bu kodu
                     * döndürür. JWT üretilmez.
                     *
                     * Kullanıcı mevcut doğrulama ekranına
                     * yönlendirilir.
                     */
                    if (
                        response.code() == 403 &&
                        errorCode ==
                        "EMAIL_NOT_VERIFIED"
                    ) {
                        _uiState.value =
                            AuthUiState(
                                isSessionChecking = false,
                                isLoading = false,

                                emailVerificationRequired =
                                    true,

                                pendingVerificationEmail =
                                    normalizedEmail,

                                message =
                                    parseErrorMessage(
                                        errorJson
                                    ) ?: "E-posta adresinizi doğrulamanız gerekiyor.",

                                isError = false
                            )

                        return@launch
                    }

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
                             * Beklenmeyen başka bir 403 cevabı
                             * doğrulama yönlendirmesi yapmaz.
                             */
                            403 ->
                                parseErrorMessage(
                                    errorJson
                                ) ?: "Bu işlem için yetkiniz bulunmuyor."

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
        password: String
    ) {
        if (_uiState.value.isLoading) {
            return
        }

        val normalizedFullName =
            fullName.trim()

        val normalizedEmail =
            email
                .trim()
                .lowercase()

        if (
            normalizedFullName.isBlank() ||
            normalizedEmail.isBlank() ||
            password.isBlank()
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
                        fullName =
                            normalizedFullName,

                        email =
                            normalizedEmail,

                        password =
                            password
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

                            pendingVerificationEmail =
                                normalizedEmail,

                            message =
                                responseBody.message,

                            isError = false
                        )
                } else {
                    val errorMessage =
                        parseErrorMessage(
                            response
                                .errorBody()
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

    fun verifyEmail(
        email: String,
        code: String
    ) {
        if (_uiState.value.isLoading) {
            return
        }

        val normalizedEmail =
            email
                .trim()
                .lowercase()

        val normalizedCode =
            code.trim()

        if (
            normalizedEmail.isBlank() ||
            normalizedCode.length != 6 ||
            normalizedCode.any {
                !it.isDigit()
            }
        ) {
            showVerificationError(
                "6 haneli doğrulama kodunu girin."
            )

            return
        }

        viewModelScope.launch {
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
                    authRepository.verifyEmail(
                        email =
                            normalizedEmail,

                        code =
                            normalizedCode
                    )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        previousState.copy(
                            isLoading = false,

                            emailVerificationSuccessful =
                                true,

                            message =
                                responseBody.message,

                            isError = false
                        )
                } else {
                    val errorMessage =
                        parseErrorMessage(
                            response
                                .errorBody()
                                ?.string()
                        )

                    _uiState.value =
                        previousState.copy(
                            isLoading = false,

                            message =
                                errorMessage
                                    ?: "Doğrulama kodu geçersiz veya kullanılamıyor.",

                            isError = true
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Sunucuya bağlanılamadı. Backend'in açık olduğunu kontrol edin.",

                        isError = true
                    )
            } catch (_: Exception) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "E-posta doğrulanırken beklenmeyen bir hata oluştu.",

                        isError = true
                    )
            }
        }
    }

    fun resendEmailVerification(
        email: String
    ) {
        if (_uiState.value.isLoading) {
            return
        }

        val normalizedEmail =
            email
                .trim()
                .lowercase()

        if (normalizedEmail.isBlank()) {
            showVerificationError(
                "E-posta adresi bulunamadı."
            )

            return
        }

        viewModelScope.launch {
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
                    authRepository
                        .resendEmailVerification(
                            email =
                                normalizedEmail
                        )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        previousState.copy(
                            isLoading = false,

                            /*
                             * Her başarılı HTTP resend cevabında
                             * değer artırılır. VerificationScreen
                             * bu değişimi görüp 60 sn sayacı
                             * yeniden başlatır.
                             */
                            resendRequestVersion =
                                previousState
                                    .resendRequestVersion + 1,

                            message =
                                responseBody.message,

                            isError = false
                        )
                } else {
                    val errorJson =
                        response
                            .errorBody()
                            ?.string()

                    val errorMessage =
                        when (response.code()) {
                            429 -> {
                                val retryAfterSeconds =
                                    response.headers()[
                                        "Retry-After"
                                    ]?.toIntOrNull()

                                if (
                                    retryAfterSeconds != null &&
                                    retryAfterSeconds > 0
                                ) {
                                    "Çok fazla doğrulama kodu isteği yapıldı. " +
                                            "Yaklaşık $retryAfterSeconds saniye sonra tekrar deneyin."
                                } else {
                                    "Çok fazla doğrulama kodu isteği yapıldı. " +
                                            "Lütfen daha sonra tekrar deneyin."
                                }
                            }

                            400 ->
                                parseErrorMessage(
                                    errorJson
                                ) ?: "Gönderilen bilgiler doğrulanamadı."

                            else ->
                                parseErrorMessage(
                                    errorJson
                                ) ?: "Doğrulama kodu yeniden gönderilemedi."
                        }

                    _uiState.value =
                        previousState.copy(
                            isLoading = false,
                            message = errorMessage,
                            isError = true
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Sunucuya bağlanılamadı. Backend'in açık olduğunu kontrol edin.",

                        isError = true
                    )
            } catch (_: Exception) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Doğrulama kodu yeniden gönderilirken bir hata oluştu.",

                        isError = true
                    )
            }
        }
    }

    fun requestPasswordReset(
        email: String
    ) {
        if (_uiState.value.isLoading) {
            return
        }

        val normalizedEmail =
            email
                .trim()
                .lowercase()

        if (normalizedEmail.isBlank()) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    message =
                        "E-posta adresini girin.",
                    isError = true
                )

            return
        }

        viewModelScope.launch {
            val previousState =
                _uiState.value

            _uiState.value =
                previousState.copy(
                    isLoading = true,
                    passwordResetRequestSuccessful =
                        false,
                    message = null,
                    isError = false
                )

            try {
                val response =
                    authRepository
                        .forgotPassword(
                            email =
                                normalizedEmail
                        )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        previousState.copy(
                            isLoading = false,

                            passwordResetRequestSuccessful =
                                true,

                            pendingPasswordResetEmail =
                                normalizedEmail,

                            message =
                                responseBody.message,

                            isError = false
                        )
                } else {
                    val errorJson =
                        response
                            .errorBody()
                            ?.string()

                    val errorMessage =
                        when (response.code()) {
                            429 -> {
                                val retryAfterSeconds =
                                    response.headers()[
                                        "Retry-After"
                                    ]?.toIntOrNull()

                                if (
                                    retryAfterSeconds != null &&
                                    retryAfterSeconds > 0
                                ) {
                                    "Çok fazla şifre sıfırlama isteği yapıldı. " +
                                            "Yaklaşık $retryAfterSeconds saniye sonra tekrar deneyin."
                                } else {
                                    "Çok fazla şifre sıfırlama isteği yapıldı. " +
                                            "Lütfen daha sonra tekrar deneyin."
                                }
                            }

                            400 ->
                                parseErrorMessage(
                                    errorJson
                                ) ?: "Gönderilen bilgiler doğrulanamadı."

                            else ->
                                parseErrorMessage(
                                    errorJson
                                ) ?: "Şifre sıfırlama isteği şu anda tamamlanamadı."
                        }

                    _uiState.value =
                        previousState.copy(
                            isLoading = false,
                            message = errorMessage,
                            isError = true
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Sunucuya bağlanılamadı. Backend'in açık olduğunu kontrol edin.",

                        isError = true
                    )
            } catch (_: Exception) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Şifre sıfırlama isteği sırasında beklenmeyen bir hata oluştu.",

                        isError = true
                    )
            }
        }
    }

    fun resendPasswordResetCode(
        email: String
    ) {
        if (_uiState.value.isLoading) {
            return
        }

        val normalizedEmail =
            email
                .trim()
                .lowercase()

        if (normalizedEmail.isBlank()) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    message =
                        "E-posta adresi bulunamadı.",
                    isError = true
                )

            return
        }

        viewModelScope.launch {
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
                    authRepository
                        .forgotPassword(
                            email =
                                normalizedEmail
                        )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        previousState.copy(
                            isLoading = false,

                            pendingPasswordResetEmail =
                                normalizedEmail,

                            passwordResetResendRequestVersion =
                                previousState
                                    .passwordResetResendRequestVersion + 1,

                            message =
                                responseBody.message,

                            isError = false
                        )
                } else {
                    val errorJson =
                        response
                            .errorBody()
                            ?.string()

                    val errorMessage =
                        when (response.code()) {
                            429 -> {
                                val retryAfterSeconds =
                                    response.headers()[
                                        "Retry-After"
                                    ]?.toIntOrNull()

                                if (
                                    retryAfterSeconds != null &&
                                    retryAfterSeconds > 0
                                ) {
                                    "Çok fazla şifre sıfırlama isteği yapıldı. " +
                                            "Yaklaşık $retryAfterSeconds saniye sonra tekrar deneyin."
                                } else {
                                    "Çok fazla şifre sıfırlama isteği yapıldı. " +
                                            "Lütfen daha sonra tekrar deneyin."
                                }
                            }

                            400 ->
                                parseErrorMessage(
                                    errorJson
                                ) ?: "Gönderilen bilgiler doğrulanamadı."

                            else ->
                                parseErrorMessage(
                                    errorJson
                                ) ?: "Yeni şifre sıfırlama kodu istenemedi."
                        }

                    _uiState.value =
                        previousState.copy(
                            isLoading = false,
                            message = errorMessage,
                            isError = true
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Sunucuya bağlanılamadı. Backend'in açık olduğunu kontrol edin.",

                        isError = true
                    )
            } catch (_: Exception) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Yeni şifre sıfırlama kodu istenirken bir hata oluştu.",

                        isError = true
                    )
            }
        }
    }

    fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ) {
        if (_uiState.value.isLoading) {
            return
        }

        val normalizedEmail =
            email
                .trim()
                .lowercase()

        val normalizedCode =
            code.trim()

        if (
            normalizedEmail.isBlank() ||
            normalizedCode.length != 6 ||
            normalizedCode.any {
                !it.isDigit()
            } ||
            newPassword.isBlank()
        ) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    message =
                        "Şifre sıfırlama bilgilerini kontrol edin.",
                    isError = true
                )

            return
        }

        viewModelScope.launch {
            val previousState =
                _uiState.value

            _uiState.value =
                previousState.copy(
                    isLoading = true,
                    passwordResetSuccessful =
                        false,
                    message = null,
                    isError = false
                )

            try {
                val response =
                    authRepository
                        .resetPassword(
                            email =
                                normalizedEmail,

                            code =
                                normalizedCode,

                            newPassword =
                                newPassword
                        )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        previousState.copy(
                            isLoading = false,

                            passwordResetSuccessful =
                                true,

                            pendingPasswordResetEmail =
                                normalizedEmail,

                            message =
                                responseBody.message,

                            isError = false
                        )
                } else {
                    val errorJson =
                        response
                            .errorBody()
                            ?.string()

                    val errorMessage =
                        when (response.code()) {
                            429 -> {
                                val retryAfterSeconds =
                                    response.headers()[
                                        "Retry-After"
                                    ]?.toIntOrNull()

                                if (
                                    retryAfterSeconds != null &&
                                    retryAfterSeconds > 0
                                ) {
                                    "Çok fazla şifre sıfırlama denemesi yapıldı. " +
                                            "Yaklaşık $retryAfterSeconds saniye sonra tekrar deneyin."
                                } else {
                                    "Çok fazla şifre sıfırlama denemesi yapıldı. " +
                                            "Lütfen daha sonra tekrar deneyin."
                                }
                            }

                            400 ->
                                parseErrorMessage(
                                    errorJson
                                ) ?: "Kod geçersiz, süresi dolmuş veya kullanılamıyor."

                            else ->
                                parseErrorMessage(
                                    errorJson
                                ) ?: "Şifre sıfırlama işlemi tamamlanamadı."
                        }

                    _uiState.value =
                        previousState.copy(
                            isLoading = false,
                            message = errorMessage,
                            isError = true
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Sunucuya bağlanılamadı. Backend'in açık olduğunu kontrol edin.",

                        isError = true
                    )
            } catch (_: Exception) {
                _uiState.value =
                    previousState.copy(
                        isLoading = false,

                        message =
                            "Şifre sıfırlanırken beklenmeyen bir hata oluştu.",

                        isError = true
                    )
            }
        }
    }

    /*
     * ForgotPasswordScreen -> ResetPasswordScreen
     * navigation olayı bir kez tüketilir.
     * E-posta state'te korunur.
     */
    fun consumePasswordResetRequestSuccessful() {
        _uiState.value =
            _uiState.value.copy(
                passwordResetRequestSuccessful =
                    false
            )
    }

    /*
     * Başarılı reset sonrası Login'e geçerken
     * başarı mesajı korunur; yalnızca reset
     * navigation state'i temizlenir.
     */
    fun consumePasswordResetSuccessful() {
        _uiState.value =
            _uiState.value.copy(
                passwordResetRequestSuccessful =
                    false,

                passwordResetSuccessful =
                    false,

                pendingPasswordResetEmail =
                    null,

                passwordResetResendRequestVersion =
                    0,

                isLoading = false,
                isError = false
            )
    }

    fun resetPasswordResetState() {
        _uiState.value =
            _uiState.value.copy(
                passwordResetRequestSuccessful =
                    false,

                passwordResetSuccessful =
                    false,

                pendingPasswordResetEmail =
                    null,

                passwordResetResendRequestVersion =
                    0,

                message = null,
                isError = false
            )
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
            val hasSession =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!hasSession) {
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

    /*
     * Login ekranındaki navigation olayı bir kez
     * tüketilir; pendingVerificationEmail korunur.
     */
    fun consumeEmailVerificationRequired() {
        _uiState.value =
            _uiState.value.copy(
                emailVerificationRequired = false
            )
    }

    fun resetEmailVerificationState() {
        _uiState.value =
            _uiState.value.copy(
                emailVerificationRequired = false,
                emailVerificationSuccessful = false,
                pendingVerificationEmail = null,
                resendRequestVersion = 0,
                message = null,
                isError = false
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

    private fun showVerificationError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
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

    private fun parseErrorCode(
        errorJson: String?
    ): String? {
        if (errorJson.isNullOrBlank()) {
            return null
        }

        return runCatching {
            JSONObject(errorJson)
                .optString("code")
                .takeIf {
                    it.isNotBlank()
                }
        }.getOrNull()
    }

    private fun parseErrorMessage(
        errorJson: String?
    ): String? {
        if (errorJson.isNullOrBlank()) {
            return null
        }

        return runCatching {
            val root =
                JSONObject(errorJson)

            /*
             * Bizim ApiResponse yapımızdan gelen
             * normal hata mesajı.
             */
            val message =
                root
                    .optString("message")
                    .takeIf {
                        it.isNotBlank()
                    }

            if (message != null) {
                return@runCatching message
            }

            /*
             * ASP.NET Core [ApiController]
             * model validation hataları:
             *
             * {
             *   "errors": {
             *      "Email": [...]
             *   }
             * }
             */
            val errors =
                root.optJSONObject(
                    "errors"
                )

            if (errors != null) {

                /*
                 * Kullanıcı formundaki alan sırasına
                 * göre anlamlı ilk hata gösterilir.
                 */
                val preferredFields =
                    listOf(
                        "FullName",
                        "fullName",
                        "Email",
                        "email",
                        "Password",
                        "password",
                        "Code",
                        "code",
                        "NewPassword",
                        "newPassword"
                    )

                for (field in preferredFields) {
                    val messages =
                        errors.optJSONArray(
                            field
                        )

                    val firstMessage =
                        messages
                            ?.optString(0)
                            ?.takeIf {
                                it.isNotBlank()
                            }

                    if (firstMessage != null) {
                        return@runCatching firstMessage
                    }
                }

                /*
                 * Beklenmeyen başka bir alan hatası
                 * geldiyse ilk mevcut mesajı al.
                 */
                val keys =
                    errors.keys()

                while (keys.hasNext()) {
                    val key =
                        keys.next()

                    val messages =
                        errors.optJSONArray(
                            key
                        )

                    val firstMessage =
                        messages
                            ?.optString(0)
                            ?.takeIf {
                                it.isNotBlank()
                            }

                    if (firstMessage != null) {
                        return@runCatching firstMessage
                    }
                }
            }

            root
                .optString("title")
                .takeIf {
                    it.isNotBlank()
                }
        }.getOrNull()
    }
}