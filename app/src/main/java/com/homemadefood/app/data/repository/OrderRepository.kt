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

        return orderApiService
            .createOrder(
                request = request
            )
    }

    suspend fun getOrders():
            Response<ApiResponse<List<OrderResponse>>> {

        return orderApiService
            .getOrders()
    }

    suspend fun getOrderById(
        orderId: Int
    ): Response<ApiResponse<OrderResponse>> {

        return orderApiService
            .getOrderById(
                orderId = orderId
            )
    }

    suspend fun cancelOrder(
        orderId: Int
    ): Response<ApiResponse<OrderResponse>> {

        return orderApiService
            .cancelOrder(
                orderId = orderId
            )
    }
}