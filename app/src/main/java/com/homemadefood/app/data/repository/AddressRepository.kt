package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.AddressDeleteResponse
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CreateAddressRequest
import com.homemadefood.app.data.model.UpdateAddressRequest
import com.homemadefood.app.data.remote.AddressApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class AddressRepository(
    private val addressApiService:
    AddressApiService =
        RetrofitClient.addressApiService
) {

    suspend fun getAddresses(
        token: String
    ): Response<ApiResponse<List<AddressResponse>>> {

        return addressApiService.getAddresses(
            authorization = "Bearer $token"
        )
    }

    suspend fun createAddress(
        token: String,
        request: CreateAddressRequest
    ): Response<ApiResponse<AddressResponse>> {

        return addressApiService.createAddress(
            authorization = "Bearer $token",
            request = request
        )
    }

    suspend fun updateAddress(
        token: String,
        addressId: Int,
        request: UpdateAddressRequest
    ): Response<ApiResponse<AddressResponse>> {

        return addressApiService.updateAddress(
            authorization = "Bearer $token",
            addressId = addressId,
            request = request
        )
    }

    suspend fun deleteAddress(
        token: String,
        addressId: Int
    ): Response<ApiResponse<AddressDeleteResponse>> {

        return addressApiService.deleteAddress(
            authorization = "Bearer $token",
            addressId = addressId
        )
    }
}