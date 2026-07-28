package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CreateOrderRequest
import com.homemadefood.app.data.model.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrderApiService {

    @POST("api/Order")
    suspend fun createOrder(
        @Header("Authorization")
        authorization: String,

        @Body
        request: CreateOrderRequest
    ): Response<ApiResponse<OrderResponse>>

    @GET("api/Order")
    suspend fun getOrders(
        @Header("Authorization")
        authorization: String
    ): Response<ApiResponse<List<OrderResponse>>>

    @GET("api/Order/{id}")
    suspend fun getOrderById(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        orderId: Int
    ): Response<ApiResponse<OrderResponse>>

    @PUT("api/Order/{id}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        orderId: Int
    ): Response<ApiResponse<OrderResponse>>
}