package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerOrderResponse
import com.homemadefood.app.data.repository.ProducerOrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class ProducerOrdersViewModel(
    private val producerOrderRepository:
    ProducerOrderRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadOrdersJob: Job? = null
    private var updateOrderJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            ProducerOrdersUiState()
        )

    val uiState:
            StateFlow<ProducerOrdersUiState> =
        _uiState.asStateFlow()

    fun loadOrders() {
        loadOrdersJob?.cancel()

        loadOrdersJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null,
                        successMessage = null
                    )

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
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
                        producerOrderRepository
                            .getMyOrders()

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        responseBody.data != null
                    ) {
                        _uiState.value =
                            ProducerOrdersUiState(
                                isLoading = false,

                                orders =
                                    responseBody.data
                                        .sortedByDescending {
                                            it.orderId
                                        }
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,

                                errorMessage =
                                    parseErrorMessage(
                                        response.errorBody()
                                            ?.string()
                                    ) ?: "Gelen siparişler alınamadı."
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

    fun acceptOrder(
        orderId: Int
    ) {
        updateOrderStatus(
            orderId = orderId,

            request = { id ->
                producerOrderRepository
                    .acceptOrder(
                        orderId = id
                    )
            },

            successMessage =
                "Sipariş kabul edildi."
        )
    }

    fun rejectOrder(
        orderId: Int
    ) {
        updateOrderStatus(
            orderId = orderId,

            request = { id ->
                producerOrderRepository
                    .rejectOrder(
                        orderId = id
                    )
            },

            successMessage =
                "Sipariş reddedildi."
        )
    }

    fun startPreparing(
        orderId: Int
    ) {
        updateOrderStatus(
            orderId = orderId,

            request = { id ->
                producerOrderRepository
                    .startPreparing(
                        orderId = id
                    )
            },

            successMessage =
                "Sipariş hazırlanmaya başlandı."
        )
    }

    fun markReady(
        orderId: Int
    ) {
        updateOrderStatus(
            orderId = orderId,

            request = { id ->
                producerOrderRepository
                    .markReady(
                        orderId = id
                    )
            },

            successMessage =
                "Sipariş hazırlandı."
        )
    }

    fun markOutForDelivery(
        orderId: Int
    ) {
        updateOrderStatus(
            orderId = orderId,

            request = { id ->
                producerOrderRepository
                    .markOutForDelivery(
                        orderId = id
                    )
            },

            successMessage =
                "Sipariş teslimata çıkarıldı."
        )
    }

    fun markDelivered(
        orderId: Int
    ) {
        updateOrderStatus(
            orderId = orderId,

            request = { id ->
                producerOrderRepository
                    .markDelivered(
                        orderId = id
                    )
            },

            successMessage =
                "Sipariş teslim edildi."
        )
    }

    private fun updateOrderStatus(
        orderId: Int,

        request:
        suspend (
            orderId: Int
        ) -> Response<
                ApiResponse<
                        ProducerOrderResponse
                        >
                >,

        successMessage: String
    ) {
        if (
            orderId <= 0 ||
            _uiState.value.updatingOrderId != null
        ) {
            return
        }

        updateOrderJob?.cancel()

        updateOrderJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        updatingOrderId = orderId,
                        successMessage = null,
                        errorMessage = null
                    )

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    _uiState.value =
                        _uiState.value.copy(
                            updatingOrderId = null,

                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        request(
                            orderId
                        )

                    val responseBody =
                        response.body()

                    val updatedOrder =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        updatedOrder != null
                    ) {
                        val updatedOrders =
                            _uiState.value.orders
                                .map { order ->
                                    if (
                                        order.orderId ==
                                        updatedOrder.orderId
                                    ) {
                                        updatedOrder
                                    } else {
                                        order
                                    }
                                }

                        _uiState.value =
                            _uiState.value.copy(
                                orders = updatedOrders,

                                updatingOrderId = null,

                                successMessage =
                                    successMessage,

                                errorMessage = null
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                updatingOrderId = null,

                                errorMessage =
                                    parseErrorMessage(
                                        response.errorBody()
                                            ?.string()
                                    ) ?: "Sipariş durumu güncellenemedi."
                            )
                    }
                } catch (_: IOException) {
                    _uiState.value =
                        _uiState.value.copy(
                            updatingOrderId = null,

                            errorMessage =
                                "Sunucuya bağlanılamadı."
                        )
                } catch (_: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            updatingOrderId = null,

                            errorMessage =
                                "Sipariş güncellenirken bir hata oluştu."
                        )
                }
            }
    }

    fun clearMessage() {
        _uiState.value =
            _uiState.value.copy(
                successMessage = null,
                errorMessage = null
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