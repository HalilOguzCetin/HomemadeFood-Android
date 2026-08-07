package com.homemadefood.app.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.UpdateAddressRequest
import com.homemadefood.app.data.repository.AddressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class EditAddressViewModel(
    private val addressId: Int,
    private val addressRepository: AddressRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadAddressJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            EditAddressUiState()
        )

    val uiState: StateFlow<EditAddressUiState> =
        _uiState.asStateFlow()

    init {
        loadAddress()
    }

    fun loadAddress() {
        loadAddressJob?.cancel()

        loadAddressJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
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
                        addressRepository.getAddresses()

                    val responseBody =
                        response.body()

                    val addresses =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        addresses != null
                    ) {
                        val address =
                            addresses.firstOrNull {
                                it.id == addressId
                            }

                        if (address == null) {
                            _uiState.value =
                                _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage =
                                        "Düzenlenecek adres bulunamadı."
                                )

                            return@launch
                        }

                        _uiState.value =
                            EditAddressUiState(
                                isLoading = false,
                                title = address.title,
                                fullAddress =
                                    address.fullAddress,
                                latitude =
                                    address.latitude.toString(),
                                longitude =
                                    address.longitude.toString(),
                                isDefault =
                                    address.isDefault,
                                isSaving = false,
                                isSaved = false,
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
                                    ) ?: "Adres bilgisi alınamadı."
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
                                "Adres yüklenirken bir hata oluştu."
                        )
                }
            }
    }

    fun updateTitle(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                title = value,
                errorMessage = null
            )
    }

    fun updateFullAddress(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                fullAddress = value,
                errorMessage = null
            )
    }

    fun updateLatitude(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                latitude = value,
                errorMessage = null
            )
    }

    fun updateLongitude(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                longitude = value,
                errorMessage = null
            )
    }

    fun updateIsDefault(
        value: Boolean
    ) {
        _uiState.value =
            _uiState.value.copy(
                isDefault = value,
                errorMessage = null
            )
    }

    fun updateAddress() {
        if (
            _uiState.value.isLoading ||
            _uiState.value.isSaving
        ) {
            return
        }

        val title =
            _uiState.value.title.trim()

        val fullAddress =
            _uiState.value.fullAddress.trim()

        val latitude =
            _uiState.value.latitude
                .trim()
                .replace(",", ".")
                .toDoubleOrNull()

        val longitude =
            _uiState.value.longitude
                .trim()
                .replace(",", ".")
                .toDoubleOrNull()

        when {
            title.isBlank() -> {
                showError(
                    "Adres başlığı boş bırakılamaz."
                )

                return
            }

            fullAddress.isBlank() -> {
                showError(
                    "Açık adres boş bırakılamaz."
                )

                return
            }

            latitude == null -> {
                showError(
                    "Geçerli bir enlem değeri girin."
                )

                return
            }

            longitude == null -> {
                showError(
                    "Geçerli bir boylam değeri girin."
                )

                return
            }

            latitude !in -90.0..90.0 -> {
                showError(
                    "Enlem -90 ile 90 arasında olmalıdır."
                )

                return
            }

            longitude !in -180.0..180.0 -> {
                showError(
                    "Boylam -180 ile 180 arasında olmalıdır."
                )

                return
            }
        }

        viewModelScope.launch {
            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                showError(
                    "Oturum bilgisi bulunamadı. Yeniden giriş yapın."
                )

                return@launch
            }

            _uiState.value =
                _uiState.value.copy(
                    isSaving = true,
                    isSaved = false,
                    errorMessage = null
                )

            try {
                val request =
                    UpdateAddressRequest(
                        title = title,
                        fullAddress = fullAddress,
                        latitude = latitude,
                        longitude = longitude,
                        isDefault =
                            _uiState.value.isDefault
                    )

                val response =
                    addressRepository.updateAddress(
                        addressId = addressId,
                        request = request
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
                            isSaving = false,
                            isSaved = true,
                            errorMessage = null
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Adres güncellenemedi."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "Adres güncellenirken bir hata oluştu."
                )
            }
        }
    }

    fun resetSavedState() {
        _uiState.value =
            _uiState.value.copy(
                isSaved = false
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                isSaving = false,
                isSaved = false,
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