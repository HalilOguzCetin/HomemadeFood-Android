package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.EmailVerificationResponse
import com.homemadefood.app.data.model.LoginRequest
import com.homemadefood.app.data.model.LoginResponse
import com.homemadefood.app.data.model.RegisterRequest
import com.homemadefood.app.data.model.RegisterResponse
import com.homemadefood.app.data.model.ResendEmailVerificationRequest
import com.homemadefood.app.data.model.UserProfileResponse
import com.homemadefood.app.data.model.VerifyEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

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
}