package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.LoginRequest
import com.homemadefood.app.data.model.LoginResponse
import com.homemadefood.app.data.model.RegisterRequest
import com.homemadefood.app.data.model.RegisterResponse
import com.homemadefood.app.data.model.UserProfileResponse
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