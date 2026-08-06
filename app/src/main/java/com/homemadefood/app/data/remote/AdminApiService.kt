package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.AdminOrderDetailResponse
import com.homemadefood.app.data.model.AdminOrderListItemResponse
import com.homemadefood.app.data.model.AdminProducerApplicationResponse
import com.homemadefood.app.data.model.AdminUserDetailResponse
import com.homemadefood.app.data.model.AdminUserListItemResponse
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.RecommendationPerformanceResponse
import com.homemadefood.app.data.model.RejectProducerApplicationRequest
import com.homemadefood.app.data.model.UpdateUserStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApiService {

    @GET("api/Admin/producer-applications")
    suspend fun getProducerApplications(
        @Header("Authorization")
        authorization: String,

        @Query("status")
        status: String
    ): Response<
            ApiResponse<
                    List<AdminProducerApplicationResponse>
                    >
            >

    @POST(
        "api/Admin/producer-applications/{id}/approve"
    )
    suspend fun approveProducerApplication(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        producerProfileId: Int
    ): Response<ApiResponse<Any?>>

    @POST(
        "api/Admin/producer-applications/{id}/reject"
    )
    suspend fun rejectProducerApplication(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        producerProfileId: Int,

        @Body
        request: RejectProducerApplicationRequest
    ): Response<ApiResponse<Any?>>

    @GET("api/Admin/users")
    suspend fun getUsers(
        @Header("Authorization")
        authorization: String,

        @Query("role")
        role: String?,

        @Query("isActive")
        isActive: Boolean?,

        @Query("search")
        search: String?
    ): Response<
            ApiResponse<
                    List<AdminUserListItemResponse>
                    >
            >

    @GET("api/Admin/users/{id}")
    suspend fun getUserById(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        userId: Int
    ): Response<
            ApiResponse<
                    AdminUserDetailResponse
                    >
            >

    @PATCH("api/Admin/users/{id}/status")
    suspend fun updateUserStatus(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        userId: Int,

        @Body
        request: UpdateUserStatusRequest
    ): Response<ApiResponse<Any?>>

    @GET("api/Admin/orders")
    suspend fun getOrders(
        @Header("Authorization")
        authorization: String,

        @Query("status")
        status: String?,

        @Query("customerId")
        customerId: Int?,

        @Query("producerProfileId")
        producerProfileId: Int?,

        @Query("search")
        search: String?,

        @Query("dateFrom")
        dateFrom: String?,

        @Query("dateTo")
        dateTo: String?
    ): Response<
            ApiResponse<
                    List<AdminOrderListItemResponse>
                    >
            >

    @GET("api/Admin/orders/{id}")
    suspend fun getOrderById(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    AdminOrderDetailResponse
                    >
            >

    @GET("api/RecommendationAnalytics/summary")
    suspend fun getRecommendationAnalytics(
        @Header("Authorization")
        authorization: String
    ): Response<
            ApiResponse<
                    RecommendationPerformanceResponse
                    >
            >
}