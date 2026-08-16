package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.EmailVerificationResponse
import com.homemadefood.app.data.model.ForgotPasswordRequest
import com.homemadefood.app.data.model.LoginRequest
import com.homemadefood.app.data.model.LoginResponse
import com.homemadefood.app.data.model.PasswordResetResponse
import com.homemadefood.app.data.model.PhoneVerificationRequestResponse
import com.homemadefood.app.data.model.RequestPhoneVerificationRequest
import com.homemadefood.app.data.model.RegisterRequest
import com.homemadefood.app.data.model.RegisterResponse
import com.homemadefood.app.data.model.ResendEmailVerificationRequest
import com.homemadefood.app.data.model.ResetPasswordRequest
import com.homemadefood.app.data.model.UpdateUserProfileRequest
import com.homemadefood.app.data.model.UserProfileResponse
import com.homemadefood.app.data.model.VerifyEmailRequest
import com.homemadefood.app.data.model.VerifyPhoneRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {

    /*
     * Public endpoint.
     * Authorization header gönderilmez.
     */
    @POST("api/Auth/register")
    suspend fun register(
        @Body
        request: RegisterRequest
    ): Response<
            ApiResponse<RegisterResponse>
            >

    /*
     * Public endpoint.
     * Kullanıcı henüz giriş yapmadan
     * e-posta adresini doğrular.
     */
    @POST("api/Auth/verify-email")
    suspend fun verifyEmail(
        @Body
        request: VerifyEmailRequest
    ): Response<
            ApiResponse<EmailVerificationResponse>
            >

    /*
     * Public endpoint.
     * Yeni doğrulama kodu talep edilir.
     */
    @POST("api/Auth/resend-email-verification")
    suspend fun resendEmailVerification(
        @Body
        request: ResendEmailVerificationRequest
    ): Response<
            ApiResponse<EmailVerificationResponse>
            >

    /*
     * Public endpoint.
     * Hesabın varlığını açıklamadan
     * şifre sıfırlama kodu talep eder.
     */
    @POST("api/Auth/forgot-password")
    suspend fun forgotPassword(
        @Body
        request: ForgotPasswordRequest
    ): Response<
            ApiResponse<PasswordResetResponse>
            >

    /*
     * Public endpoint.
     * Kod + yeni şifre ile parolayı yeniler.
     */
    @POST("api/Auth/reset-password")
    suspend fun resetPassword(
        @Body
        request: ResetPasswordRequest
    ): Response<
            ApiResponse<PasswordResetResponse>
            >

    /*
     * Public endpoint.
     * Authorization header gönderilmez.
     */
    @POST("api/Auth/login")
    suspend fun login(
        @Body
        request: LoginRequest
    ): Response<
            ApiResponse<LoginResponse>
            >

    /*
     * Korumalı endpoint.
     *
     * JWT AuthorizationInterceptor tarafından
     * Android Keystore'dan alınarak eklenir.
     */
    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Auth/profile")
    suspend fun getProfile():
            Response<
                    ApiResponse<UserProfileResponse>
                    >

    /*
     * C2 müşteri profil düzenleme.
     * Şu anda yalnızca FullName güncellenir.
     * E-posta/telefon güvenlik nedeniyle ayrı
     * doğrulama akışlarında tutulur.
     */
    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/Auth/profile")
    suspend fun updateProfile(
        @Body
        request: UpdateUserProfileRequest
    ): Response<
            ApiResponse<UserProfileResponse>
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @POST("api/Auth/phone/request-code")
    suspend fun requestPhoneVerification(
        @Body
        request: RequestPhoneVerificationRequest
    ): Response<
            ApiResponse<PhoneVerificationRequestResponse>
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @POST("api/Auth/phone/verify")
    suspend fun verifyPhone(
        @Body
        request: VerifyPhoneRequest
    ): Response<
            ApiResponse<UserProfileResponse>
            >
}