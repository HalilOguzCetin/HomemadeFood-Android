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

class OrderDetailViewModel(
    private val orderId: Int,
    private val orderRepository: OrderRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadOrderJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            OrderDetailUiState()
        )

    val uiState: StateFlow<OrderDetailUiState> =
        _uiState.asStateFlow()

    fun loadOrder() {
        loadOrderJob?.cancel()

        loadOrderJob =
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
                        orderRepository.getOrderById(
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
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                order = responseBody.data,
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
                                    ) ?: "Sipariş detayı alınamadı."
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
                                "Sipariş detayı yüklenirken bir hata oluştu."
                        )
                }
            }
    }

    fun cancelOrder() {
        if (_uiState.value.isCancelling) {
            return
        }

        val currentOrder =
            _uiState.value.order

        if (currentOrder == null) {
            showError(
                "Sipariş bilgisi bulunamadı."
            )

            return
        }

        if (currentOrder.status != "Pending") {
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
                    isCancelling = true,
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
                    _uiState.value =
                        _uiState.value.copy(
                            order = responseBody.data,
                            isCancelling = false,

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
                isCancelling = false,
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