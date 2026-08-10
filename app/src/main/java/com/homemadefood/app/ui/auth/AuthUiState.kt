package com.homemadefood.app.ui.auth

import com.homemadefood.app.data.model.AppMode

data class AuthUiState(
    val isSessionChecking: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,

    val registrationSuccessful: Boolean = false,

    /*
     * Login doğru şifreyle yapıldı ancak
     * e-posta doğrulanmamışsa navigation
     * doğrulama ekranına yönlenir.
     */
    val emailVerificationRequired: Boolean = false,

    /*
     * Register başarılı olduğunda veya login
     * doğrulama gerektirdiğinde doğrulama
     * ekranına taşınacak e-posta adresi.
     */
    val pendingVerificationEmail: String? = null,

    /*
     * Verify-email başarılı olduğunda navigation
     * Login ekranına yönlendirme yapar.
     */
    val emailVerificationSuccessful: Boolean = false,

    /*
     * Başarılı resend HTTP cevabında artırılır.
     * VerificationScreen sayaç yenilemek için
     * bu değeri gözlemler.
     */
    val resendRequestVersion: Int = 0,

    /*
     * Forgot-password başarılı HTTP cevabından
     * sonra ResetPasswordScreen'e geçiş olayı.
     */
    val passwordResetRequestSuccessful: Boolean = false,

    /*
     * Reset ekranının kullanacağı normalize
     * edilmiş e-posta adresi.
     */
    val pendingPasswordResetEmail: String? = null,

    /*
     * Reset-password başarılı olduğunda
     * Login ekranına dönme olayı.
     */
    val passwordResetSuccessful: Boolean = false,

    /*
     * Reset ekranında yeni kod istendiğinde
     * 60 saniyelik UI sayacını yeniler.
     */
    val passwordResetResendRequestVersion: Int = 0,

    val message: String? = null,
    val isError: Boolean = false,

    val userRole: String? = null,

    val canUseProducerMode: Boolean = false,

    val producerProfileId: Int? = null,

    val producerVerificationStatus: String? = null,

    val activeMode: AppMode? = null
)