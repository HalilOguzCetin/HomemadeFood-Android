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
    private val addressRepository:
    AddressRepository,

    private val sessionManager:
    SessionManager,

    private val reverseGeocoder:
    AddressReverseGeocoder
) : ViewModel() {

    private var reverseGeocodeJob:
            Job? = null

    private val _uiState =
        MutableStateFlow(
            AddressFormUiState()
        )

    val uiState:
            StateFlow<AddressFormUiState> =
        _uiState.asStateFlow()

    fun updateTitle(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                title =
                    value.take(60),
                errorMessage = null
            )
    }

    fun updateCity(
        value: String
    ) {
        updateAddressField {
            copy(
                city = value.take(100)
            )
        }
    }

    fun updateDistrict(
        value: String
    ) {
        updateAddressField {
            copy(
                district = value.take(100)
            )
        }
    }

    fun updateNeighborhood(
        value: String
    ) {
        updateAddressField {
            copy(
                neighborhood =
                    value.take(120)
            )
        }
    }

    fun updateStreet(
        value: String
    ) {
        updateAddressField {
            copy(
                street = value.take(150)
            )
        }
    }

    fun updateBuildingNo(
        value: String
    ) {
        updateAddressField {
            copy(
                buildingNo =
                    value.take(30)
            )
        }
    }

    fun updateFloor(
        value: String
    ) {
        updateAddressField {
            copy(
                floor = value.take(20)
            )
        }
    }

    fun updateApartmentNo(
        value: String
    ) {
        updateAddressField {
            copy(
                apartmentNo =
                    value.take(20)
            )
        }
    }

    fun updateAddressNote(
        value: String
    ) {
        updateAddressField {
            copy(
                addressNote =
                    value.take(300)
            )
        }
    }

    fun updateSelectedLocation(
        latitude: Double,
        longitude: Double
    ) {
        val location =
            SelectedLocation(
                latitude = latitude,
                longitude = longitude
            )

        if (!location.isValid()) {
            showError(
                "Seçilen konum geçerli değil."
            )
            return
        }

        reverseGeocodeJob?.cancel()

        /*
         * Konum değiştiğinde konuma bağlı eski
         * adres parçalarını temizliyoruz.
         * Kat, daire, not ve adres başlığı korunur.
         */
        _uiState.value =
            _uiState.value.copy(
                selectedLocation =
                    location,

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

                val result =
                    reverseGeocoder
                        .reverseGeocode(
                            latitude =
                                location.latitude,

                            longitude =
                                location.longitude
                        )

                /*
                 * Kullanıcı bu işlem sürerken
                 * başka bir konum seçtiyse eski
                 * geocoding sonucu state'i bozmasın.
                 */
                if (
                    _uiState.value
                        .selectedLocation !=
                    location
                ) {
                    return@launch
                }

                result
                    .onSuccess {
                            address ->

                        val requiredFieldsComplete =
                            address.city
                                .isNotBlank() &&
                                    address.district
                                        .isNotBlank() &&
                                    address.neighborhood
                                        .isNotBlank() &&
                                    address.street
                                        .isNotBlank() &&
                                    address.buildingNo
                                        .isNotBlank()

                        _uiState.value =
                            _uiState.value.copy(
                                city =
                                    address.city,

                                district =
                                    address.district,

                                neighborhood =
                                    address
                                        .neighborhood,

                                street =
                                    address.street,

                                buildingNo =
                                    address
                                        .buildingNo,

                                isResolvingAddress =
                                    false,

                                locationLookupMessage =
                                    if (
                                        requiredFieldsComplete
                                    ) {
                                        "Adres bilgileri konumdan otomatik dolduruldu. Kaydetmeden önce kontrol edin."
                                    } else {
                                        "Konum bulundu. Bazı adres alanları otomatik bulunamadı; eksik alanları tamamlayın."
                                    },

                                errorMessage = null
                            )
                    }
                    .onFailure {
                        _uiState.value =
                            _uiState.value.copy(
                                isResolvingAddress =
                                    false,

                                locationLookupMessage =
                                    "Konum seçildi ancak adres bilgileri otomatik bulunamadı. Alanları elle doldurabilirsiniz.",

                                errorMessage = null
                            )
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

    fun updateIsDefault(
        value: Boolean
    ) {
        _uiState.value =
            _uiState.value.copy(
                isDefault = value,
                errorMessage = null
            )
    }

    fun saveAddress() {
        val currentState =
            _uiState.value

        if (
            currentState.isSaving ||
            currentState.isResolvingAddress
        ) {
            return
        }

        val title =
            currentState.title.trim()

        val selectedLocation =
            currentState.selectedLocation

        when {
            selectedLocation == null -> {
                showError(
                    "Önce haritadan teslimat konumunu seçmelisiniz."
                )
                return
            }

            !selectedLocation.isValid() -> {
                showError(
                    "Seçilen konum geçerli değil."
                )
                return
            }

            title.isBlank() -> {
                showError(
                    "Adres başlığı boş bırakılamaz."
                )
                return
            }

            currentState.city
                .isBlank() -> {
                showError(
                    "İl alanı boş bırakılamaz."
                )
                return
            }

            currentState.district
                .isBlank() -> {
                showError(
                    "İlçe alanı boş bırakılamaz."
                )
                return
            }

            currentState.neighborhood
                .isBlank() -> {
                showError(
                    "Mahalle alanı boş bırakılamaz."
                )
                return
            }

            currentState.street
                .isBlank() -> {
                showError(
                    "Cadde / sokak alanı boş bırakılamaz."
                )
                return
            }

            currentState.buildingNo
                .isBlank() -> {
                showError(
                    "Bina numarası boş bırakılamaz."
                )
                return
            }
        }

        val fullAddress =
            currentState
                .buildFullAddress()
                .trim()

        if (fullAddress.isBlank()) {
            showError(
                "Adres bilgileri oluşturulamadı."
            )
            return
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
                /*
                 * Aşama 12'ye kadar backend DTO'sunu
                 * değiştirmiyoruz. Yapılandırılmış
                 * alanları okunabilir FullAddress
                 * metnine çevirip mevcut endpoint'e
                 * göndermeye devam ediyoruz.
                 */
                val request =
                    CreateAddressRequest(
                        title = title,

                        fullAddress =
                            fullAddress,

                        latitude =
                            selectedLocation
                                .latitude,

                        longitude =
                            selectedLocation
                                .longitude,

                        isDefault =
                            currentState
                                .isDefault
                    )

                val response =
                    addressRepository
                        .createAddress(
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
            _uiState.value.copy(
                isSaved = false
            )
    }

    private fun updateAddressField(
        transform:
        AddressFormUiState.() ->
        AddressFormUiState
    ) {
        _uiState.value =
            _uiState.value
                .transform()
                .copy(
                    errorMessage = null
                )
    }

    private fun showError(
        message: String
    ) {
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
                .takeIf {
                    it.isNotBlank()
                }
        }.getOrNull()
    }
}