package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.repository.StorefrontRepository
import com.homemadefood.app.data.remote.ApiErrorParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class StorefrontMenuViewModel(
    private val producerProfileId: Int,

    private val storefrontRepository:
    StorefrontRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            StorefrontMenuUiState()
        )

    val uiState:
            StateFlow<StorefrontMenuUiState> =
        _uiState.asStateFlow()

    fun loadMenu() {
        if (producerProfileId <= 0) {
            _uiState.value =
                StorefrontMenuUiState(
                    isLoading = false,
                    errorMessage =
                        "İşletme bilgisi geçersiz."
                )
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

            try {
                val response =
                    storefrontRepository
                        .getStorefrontMenu(
                            producerProfileId =
                                producerProfileId
                        )

                val responseBody =
                    response.body()

                val menu =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    menu != null
                ) {
                    _uiState.value =
                        StorefrontMenuUiState(
                            isLoading = false,
                            menu = menu,
                            errorMessage = null
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response
                                .errorBody()
                                ?.string()
                        ) ?: responseBody
                            ?.message
                            ?.takeIf {
                                it.isNotBlank()
                            }
                        ?: "İşletme menüsü alınamadı."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "İşletme menüsü yüklenirken bir hata oluştu."
                )
            }
        }
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            StorefrontMenuUiState(
                isLoading = false,
                menu = null,
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