package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerApplicationRequest
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.data.model.ProducerApplicationSubmitResponse
import com.homemadefood.app.data.remote.ProducerApiService
import com.homemadefood.app.data.remote.RetrofitClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class ProducerRepository(
    private val producerApiService:
    ProducerApiService =
        RetrofitClient.producerApiService
) {

    private val plainTextMediaType =
        "text/plain".toMediaType()

    suspend fun apply(
        request: ProducerApplicationRequest,
        businessImage: MultipartBody.Part?
    ): Response<
            ApiResponse<
                    ProducerApplicationSubmitResponse
                    >
            > {

        return producerApiService.apply(
            businessName =
                request.businessName
                    .toRequestBody(
                        plainTextMediaType
                    ),

            description =
                request.description
                    .toRequestBody(
                        plainTextMediaType
                    ),

            address =
                request.address
                    .toRequestBody(
                        plainTextMediaType
                    ),

            city =
                request.city
                    .toRequestBody(
                        plainTextMediaType
                    ),

            district =
                request.district
                    .toRequestBody(
                        plainTextMediaType
                    ),

            neighborhood =
                request.neighborhood
                    .toRequestBody(
                        plainTextMediaType
                    ),

            street =
                request.street
                    .toRequestBody(
                        plainTextMediaType
                    ),

            buildingNo =
                request.buildingNo
                    .toRequestBody(
                        plainTextMediaType
                    ),

            floor =
                request.floor
                    .orEmpty()
                    .toRequestBody(
                        plainTextMediaType
                    ),

            apartmentNo =
                request.apartmentNo
                    .orEmpty()
                    .toRequestBody(
                        plainTextMediaType
                    ),

            addressNote =
                request.addressNote
                    .orEmpty()
                    .toRequestBody(
                        plainTextMediaType
                    ),

            latitude =
                request.latitude
                    .toString()
                    .toRequestBody(
                        plainTextMediaType
                    ),

            longitude =
                request.longitude
                    .toString()
                    .toRequestBody(
                        plainTextMediaType
                    ),

            dailyCapacity =
                request.dailyCapacity
                    .toString()
                    .toRequestBody(
                        plainTextMediaType
                    ),

            businessImage =
                businessImage
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
        isAvailable: Boolean,

        businessImage: MultipartBody.Part?
    ): Response<
            ApiResponse<
                    ProducerApplicationStatusResponse
                    >
            > {

        return producerApiService
            .updateMyProfile(
                businessName =
                    businessName.toRequestBody(
                        plainTextMediaType
                    ),

                description =
                    description.toRequestBody(
                        plainTextMediaType
                    ),

                address =
                    address.toRequestBody(
                        plainTextMediaType
                    ),

                city =
                    city.toRequestBody(
                        plainTextMediaType
                    ),

                district =
                    district.toRequestBody(
                        plainTextMediaType
                    ),

                neighborhood =
                    neighborhood.toRequestBody(
                        plainTextMediaType
                    ),

                street =
                    street.toRequestBody(
                        plainTextMediaType
                    ),

                buildingNo =
                    buildingNo.toRequestBody(
                        plainTextMediaType
                    ),

                floor =
                    floor.orEmpty()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                apartmentNo =
                    apartmentNo.orEmpty()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                addressNote =
                    addressNote.orEmpty()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                latitude =
                    latitude.toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                longitude =
                    longitude.toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                dailyCapacity =
                    dailyCapacity.toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                isAvailable =
                    isAvailable.toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                businessImage =
                    businessImage
            )
    }
}