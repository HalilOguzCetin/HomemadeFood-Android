package com.homemadefood.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AdminRepository
import java.io.IOException
import com.homemadefood.app.data.remote.ApiErrorParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdminOrderDetailViewModel(
    private val adminRepository:
    AdminRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadOrderJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            AdminOrderDetailUiState()
        )

    val uiState:
            StateFlow<AdminOrderDetailUiState> =
        _uiState.asStateFlow()

    fun loadOrder(
        orderId: Int
    ) {
        if (orderId <= 0) {
            _uiState.value =
                AdminOrderDetailUiState(
                    isLoading = false,

                    errorMessage =
                        "Geçersiz sipariş bilgisi."
                )

            return
        }

        loadOrderJob?.cancel()

        loadOrderJob =
            viewModelScope.launch {
                _uiState.value =
                    AdminOrderDetailUiState(
                        isLoading = true
                    )

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    showError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                try {
                    val response =
                        adminRepository.getOrderById(
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
                            AdminOrderDetailUiState(
                                isLoading = false,
                                order = responseBody.data,
                                errorMessage = null
                            )
                    } else {
                        showError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Sipariş detayı alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Sipariş detayı yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            AdminOrderDetailUiState(
                isLoading = false,
                order = null,
                errorMessage = message
            )
    }

    private fun parseErrorMessage(
        errorJson: String?
    ): String? {
        return ApiErrorParser
            .parse(
                errorJson
            )
            .message
    }
}