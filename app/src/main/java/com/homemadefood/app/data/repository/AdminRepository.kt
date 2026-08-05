package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.AdminProducerApplicationResponse
import com.homemadefood.app.data.model.AdminUserDetailResponse
import com.homemadefood.app.data.model.AdminUserListItemResponse
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.RecommendationPerformanceResponse
import com.homemadefood.app.data.model.RejectProducerApplicationRequest
import com.homemadefood.app.data.model.UpdateUserStatusRequest
import com.homemadefood.app.data.remote.AdminApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class AdminRepository(
    private val adminApiService:
    AdminApiService =
        RetrofitClient.adminApiService
) {

    suspend fun getProducerApplications(
        token: String,
        status: String = "Pending"
    ): Response<
            ApiResponse<
                    List<AdminProducerApplicationResponse>
                    >
            > {
        return adminApiService
            .getProducerApplications(
                authorization = "Bearer $token",
                status = status
            )
    }

    suspend fun approveProducerApplication(
        token: String,
        producerProfileId: Int
    ): Response<ApiResponse<Any?>> {
        return adminApiService
            .approveProducerApplication(
                authorization = "Bearer $token",
                producerProfileId =
                    producerProfileId
            )
    }

    suspend fun rejectProducerApplication(
        token: String,
        producerProfileId: Int,
        reason: String
    ): Response<ApiResponse<Any?>> {
        return adminApiService
            .rejectProducerApplication(
                authorization = "Bearer $token",
                producerProfileId =
                    producerProfileId,

                request =
                    RejectProducerApplicationRequest(
                        reason = reason.trim()
                    )
            )
    }

    suspend fun getUsers(
        token: String,
        role: String? = null,
        isActive: Boolean? = null,
        search: String? = null
    ): Response<
            ApiResponse<
                    List<AdminUserListItemResponse>
                    >
            > {
        return adminApiService
            .getUsers(
                authorization = "Bearer $token",

                role =
                    role
                        ?.trim()
                        ?.takeIf {
                            it.isNotBlank()
                        },

                isActive =
                    isActive,

                search =
                    search
                        ?.trim()
                        ?.takeIf {
                            it.isNotBlank()
                        }
            )
    }

    suspend fun getUserById(
        token: String,
        userId: Int
    ): Response<
            ApiResponse<
                    AdminUserDetailResponse
                    >
            > {
        return adminApiService
            .getUserById(
                authorization = "Bearer $token",
                userId = userId
            )
    }

    suspend fun updateUserStatus(
        token: String,
        userId: Int,
        isActive: Boolean
    ): Response<ApiResponse<Any?>> {
        return adminApiService
            .updateUserStatus(
                authorization = "Bearer $token",
                userId = userId,

                request =
                    UpdateUserStatusRequest(
                        isActive = isActive
                    )
            )
    }

    suspend fun getRecommendationAnalytics(
        token: String
    ): Response<
            ApiResponse<
                    RecommendationPerformanceResponse
                    >
            > {
        return adminApiService
            .getRecommendationAnalytics(
                authorization = "Bearer $token"
            )
    }
}