package com.homemadefood.app.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.OrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class OrdersViewModel(
    private val orderRepository: OrderRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadOrdersJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            OrdersUiState()
        )

    val uiState: StateFlow<OrdersUiState> =
        _uiState.asStateFlow()

    fun loadOrders() {
        loadOrdersJob?.cancel()

        loadOrdersJob =
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
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        orderRepository.getOrders(
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
                            _uiState.value.copy(
                                isLoading = false,

                                orders =
                                    responseBody.data
                                        .sortedByDescending {
                                            it.orderId
                                        },

                                errorMessage = null
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,

                                errorMessage =
                                    parseErrorMessage(
                                        response.errorBody()
                                            ?.string()
                                    ) ?: "Siparişler alınamadı."
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
                                "Siparişler yüklenirken bir hata oluştu."
                        )
                }
            }
    }

    fun cancelOrder(
        orderId: Int
    ) {
        if (
            _uiState.value.cancellingOrderId != null
        ) {
            return
        }

        val order =
            _uiState.value.orders.firstOrNull {
                it.orderId == orderId
            }

        if (order == null) {
            showError(
                "İptal edilecek sipariş bulunamadı."
            )

            return
        }

        if (order.status != "Pending") {
            showError(
                "Yalnızca onay bekleyen siparişler iptal edilebilir."
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
                    cancellingOrderId = orderId,
                    errorMessage = null,
                    actionMessage = null
                )

            try {
                val response =
                    orderRepository.cancelOrder(
                        token = token,
                        orderId = orderId
                    )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    responseBody.data != null
                ) {
                    val updatedOrder =
                        responseBody.data

                    val updatedOrders =
                        _uiState.value.orders.map {
                                currentOrder ->

                            if (
                                currentOrder.orderId ==
                                updatedOrder.orderId
                            ) {
                                updatedOrder
                            } else {
                                currentOrder
                            }
                        }

                    _uiState.value =
                        _uiState.value.copy(
                            orders = updatedOrders,
                            cancellingOrderId = null,

                            actionMessage =
                                responseBody.message
                                    .ifBlank {
                                        "Sipariş iptal edildi."
                                    },

                            errorMessage = null
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Sipariş iptal edilemedi."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "Sipariş iptal edilirken bir hata oluştu."
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
                cancellingOrderId = null,
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