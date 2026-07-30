package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerOrderResponse
import com.homemadefood.app.data.remote.ProducerOrderApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class ProducerOrderRepository(
    private val producerOrderApiService:
    ProducerOrderApiService =
        RetrofitClient.producerOrderApiService
) {

    suspend fun getMyOrders(
        token: String
    ): Response<
            ApiResponse<
                    List<ProducerOrderResponse>
                    >
            > {
        return producerOrderApiService.getMyOrders(
            authorization = "Bearer $token"
        )
    }

    suspend fun acceptOrder(
        token: String,
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {
        return producerOrderApiService.acceptOrder(
            authorization = "Bearer $token",
            orderId = orderId
        )
    }

    suspend fun rejectOrder(
        token: String,
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {
        return producerOrderApiService.rejectOrder(
            authorization = "Bearer $token",
            orderId = orderId
        )
    }

    suspend fun startPreparing(
        token: String,
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {
        return producerOrderApiService.startPreparing(
            authorization = "Bearer $token",
            orderId = orderId
        )
    }

    suspend fun markReady(
        token: String,
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {
        return producerOrderApiService.markReady(
            authorization = "Bearer $token",
            orderId = orderId
        )
    }

    suspend fun markOutForDelivery(
        token: String,
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {
        return producerOrderApiService
            .markOutForDelivery(
                authorization = "Bearer $token",
                orderId = orderId
            )
    }

    suspend fun markDelivered(
        token: String,
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {
        return producerOrderApiService.markDelivered(
            authorization = "Bearer $token",
            orderId = orderId
        )
    }
}