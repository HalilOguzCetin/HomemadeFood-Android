package com.homemadefood.app.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.DeliveryAddressSelectionManager
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.remote.ApiErrorParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class AddressesViewModel(
    private val addressRepository: AddressRepository,
    private val sessionManager: SessionManager,
    private val deliveryAddressSelectionManager:
    DeliveryAddressSelectionManager
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
                        /*
                         * C4D:
                         * Addresses ekranına doğrudan Account'tan
                         * gelinmiş olsa bile stale selected id
                         * temizlenir / fallback çözülür.
                         */
                        deliveryAddressSelectionManager
                            .resolve(
                                addresses
                            )

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
                    /*
                     * Backend default adresi yeniden atayabilir.
                     * Bu yüzden yalnız local listeden silmek yerine
                     * server listesini tekrar çekiyoruz.
                     *
                     * Böylece:
                     * - aktif adres silindiyse yeni default/ilk adres
                     *   anında selectedDeliveryAddress olur,
                     * - son adres silindiyse selection temizlenir,
                     * - yeni backend IsDefault bilgisi UI'a gelir.
                     */
                    refreshAfterDelete(
                        deletedAddressId =
                            addressId,

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

    private suspend fun refreshAfterDelete(
        deletedAddressId: Int,
        actionMessage: String?
    ) {
        val localRemainingAddresses =
            _uiState.value
                .addresses
                .filterNot {
                    it.id ==
                            deletedAddressId
                }

        try {
            val refreshResponse =
                addressRepository
                    .getAddresses()

            val refreshBody =
                refreshResponse.body()

            val serverAddresses =
                refreshBody?.data

            if (
                refreshResponse.isSuccessful &&
                refreshBody?.success == true &&
                serverAddresses != null
            ) {
                deliveryAddressSelectionManager
                    .resolve(
                        serverAddresses
                    )

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        addresses =
                            serverAddresses,
                        deletingAddressId =
                            null,
                        errorMessage = null,
                        actionMessage =
                            actionMessage
                    )

                return
            }

            /*
             * Delete başarılı, yalnız refresh başarısız.
             * Kullanıcıya silme işlemini geri alınmış gibi
             * göstermeyelim. Local listeyi koruyup selection'ı
             * güvenli biçimde reconcile ederiz.
             */
            deliveryAddressSelectionManager
                .resolve(
                    localRemainingAddresses
                )

            _uiState.value =
                _uiState.value.copy(
                    addresses =
                        localRemainingAddresses,

                    deletingAddressId =
                        null,

                    errorMessage =
                        "Adres silindi ancak güncel adres listesi alınamadı. Ana sayfaya döndüğünüzde tekrar yenilenecek.",

                    actionMessage =
                        actionMessage
                )
        } catch (_: Exception) {
            deliveryAddressSelectionManager
                .resolve(
                    localRemainingAddresses
                )

            _uiState.value =
                _uiState.value.copy(
                    addresses =
                        localRemainingAddresses,

                    deletingAddressId =
                        null,

                    errorMessage =
                        "Adres silindi ancak güncel adres listesi alınamadı. Ana sayfaya döndüğünüzde tekrar yenilenecek.",

                    actionMessage =
                        actionMessage
                )
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
        return ApiErrorParser
            .parse(
                errorJson
            )
            .message
    }
}