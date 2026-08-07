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

    suspend fun getAddresses():
            Response<
                    ApiResponse<
                            List<AddressResponse>
                            >
                    > {

        return addressApiService
            .getAddresses()
    }

    suspend fun createAddress(
        request: CreateAddressRequest
    ): Response<ApiResponse<AddressResponse>> {

        return addressApiService
            .createAddress(
                request = request
            )
    }

    suspend fun updateAddress(
        addressId: Int,
        request: UpdateAddressRequest
    ): Response<ApiResponse<AddressResponse>> {

        return addressApiService
            .updateAddress(
                addressId = addressId,
                request = request
            )
    }

    suspend fun deleteAddress(
        addressId: Int
    ): Response<ApiResponse<AddressDeleteResponse>> {

        return addressApiService
            .deleteAddress(
                addressId = addressId
            )
    }
}