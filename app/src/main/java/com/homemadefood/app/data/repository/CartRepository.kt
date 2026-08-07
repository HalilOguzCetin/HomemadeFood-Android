package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.AddCartItemRequest
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CartResponse
import com.homemadefood.app.data.model.ClearCartResponse
import com.homemadefood.app.data.model.UpdateCartItemRequest
import com.homemadefood.app.data.remote.CartApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class CartRepository(
    private val cartApiService:
    CartApiService =
        RetrofitClient.cartApiService
) {

    suspend fun getCart():
            Response<ApiResponse<CartResponse>> {

        return cartApiService
            .getCart()
    }

    suspend fun addItem(
        foodId: Int,
        quantity: Int,
        recommendationSearchId: Int? = null
    ): Response<ApiResponse<CartResponse>> {

        val request =
            AddCartItemRequest(
                foodId = foodId,
                quantity = quantity,
                recommendationSearchId =
                    recommendationSearchId
            )

        return cartApiService
            .addItem(
                request = request
            )
    }

    suspend fun updateItem(
        cartItemId: Int,
        quantity: Int
    ): Response<ApiResponse<CartResponse>> {

        val request =
            UpdateCartItemRequest(
                quantity = quantity
            )

        return cartApiService
            .updateItem(
                cartItemId = cartItemId,
                request = request
            )
    }

    suspend fun removeItem(
        cartItemId: Int
    ): Response<ApiResponse<CartResponse>> {

        return cartApiService
            .removeItem(
                cartItemId = cartItemId
            )
    }

    suspend fun clearCart():
            Response<ApiResponse<ClearCartResponse>> {

        return cartApiService
            .clearCart()
    }
}