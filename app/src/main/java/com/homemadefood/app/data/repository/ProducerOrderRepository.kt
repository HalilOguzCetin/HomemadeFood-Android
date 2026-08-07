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

    suspend fun getMyOrders():
            Response<
                    ApiResponse<
                            List<ProducerOrderResponse>
                            >
                    > {

        return producerOrderApiService
            .getMyOrders()
    }

    suspend fun acceptOrder(
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {

        return producerOrderApiService
            .acceptOrder(
                orderId = orderId
            )
    }

    suspend fun rejectOrder(
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {

        return producerOrderApiService
            .rejectOrder(
                orderId = orderId
            )
    }

    suspend fun startPreparing(
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {

        return producerOrderApiService
            .startPreparing(
                orderId = orderId
            )
    }

    suspend fun markReady(
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {

        return producerOrderApiService
            .markReady(
                orderId = orderId
            )
    }

    suspend fun markOutForDelivery(
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {

        return producerOrderApiService
            .markOutForDelivery(
                orderId = orderId
            )
    }

    suspend fun markDelivered(
        orderId: Int
    ): Response<
            ApiResponse<
                    ProducerOrderResponse
                    >
            > {

        return producerOrderApiService
            .markDelivered(
                orderId = orderId
            )
    }
}