package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerApplicationRequest
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.data.model.ProducerApplicationSubmitResponse
import com.homemadefood.app.data.remote.ProducerApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response
import com.homemadefood.app.data.model.UpdateProducerProfileRequest

class ProducerRepository(
    private val producerApiService:
    ProducerApiService =
        RetrofitClient.producerApiService
) {

    suspend fun apply(
        token: String,
        request: ProducerApplicationRequest
    ): Response<
            ApiResponse<
                    ProducerApplicationSubmitResponse
                    >
            > {
        return producerApiService.apply(
            authorization = "Bearer $token",
            request = request
        )
    }

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
    suspend fun getMyProfile(
        token: String
    ): Response<
            ApiResponse<
                    ProducerApplicationStatusResponse
                    >
            > {
        return producerApiService.getMyProfile(
            authorization = "Bearer $token"
        )
    }
    suspend fun updateMyProfile(
        token: String,
        businessName: String,
        description: String,
        address: String,
        latitude: Double,
        longitude: Double,
        dailyCapacity: Int,
        isAvailable: Boolean
    ): Response<
            ApiResponse<
                    ProducerApplicationStatusResponse
                    >
            > {
        return producerApiService.updateMyProfile(
            authorization = "Bearer $token",

            request =
                UpdateProducerProfileRequest(
                    businessName =
                        businessName,

                    description =
                        description,

                    address =
                        address,

                    latitude =
                        latitude,

                    longitude =
                        longitude,

                    dailyCapacity =
                        dailyCapacity,

                    isAvailable =
                        isAvailable
                )
        )
    }
}