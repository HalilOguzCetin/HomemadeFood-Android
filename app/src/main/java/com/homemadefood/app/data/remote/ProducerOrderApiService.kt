package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerOrderResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProducerOrderApiService {

    @GET("api/ProducerOrder")
    suspend fun getMyOrders(
        @Header("Authorization")
        authorization: String
    ): Response<
            ApiResponse<
                    List<ProducerOrderResponse>
                    >
            >

    @PUT("api/ProducerOrder/{id}/accept")
    suspend fun acceptOrder(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >

    @PUT("api/ProducerOrder/{id}/reject")
    suspend fun rejectOrder(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >

    @PUT("api/ProducerOrder/{id}/start-preparing")
    suspend fun startPreparing(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >

    @PUT("api/ProducerOrder/{id}/ready")
    suspend fun markReady(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >

    @PUT("api/ProducerOrder/{id}/out-for-delivery")
    suspend fun markOutForDelivery(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >

    @PUT("api/ProducerOrder/{id}/delivered")
    suspend fun markDelivered(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            >
}