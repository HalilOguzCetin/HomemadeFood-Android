package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.EmailVerificationResponse
import com.homemadefood.app.data.model.ForgotPasswordRequest
import com.homemadefood.app.data.model.LoginRequest
import com.homemadefood.app.data.model.LoginResponse
import com.homemadefood.app.data.model.PasswordResetResponse
import com.homemadefood.app.data.model.RegisterRequest
import com.homemadefood.app.data.model.RegisterResponse
import com.homemadefood.app.data.model.ResendEmailVerificationRequest
import com.homemadefood.app.data.model.ResetPasswordRequest
import com.homemadefood.app.data.model.UserProfileResponse
import com.homemadefood.app.data.model.VerifyEmailRequest
import com.homemadefood.app.data.remote.AuthApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class AuthRepository(
    private val authApiService:
    AuthApiService =
        RetrofitClient.authApiService
) {

    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Response<ApiResponse<RegisterResponse>> {

        val request =
            RegisterRequest(
                fullName =
                    fullName.trim(),

                email =
                    email
                        .trim()
                        .lowercase(),

                password =
                    password
            )

        return authApiService
            .register(
                request = request
            )
    }

    suspend fun verifyEmail(
        email: String,
        code: String
    ): Response<
            ApiResponse<EmailVerificationResponse>
            > {

        val request =
            VerifyEmailRequest(
                email =
                    email
                        .trim()
                        .lowercase(),

                code =
                    code.trim()
            )

        return authApiService
            .verifyEmail(
                request = request
            )
    }

    suspend fun resendEmailVerification(
        email: String
    ): Response<
            ApiResponse<EmailVerificationResponse>
            > {

        val request =
            ResendEmailVerificationRequest(
                email =
                    email
                        .trim()
                        .lowercase()
            )

        return authApiService
            .resendEmailVerification(
                request = request
            )
    }

    suspend fun forgotPassword(
        email: String
    ): Response<
            ApiResponse<PasswordResetResponse>
            > {

        val request =
            ForgotPasswordRequest(
                email =
                    email
                        .trim()
                        .lowercase()
            )

        return authApiService
            .forgotPassword(
                request = request
            )
    }

    suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): Response<
            ApiResponse<PasswordResetResponse>
            > {

        val request =
            ResetPasswordRequest(
                email =
                    email
                        .trim()
                        .lowercase(),

                code =
                    code.trim(),

                newPassword =
                    newPassword
            )

        return authApiService
            .resetPassword(
                request = request
            )
    }

    suspend fun login(
        email: String,
        password: String
    ): Response<
            ApiResponse<LoginResponse>
            > {

        val request =
            LoginRequest(
                email =
                    email.trim(),

                password =
                    password
            )

        return authApiService
            .login(
                request = request
            )
    }

    suspend fun getProfile():
            Response<
                    ApiResponse<UserProfileResponse>
                    > {

        return authApiService
            .getProfile()
    }
}