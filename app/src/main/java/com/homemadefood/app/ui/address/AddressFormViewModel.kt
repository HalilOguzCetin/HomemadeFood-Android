package com.homemadefood.app.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.CreateAddressRequest
import com.homemadefood.app.data.repository.AddressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class AddressFormViewModel(
    private val addressRepository: AddressRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var reverseGeocodeJob: Job? = null

    private val _uiState =
        MutableStateFlow(AddressFormUiState())

    val uiState: StateFlow<AddressFormUiState> =
        _uiState.asStateFlow()

    fun updateTitle(value: String) {
        _uiState.value =
            _uiState.value.copy(
                title = value.take(60),
                errorMessage = null
            )
    }

    fun updateCity(value: String) =
        updateAddressField {
            copy(city = value.take(100))
        }

    fun updateDistrict(value: String) =
        updateAddressField {
            copy(district = value.take(100))
        }

    fun updateNeighborhood(value: String) =
        updateAddressField {
            copy(neighborhood = value.take(120))
        }

    fun updateStreet(value: String) =
        updateAddressField {
            copy(street = value.take(150))
        }

    fun updateBuildingNo(value: String) =
        updateAddressField {
            copy(buildingNo = value.take(30))
        }

    fun updateFloor(value: String) =
        updateAddressField {
            copy(floor = value.take(20))
        }

    fun updateApartmentNo(value: String) =
        updateAddressField {
            copy(apartmentNo = value.take(20))
        }

    fun updateAddressNote(value: String) =
        updateAddressField {
            copy(addressNote = value.take(300))
        }

    fun updateSelectedLocation(
        latitude: Double,
        longitude: Double
    ) {
        val location =
            SelectedLocation(latitude, longitude)

        if (!location.isValid()) {
            showError("Seçilen konum geçerli değil.")
            return
        }

        reverseGeocodeJob?.cancel()

        _uiState.value =
            _uiState.value.copy(
                selectedLocation = location,
                city = "",
                district = "",
                neighborhood = "",
                street = "",
                buildingNo = "",
                isResolvingAddress = true,
                locationLookupMessage =
                    "Konumdan adres bilgileri bulunuyor...",
                errorMessage = null
            )

        reverseGeocodeJob =
            viewModelScope.launch {
                try {
                    val response =
                        addressRepository.reverseGeocode(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )

                    if (
                        _uiState.value.selectedLocation !=
                        location
                    ) {
                        return@launch
                    }

                    val body = response.body()
                    val address = body?.data

                    if (
                        response.isSuccessful &&
                        body?.success == true &&
                        address != null
                    ) {
                        val complete =
                            address.city.isNotBlank() &&
                                    address.district.isNotBlank() &&
                                    address.neighborhood.isNotBlank() &&
                                    address.street.isNotBlank() &&
                                    address.buildingNo.isNotBlank()

                        _uiState.value =
                            _uiState.value.copy(
                                city = address.city,
                                district = address.district,
                                neighborhood =
                                    address.neighborhood,
                                street = address.street,
                                buildingNo =
                                    address.buildingNo,
                                isResolvingAddress = false,
                                locationLookupMessage =
                                    if (complete) {
                                        "Adres bilgileri Google konum servisinden otomatik dolduruldu. Kaydetmeden önce kontrol edin."
                                    } else {
                                        "Konum bulundu. Bazı adres alanları otomatik bulunamadı; eksik alanları tamamlayın."
                                    },
                                errorMessage = null
                            )
                        return@launch
                    }

                    _uiState.value =
                        _uiState.value.copy(
                            isResolvingAddress = false,
                            locationLookupMessage =
                                parseErrorMessage(
                                    response.errorBody()
                                        ?.string()
                                ) ?: "Konum seçildi ancak adres bilgileri otomatik bulunamadı. Alanları elle doldurabilirsiniz.",
                            errorMessage = null
                        )
                } catch (_: IOException) {
                    if (
                        _uiState.value.selectedLocation ==
                        location
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isResolvingAddress = false,
                                locationLookupMessage =
                                    "Adres servisine bağlanılamadı. Alanları elle doldurabilirsiniz.",
                                errorMessage = null
                            )
                    }
                } catch (_: Exception) {
                    if (
                        _uiState.value.selectedLocation ==
                        location
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isResolvingAddress = false,
                                locationLookupMessage =
                                    "Adres bilgileri alınırken bir hata oluştu. Alanları elle doldurabilirsiniz.",
                                errorMessage = null
                            )
                    }
                }
            }
    }

    fun clearSelectedLocation() {
        reverseGeocodeJob?.cancel()
        _uiState.value =
            _uiState.value.copy(
                selectedLocation = null,
                city = "",
                district = "",
                neighborhood = "",
                street = "",
                buildingNo = "",
                isResolvingAddress = false,
                locationLookupMessage = null,
                errorMessage = null
            )
    }

    fun updateIsDefault(value: Boolean) {
        _uiState.value =
            _uiState.value.copy(
                isDefault = value,
                errorMessage = null
            )
    }

    fun saveAddress() {
        val current = _uiState.value

        if (
            current.isSaving ||
            current.isResolvingAddress
        ) {
            return
        }

        val title = current.title.trim()
        val location = current.selectedLocation

        when {
            location == null -> {
                showError(
                    "Önce haritadan teslimat konumunu seçmelisiniz."
                )
                return
            }
            !location.isValid() -> {
                showError("Seçilen konum geçerli değil.")
                return
            }
            title.isBlank() -> {
                showError(
                    "Adres başlığı boş bırakılamaz."
                )
                return
            }
            current.city.isBlank() -> {
                showError("İl alanı boş bırakılamaz.")
                return
            }
            current.district.isBlank() -> {
                showError("İlçe alanı boş bırakılamaz.")
                return
            }
            current.neighborhood.isBlank() -> {
                showError(
                    "Mahalle alanı boş bırakılamaz."
                )
                return
            }
            current.street.isBlank() -> {
                showError(
                    "Cadde / sokak alanı boş bırakılamaz."
                )
                return
            }
            current.buildingNo.isBlank() -> {
                showError(
                    "Bina numarası boş bırakılamaz."
                )
                return
            }
        }

        val fullAddress =
            current.buildFullAddress().trim()

        viewModelScope.launch {
            val isLoggedIn =
                sessionManager.isLoggedIn.first()

            if (!isLoggedIn) {
                showError(
                    "Oturum bilgisi bulunamadı. Yeniden giriş yapın."
                )
                return@launch
            }

            _uiState.value =
                current.copy(
                    isSaving = true,
                    isSaved = false,
                    errorMessage = null
                )

            try {
                val request =
                    CreateAddressRequest(
                        title = title,
                        fullAddress = fullAddress,
                        city = current.city.trim(),
                        district = current.district.trim(),
                        neighborhood =
                            current.neighborhood.trim(),
                        street = current.street.trim(),
                        buildingNo =
                            current.buildingNo.trim(),
                        floor =
                            current.floor.trim()
                                .takeIf { it.isNotBlank() },
                        apartmentNo =
                            current.apartmentNo.trim()
                                .takeIf { it.isNotBlank() },
                        addressNote =
                            current.addressNote.trim()
                                .takeIf { it.isNotBlank() },
                        latitude = location.latitude,
                        longitude = location.longitude,
                        isDefault = current.isDefault
                    )

                val response =
                    addressRepository.createAddress(
                        request = request
                    )

                val body = response.body()

                if (
                    response.isSuccessful &&
                    body?.success == true &&
                    body.data != null
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
                        ) ?: "Adres kaydedilemedi."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "Adres kaydedilirken bir hata oluştu."
                )
            }
        }
    }

    fun resetSavedState() {
        _uiState.value =
            _uiState.value.copy(isSaved = false)
    }

    private fun updateAddressField(
        transform:
        AddressFormUiState.() ->
        AddressFormUiState
    ) {
        _uiState.value =
            _uiState.value
                .transform()
                .copy(errorMessage = null)
    }

    private fun showError(message: String) {
        _uiState.value =
            _uiState.value.copy(
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
                .takeIf { it.isNotBlank() }
        }.getOrNull()
    }
}