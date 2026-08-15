package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerApplicationRequest
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.data.model.ProducerApplicationSubmitResponse
import com.homemadefood.app.data.model.UpdateProducerProfileRequest
import com.homemadefood.app.data.remote.ProducerApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class ProducerRepository(
    private val producerApiService:
    ProducerApiService =
        RetrofitClient.producerApiService
) {

    suspend fun apply(
        request: ProducerApplicationRequest
    ): Response<
            ApiResponse<
                    ProducerApplicationSubmitResponse
                    >
            > {

        return producerApiService.apply(
            request = request
        )
    }

    suspend fun getMyApplication():
            Response<
                    ApiResponse<
                            ProducerApplicationStatusResponse
                            >
                    > {

        return producerApiService
            .getMyApplication()
    }

    suspend fun getMyProfile():
            Response<
                    ApiResponse<
                            ProducerApplicationStatusResponse
                            >
                    > {

        return producerApiService
            .getMyProfile()
    }

    suspend fun updateMyProfile(
        businessName: String,
        description: String,

        address: String,

        city: String,
        district: String,
        neighborhood: String,
        street: String,
        buildingNo: String,
        floor: String?,
        apartmentNo: String?,
        addressNote: String?,

        latitude: Double,
        longitude: Double,

        dailyCapacity: Int,
        isAvailable: Boolean
    ): Response<
            ApiResponse<
                    ProducerApplicationStatusResponse
                    >
            > {

        return producerApiService
            .updateMyProfile(
                request =
                    UpdateProducerProfileRequest(
                        businessName =
                            businessName,

                        description =
                            description,

                        address =
                            address,

                        city =
                            city,

                        district =
                            district,

                        neighborhood =
                            neighborhood,

                        street =
                            street,

                        buildingNo =
                            buildingNo,

                        floor =
                            floor,

                        apartmentNo =
                            apartmentNo,

                        addressNote =
                            addressNote,

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