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
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Admin/producer-applications")
    suspend fun getProducerApplications(
        @Query("status")
        status: String
    ): Response<
            ApiResponse<
                    List<AdminProducerApplicationResponse>
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @POST(
        "api/Admin/producer-applications/{id}/approve"
    )
    suspend fun approveProducerApplication(
        @Path("id")
        producerProfileId: Int
    ): Response<ApiResponse<Any?>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @POST(
        "api/Admin/producer-applications/{id}/reject"
    )
    suspend fun rejectProducerApplication(
        @Path("id")
        producerProfileId: Int,

        @Body
        request: RejectProducerApplicationRequest
    ): Response<ApiResponse<Any?>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Admin/users")
    suspend fun getUsers(
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

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Admin/users/{id}")
    suspend fun getUserById(
        @Path("id")
        userId: Int
    ): Response<
            ApiResponse<
                    AdminUserDetailResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PATCH("api/Admin/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id")
        userId: Int,

        @Body
        request: UpdateUserStatusRequest
    ): Response<ApiResponse<Any?>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Admin/orders")
    suspend fun getOrders(
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

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Admin/orders/{id}")
    suspend fun getOrderById(
        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    AdminOrderDetailResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/RecommendationAnalytics/summary")
    suspend fun getRecommendationAnalytics():
            Response<
                    ApiResponse<
                            RecommendationPerformanceResponse
                            >
                    >
}