package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CreateOrderRequest
import com.homemadefood.app.data.model.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrderApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @POST("api/Order")
    suspend fun createOrder(
        @Body
        request: CreateOrderRequest
    ): Response<ApiResponse<OrderResponse>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Order")
    suspend fun getOrders():
            Response<ApiResponse<List<OrderResponse>>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Order/{id}")
    suspend fun getOrderById(
        @Path("id")
        orderId: Int
    ): Response<ApiResponse<OrderResponse>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/Order/{id}/cancel")
    suspend fun cancelOrder(
        @Path("id")
        orderId: Int
    ): Response<ApiResponse<OrderResponse>>
}