package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.LoginRequest
import com.homemadefood.app.data.model.LoginResponse
import com.homemadefood.app.data.model.RegisterRequest
import com.homemadefood.app.data.model.RegisterResponse
import com.homemadefood.app.data.model.UserProfileResponse
import com.homemadefood.app.data.remote.AuthApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class AuthRepository(
    private val authApiService: AuthApiService =
        RetrofitClient.authApiService
) {

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String
    ): Response<ApiResponse<RegisterResponse>> {

        val request = RegisterRequest(
            fullName = fullName.trim(),
            email = email.trim(),
            password = password,
            phone = phone.trim()
        )

        return authApiService.register(request)
    }

    suspend fun login(
        email: String,
        password: String
    ): Response<ApiResponse<LoginResponse>> {

        val request = LoginRequest(
            email = email.trim(),
            password = password
        )

        return authApiService.login(request)
    }

    suspend fun getProfile(
        token: String
    ): Response<ApiResponse<UserProfileResponse>> {

        return authApiService.getProfile(
            authorization = "Bearer $token"
        )
    }
}