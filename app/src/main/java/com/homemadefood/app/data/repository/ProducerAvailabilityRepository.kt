package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerAvailabilityMode
import com.homemadefood.app.data.model.ProducerAvailabilityResponse
import com.homemadefood.app.data.model.UpdateProducerAvailabilityModeRequest
import com.homemadefood.app.data.model.UpdateProducerBusinessHourRequest
import com.homemadefood.app.data.model.UpdateProducerBusinessHoursRequest
import com.homemadefood.app.data.remote.ProducerAvailabilityApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class ProducerAvailabilityRepository(
    private val producerAvailabilityApiService:
    ProducerAvailabilityApiService =
        RetrofitClient
            .producerAvailabilityApiService
) {

    suspend fun getMyAvailability():
            Response<
                    ApiResponse<
                            ProducerAvailabilityResponse
                            >
                    > {

        return producerAvailabilityApiService
            .getMyAvailability()
    }

    suspend fun updateMyBusinessHours(
        businessHours:
        List<UpdateProducerBusinessHourRequest>
    ): Response<
            ApiResponse<
                    ProducerAvailabilityResponse
                    >
            > {

        return producerAvailabilityApiService
            .updateMyBusinessHours(
                request =
                    UpdateProducerBusinessHoursRequest(
                        businessHours =
                            businessHours
                    )
            )
    }

    suspend fun updateMyAvailabilityMode(
        mode: ProducerAvailabilityMode
    ): Response<
            ApiResponse<
                    ProducerAvailabilityResponse
                    >
            > {

        return producerAvailabilityApiService
            .updateMyAvailabilityMode(
                request =
                    UpdateProducerAvailabilityModeRequest(
                        availabilityMode =
                            mode.backendValue
                    )
            )
    }
}
