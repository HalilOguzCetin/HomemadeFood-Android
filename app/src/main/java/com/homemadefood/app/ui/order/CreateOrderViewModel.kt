package com.homemadefood.app.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.PaymentMethods
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.CartRepository
import com.homemadefood.app.data.repository.OrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CreateOrderViewModel(
    private val cartRepository: CartRepository,
    private val addressRepository: AddressRepository,
    private val orderRepository: OrderRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadDataJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            CreateOrderUiState()
        )

    val uiState: StateFlow<CreateOrderUiState> =
        _uiState.asStateFlow()

    fun loadData() {
        loadDataJob?.cancel()

        loadDataJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null,
                        createdOrder = null
                    )

                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val cartRequest =
                        async {
                            cartRepository.getCart(
                                token = token
                            )
                        }

                    val addressesRequest =
                        async {
                            addressRepository.getAddresses(
                                token = token
                            )
                        }

                    val cartResponse =
                        cartRequest.await()

                    val addressesResponse =
                        addressesRequest.await()

                    val cartBody =
                        cartResponse.body()

                    val addressesBody =
                        addressesResponse.body()

                    if (
                        !cartResponse.isSuccessful ||
                        cartBody?.success != true ||
                        cartBody.data == null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,

                                errorMessage =
                                    parseErrorMessage(
                                        cartResponse
                                            .errorBody()
                                            ?.string()
                                    ) ?: "Sepet bilgisi alınamadı."
                            )

                        return@launch
                    }

                    if (
                        !addressesResponse.isSuccessful ||
                        addressesBody?.success != true ||
                        addressesBody.data == null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,

                                errorMessage =
                                    parseErrorMessage(
                                        addressesResponse
                                            .errorBody()
                                            ?.string()
                                    ) ?: "Adresler alınamadı."
                            )

                        return@launch
                    }

                    val addresses =
                        addressesBody.data

                    val defaultAddress =
                        addresses.firstOrNull {
                            it.isDefault
                        }

                    val selectedAddressId =
                        defaultAddress?.id
                            ?: addresses.firstOrNull()?.id

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            cart = cartBody.data,
                            addresses = addresses,
                            selectedAddressId =
                                selectedAddressId,
                            errorMessage = null
                        )
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
                                "Sipariş bilgileri yüklenirken bir hata oluştu."
                        )
                }
            }
    }

    fun selectAddress(
        addressId: Int
    ) {
        val addressExists =
            _uiState.value.addresses.any {
                it.id == addressId
            }

        if (!addressExists) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedAddressId = addressId,
                errorMessage = null
            )
    }

    fun selectPaymentMethod(
        paymentMethod: String
    ) {
        val isValid =
            paymentMethod ==
                    PaymentMethods.CASH_ON_DELIVERY ||
                    paymentMethod ==
                    PaymentMethods.CARD_ON_DELIVERY

        if (!isValid) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                paymentMethod = paymentMethod,
                errorMessage = null
            )
    }

    fun updateCustomerNote(
        value: String
    ) {
        if (value.length > 500) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                customerNote = value,
                errorMessage = null
            )
    }

    fun createOrder() {
        if (
            _uiState.value.isCreatingOrder ||
            _uiState.value.isLoading
        ) {
            return
        }

        val cart =
            _uiState.value.cart

        val selectedAddressId =
            _uiState.value.selectedAddressId

        when {
            cart == null ||
                    cart.items.isEmpty() -> {

                showError(
                    "Sipariş oluşturmak için sepetinizde ürün bulunmalıdır."
                )

                return
            }

            selectedAddressId == null -> {
                showError(
                    "Lütfen bir teslimat adresi seçin."
                )

                return
            }

            _uiState.value.addresses.none {
                it.id == selectedAddressId
            } -> {
                showError(
                    "Seçilen adres bulunamadı."
                )

                return
            }
        }

        viewModelScope.launch {
            val token =
                sessionManager.token.first()

            if (token.isNullOrBlank()) {
                showError(
                    "Oturum bilgisi bulunamadı. Yeniden giriş yapın."
                )

                return@launch
            }

            _uiState.value =
                _uiState.value.copy(
                    isCreatingOrder = true,
                    createdOrder = null,
                    errorMessage = null
                )

            try {
                val response =
                    orderRepository.createOrder(
                        token = token,
                        addressId = selectedAddressId,
                        paymentMethod =
                            _uiState.value.paymentMethod,
                        customerNote =
                            _uiState.value.customerNote
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
                            isCreatingOrder = false,
                            createdOrder =
                                responseBody.data,
                            errorMessage = null
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Sipariş oluşturulamadı."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "Sipariş oluşturulurken bir hata oluştu."
                )
            }
        }
    }

    fun resetCreatedOrder() {
        _uiState.value =
            _uiState.value.copy(
                createdOrder = null
            )
    }

    fun clearError() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = null
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                isCreatingOrder = false,
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