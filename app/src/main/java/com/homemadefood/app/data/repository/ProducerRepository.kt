package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.data.remote.ProducerApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class ProducerRepository(
    private val producerApiService:
    ProducerApiService =
        RetrofitClient.producerApiService
) {

    suspend fun getMyApplication(
        token: String
    ): Response<
            ApiResponse<
                    ProducerApplicationStatusResponse
                    >
            > {
        return producerApiService
            .getMyApplication(
                authorization = "Bearer $token"
            )
    }
}