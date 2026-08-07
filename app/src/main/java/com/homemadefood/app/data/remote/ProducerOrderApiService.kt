package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerOrderResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProducerOrderApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/ProducerOrder")
    suspend fun getMyOrders():
            Response<
                    ApiResponse<
                            List<ProducerOrderResponse>
                            >
                    >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/ProducerOrder/{id}/accept")
    suspend fun acceptOrder(
        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/ProducerOrder/{id}/reject")
    suspend fun rejectOrder(
        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/ProducerOrder/{id}/start-preparing")
    suspend fun startPreparing(
        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/ProducerOrder/{id}/ready")
    suspend fun markReady(
        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/ProducerOrder/{id}/out-for-delivery")
    suspend fun markOutForDelivery(
        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/ProducerOrder/{id}/delivered")
    suspend fun markDelivered(
        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >
}