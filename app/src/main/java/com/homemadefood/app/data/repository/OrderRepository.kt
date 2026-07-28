package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CreateOrderRequest
import com.homemadefood.app.data.model.OrderResponse
import com.homemadefood.app.data.remote.OrderApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class OrderRepository(
    private val orderApiService:
    OrderApiService =
        RetrofitClient.orderApiService
) {

    suspend fun createOrder(
        token: String,
        addressId: Int,
        paymentMethod: String,
        customerNote: String
    ): Response<ApiResponse<OrderResponse>> {

        val request =
            CreateOrderRequest(
                addressId = addressId,
                paymentMethod = paymentMethod,
                customerNote =
                    customerNote.trim()
            )

        return orderApiService.createOrder(
            authorization = "Bearer $token",
            request = request
        )
    }

    suspend fun getOrders(
        token: String
    ): Response<ApiResponse<List<OrderResponse>>> {

        return orderApiService.getOrders(
            authorization = "Bearer $token"
        )
    }

    suspend fun getOrderById(
        token: String,
        orderId: Int
    ): Response<ApiResponse<OrderResponse>> {

        return orderApiService.getOrderById(
            authorization = "Bearer $token",
            orderId = orderId
        )
    }

    suspend fun cancelOrder(
        token: String,
        orderId: Int
    ): Response<ApiResponse<OrderResponse>> {

        return orderApiService.cancelOrder(
            authorization = "Bearer $token",
            orderId = orderId
        )
    }
}