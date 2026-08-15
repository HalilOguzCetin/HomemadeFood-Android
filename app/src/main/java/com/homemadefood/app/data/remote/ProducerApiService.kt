package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.data.model.ProducerApplicationSubmitResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.http.PUT

interface ProducerApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @Multipart
    @POST("api/Producer/apply")
    suspend fun apply(
        @Part("BusinessName")
        businessName: RequestBody,

        @Part("Description")
        description: RequestBody,

        @Part("Address")
        address: RequestBody,

        @Part("City")
        city: RequestBody,

        @Part("District")
        district: RequestBody,

        @Part("Neighborhood")
        neighborhood: RequestBody,

        @Part("Street")
        street: RequestBody,

        @Part("BuildingNo")
        buildingNo: RequestBody,

        @Part("Floor")
        floor: RequestBody,

        @Part("ApartmentNo")
        apartmentNo: RequestBody,

        @Part("AddressNote")
        addressNote: RequestBody,

        @Part("Latitude")
        latitude: RequestBody,

        @Part("Longitude")
        longitude: RequestBody,

        @Part("DailyCapacity")
        dailyCapacity: RequestBody,

        @Part
        businessImage: MultipartBody.Part?
    ): Response<
            ApiResponse<
                    ProducerApplicationSubmitResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Producer/my-application")
    suspend fun getMyApplication():
            Response<
                    ApiResponse<
                            ProducerApplicationStatusResponse
                            >
                    >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Producer/my-profile")
    suspend fun getMyProfile():
            Response<
                    ApiResponse<
                            ProducerApplicationStatusResponse
                            >
                    >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @Multipart
    @PUT("api/Producer/my-profile")
    suspend fun updateMyProfile(
        @Part("BusinessName")
        businessName: RequestBody,

        @Part("Description")
        description: RequestBody,

        @Part("Address")
        address: RequestBody,

        @Part("City")
        city: RequestBody,

        @Part("District")
        district: RequestBody,

        @Part("Neighborhood")
        neighborhood: RequestBody,

        @Part("Street")
        street: RequestBody,

        @Part("BuildingNo")
        buildingNo: RequestBody,

        @Part("Floor")
        floor: RequestBody,

        @Part("ApartmentNo")
        apartmentNo: RequestBody,

        @Part("AddressNote")
        addressNote: RequestBody,

        @Part("Latitude")
        latitude: RequestBody,

        @Part("Longitude")
        longitude: RequestBody,

        @Part("DailyCapacity")
        dailyCapacity: RequestBody,

        @Part("IsAvailable")
        isAvailable: RequestBody,

        @Part
        businessImage: MultipartBody.Part?
    ): Response<
            ApiResponse<
                    ProducerApplicationStatusResponse
                    >
            >
}