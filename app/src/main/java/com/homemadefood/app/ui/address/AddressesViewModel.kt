package com.homemadefood.app.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AddressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class AddressesViewModel(
    private val addressRepository: AddressRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadAddressesJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            AddressesUiState(
                isLoading = true
            )
        )

    val uiState: StateFlow<AddressesUiState> =
        _uiState.asStateFlow()



    fun loadAddresses() {
        loadAddressesJob?.cancel()

        loadAddressesJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null,
                        actionMessage = null
                    )

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    _uiState.value =
                        AddressesUiState(
                            isLoading = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        addressRepository
                            .getAddresses()

                    val responseBody =
                        response.body()

                    val addresses =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        addresses != null
                    ) {
                        _uiState.value =
                            AddressesUiState(
                                isLoading = false,
                                addresses = addresses,
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
                                    ) ?: "Adresler alınamadı."
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
                                "Adresler yüklenirken bir hata oluştu."
                        )
                }
            }
    }

    fun deleteAddress(
        addressId: Int
    ) {
        if (_uiState.value.deletingAddressId != null) {
            return
        }

        viewModelScope.launch {
            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                _uiState.value =
                    _uiState.value.copy(
                        errorMessage =
                            "Oturum bilgisi bulunamadı."
                    )

                return@launch
            }

            _uiState.value =
                _uiState.value.copy(
                    deletingAddressId = addressId,
                    errorMessage = null,
                    actionMessage = null
                )

            try {
                val response =
                    addressRepository.deleteAddress(
                        addressId = addressId
                    )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            addresses =
                                _uiState.value.addresses
                                    .filterNot {
                                        it.id == addressId
                                    },

                            deletingAddressId = null,

                            actionMessage =
                                responseBody.message
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            deletingAddressId = null,

                            errorMessage =
                                parseErrorMessage(
                                    response.errorBody()
                                        ?.string()
                                ) ?: "Adres silinemedi."
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    _uiState.value.copy(
                        deletingAddressId = null,
                        errorMessage =
                            "Sunucuya bağlanılamadı."
                    )
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        deletingAddressId = null,
                        errorMessage =
                            "Adres silinirken bir hata oluştu."
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