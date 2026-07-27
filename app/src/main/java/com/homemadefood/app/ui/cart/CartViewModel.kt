package com.homemadefood.app.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.CartRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CartViewModel(
    private val cartRepository: CartRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadCartJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            CartUiState(
                isLoading = true
            )
        )

    val uiState: StateFlow<CartUiState> =
        _uiState.asStateFlow()

    fun loadCart() {
        loadCartJob?.cancel()

        loadCartJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null,
                        actionMessage = null
                    )

                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    _uiState.value =
                        CartUiState(
                            isLoading = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        cartRepository.getCart(
                            token = token
                        )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        responseBody.data != null
                    ) {
                        _uiState.value =
                            CartUiState(
                                isLoading = false,
                                cart = responseBody.data
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,

                                errorMessage =
                                    parseErrorMessage(
                                        response.errorBody()
                                            ?.string()
                                    ) ?: "Sepet bilgisi alınamadı."
                            )
                    }
                } catch (_: IOException) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage =
                                "Sunucuya bağlanılamadı."
                        )
                } catch (_: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage =
                                "Sepet yüklenirken bir hata oluştu."
                        )
                }
            }
    }

    fun updateQuantity(
        cartItemId: Int,
        newQuantity: Int
    ) {
        if (
            _uiState.value.updatingCartItemId != null ||
            _uiState.value.isClearingCart
        ) {
            return
        }

        if (newQuantity < 1) {
            removeItem(
                cartItemId = cartItemId
            )

            return
        }

        if (newQuantity > 50) {
            _uiState.value =
                _uiState.value.copy(
                    errorMessage =
                        "Bir üründen en fazla 50 adet eklenebilir."
                )

            return
        }

        viewModelScope.launch {
            val token =
                sessionManager.token.first()

            if (token.isNullOrBlank()) {
                showError(
                    "Oturum bilgisi bulunamadı."
                )

                return@launch
            }

            _uiState.value =
                _uiState.value.copy(
                    updatingCartItemId = cartItemId,
                    errorMessage = null,
                    actionMessage = null
                )

            try {
                val response =
                    cartRepository.updateItem(
                        token = token,
                        cartItemId = cartItemId,
                        quantity = newQuantity
                    )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    responseBody.data != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            cart = responseBody.data,
                            updatingCartItemId = null,
                            actionMessage =
                                responseBody.message
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Ürün miktarı güncellenemedi."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "Miktar güncellenirken bir hata oluştu."
                )
            }
        }
    }

    fun removeItem(
        cartItemId: Int
    ) {
        if (
            _uiState.value.updatingCartItemId != null ||
            _uiState.value.isClearingCart
        ) {
            return
        }

        viewModelScope.launch {
            val token =
                sessionManager.token.first()

            if (token.isNullOrBlank()) {
                showError(
                    "Oturum bilgisi bulunamadı."
                )

                return@launch
            }

            _uiState.value =
                _uiState.value.copy(
                    updatingCartItemId = cartItemId,
                    errorMessage = null,
                    actionMessage = null
                )

            try {
                val response =
                    cartRepository.removeItem(
                        token = token,
                        cartItemId = cartItemId
                    )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    responseBody.data != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            cart = responseBody.data,
                            updatingCartItemId = null,
                            actionMessage =
                                responseBody.message
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Ürün sepetten çıkarılamadı."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "Ürün silinirken bir hata oluştu."
                )
            }
        }
    }

    fun clearCart() {
        if (
            _uiState.value.isClearingCart ||
            _uiState.value.updatingCartItemId != null
        ) {
            return
        }

        viewModelScope.launch {
            val token =
                sessionManager.token.first()

            if (token.isNullOrBlank()) {
                showError(
                    "Oturum bilgisi bulunamadı."
                )

                return@launch
            }

            _uiState.value =
                _uiState.value.copy(
                    isClearingCart = true,
                    errorMessage = null,
                    actionMessage = null
                )

            try {
                val response =
                    cartRepository.clearCart(
                        token = token
                    )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            cart =
                                _uiState.value.cart?.copy(
                                    items = emptyList(),
                                    totalQuantity = 0,
                                    totalPrice = 0.0
                                ),

                            isClearingCart = false,

                            actionMessage =
                                responseBody.message
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Sepet temizlenemedi."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "Sepet temizlenirken bir hata oluştu."
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = null,
                actionMessage = null
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                updatingCartItemId = null,
                isClearingCart = false,
                errorMessage = message
            )
    }

    private fun parseErrorMessage(
        errorJson: String?
    ): String? {
        if (errorJson.isNullOrBlank()) {
            return null
        }

        return runCatching {
            JSONObject(errorJson)
                .optString("message")
                .takeIf {
                    it.isNotBlank()
                }
        }.getOrNull()
    }
}