package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.ProducerApplicationRequest
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.ProducerRepository
import com.homemadefood.app.ui.address.SelectedLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CustomerProducerApplicationViewModel(
    private val producerRepository:
    ProducerRepository,

    private val addressRepository:
    AddressRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadApplicationJob: Job? =
        null

    private var submitApplicationJob: Job? =
        null

    private var reverseGeocodeJob: Job? =
        null

    private val _uiState =
        MutableStateFlow(
            CustomerProducerApplicationUiState()
        )

    val uiState:
            StateFlow<
                    CustomerProducerApplicationUiState
                    > =
        _uiState.asStateFlow()

    fun loadApplication() {
        loadApplicationJob?.cancel()

        loadApplicationJob =
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
                    showError(
                        "Oturum bilgisi bulunamadı."
                    )
                    return@launch
                }

                try {
                    val response =
                        producerRepository
                            .getMyApplication()

                    val body =
                        response.body()

                    when {
                        response.isSuccessful &&
                                body?.success == true &&
                                body.data != null -> {

                            _uiState.value =
                                _uiState.value.copy(
                                    isLoading = false,
                                    application = body.data,
                                    isFormVisible = false,
                                    errorMessage = null
                                )
                        }

                        response.code() == 404 -> {
                            /*
                             * Yeni başvuru yapan kullanıcıda mevcut
                             * form state'ini yok etme.
                             *
                             * Özellikle haritadan geri dönüşte seçilen
                             * konum ve doldurulmuş form alanları korunur.
                             */
                            _uiState.value =
                                _uiState.value.copy(
                                    isLoading = false,
                                    application = null,
                                    isFormVisible = true,
                                    errorMessage = null
                                )
                        }

                        else -> {
                            showError(
                                parseErrorMessage(
                                    response
                                        .errorBody()
                                        ?.string()
                                )
                                    ?: "Üretici başvurusu alınamadı."
                            )
                        }
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Başvuru bilgisi yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun updateBusinessName(
        value: String
    ) {
        if (value.length > 150) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                businessName = value,
                errorMessage = null
            )
    }

    fun updateDescription(
        value: String
    ) {
        if (value.length > 1000) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                description = value,
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
            copy(
                neighborhood =
                    value.take(120)
            )
        }

    fun updateStreet(value: String) =
        updateAddressField {
            copy(street = value.take(150))
        }

    fun updateBuildingNo(value: String) =
        updateAddressField {
            copy(
                buildingNo =
                    value.take(30)
            )
        }

    fun updateFloor(value: String) =
        updateAddressField {
            copy(floor = value.take(20))
        }

    fun updateApartmentNo(value: String) =
        updateAddressField {
            copy(
                apartmentNo =
                    value.take(20)
            )
        }

    fun updateAddressNote(value: String) =
        updateAddressField {
            copy(
                addressNote =
                    value.take(300)
            )
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
                "Seçilen işletme konumu geçerli değil."
            )
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
                    "İşletme konumundan adres bilgileri bulunuyor...",

                errorMessage = null
            )

        reverseGeocodeJob =
            viewModelScope.launch {
                try {
                    val response =
                        addressRepository
                            .reverseGeocode(
                                latitude =
                                    location.latitude,

                                longitude =
                                    location.longitude
                            )

                    if (
                        _uiState.value
                            .selectedLocation !=
                        location
                    ) {
                        return@launch
                    }

                    val body =
                        response.body()

                    val address =
                        body?.data

                    if (
                        response.isSuccessful &&
                        body?.success == true &&
                        address != null
                    ) {
                        val complete =
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
                                    address.neighborhood,

                                street =
                                    address.street,

                                buildingNo =
                                    address.buildingNo,

                                isResolvingAddress =
                                    false,

                                locationLookupMessage =
                                    if (complete) {
                                        "İşletme adresi otomatik dolduruldu. Başvurmadan önce kontrol edin."
                                    } else {
                                        "Konum bulundu. Otomatik bulunamayan adres alanlarını tamamlayın."
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
                                    response
                                        .errorBody()
                                        ?.string()
                                )
                                    ?: "Konum seçildi ancak adres bilgileri otomatik bulunamadı. Alanları elle doldurabilirsiniz.",

                            errorMessage = null
                        )
                } catch (_: IOException) {
                    if (
                        _uiState.value
                            .selectedLocation ==
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
                        _uiState.value
                            .selectedLocation ==
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

    fun updateDailyCapacityText(
        value: String
    ) {
        if (
            value.isNotEmpty() &&
            value.any { !it.isDigit() }
        ) {
            return
        }

        if (value.length > 4) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                dailyCapacityText = value,
                errorMessage = null
            )
    }

    fun showReapplicationForm() {
        val application =
            _uiState.value.application
                ?: return

        if (!_uiState.value.isRejected) {
            return
        }

        val storedLocation =
            SelectedLocation(
                latitude = application.latitude,
                longitude = application.longitude
            ).takeIf {
                it.isValid()
            }

        _uiState.value =
            _uiState.value.copy(
                isFormVisible = true,

                businessName =
                    application.businessName,

                description =
                    application.description,

                city =
                    application.city,

                district =
                    application.district,

                neighborhood =
                    application.neighborhood,

                street =
                    application.street,

                buildingNo =
                    application.buildingNo,

                floor =
                    application.floor.orEmpty(),

                apartmentNo =
                    application.apartmentNo.orEmpty(),

                addressNote =
                    application.addressNote.orEmpty(),

                selectedLocation =
                    storedLocation,

                dailyCapacityText =
                    application.dailyCapacity
                        .toString(),

                isResolvingAddress = false,
                locationLookupMessage = null,

                errorMessage = null,
                successMessage = null
            )

        val structuredAddressMissing =
            application.city.isBlank() ||
                    application.district.isBlank() ||
                    application.neighborhood.isBlank() ||
                    application.street.isBlank() ||
                    application.buildingNo.isBlank()

        /*
         * Migration öncesi eski rejected başvurularda
         * structured kolonlar boş olabilir.
         * Kayıtlı koordinat varsa otomatik tamamlamayı deneriz.
         */
        if (
            structuredAddressMissing &&
            storedLocation != null
        ) {
            updateSelectedLocation(
                latitude =
                    storedLocation.latitude,

                longitude =
                    storedLocation.longitude
            )
        }
    }

    fun hideReapplicationForm() {
        if (_uiState.value.application == null) {
            return
        }

        reverseGeocodeJob?.cancel()

        _uiState.value =
            _uiState.value.copy(
                isFormVisible = false,
                isResolvingAddress = false,
                locationLookupMessage = null,
                errorMessage = null
            )
    }

    fun submitApplication() {
        val current =
            _uiState.value

        if (
            current.isSubmitting ||
            current.isLoading ||
            current.isResolvingAddress
        ) {
            return
        }

        val businessName =
            current.businessName.trim()

        val description =
            current.description.trim()

        val location =
            current.selectedLocation

        val dailyCapacity =
            current.dailyCapacityText
                .toIntOrNull()

        when {
            businessName.length !in 2..150 -> {
                showError(
                    "İşletme adı 2 ile 150 karakter arasında olmalıdır."
                )
                return
            }

            description.length !in 10..1000 -> {
                showError(
                    "İşletme açıklaması 10 ile 1000 karakter arasında olmalıdır."
                )
                return
            }

            location == null ||
                    !location.isValid() -> {
                showError(
                    "Önce haritadan işletme konumunu seçmelisiniz."
                )
                return
            }

            current.city.isBlank() -> {
                showError(
                    "İl alanı boş bırakılamaz."
                )
                return
            }

            current.district.isBlank() -> {
                showError(
                    "İlçe alanı boş bırakılamaz."
                )
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

            dailyCapacity == null ||
                    dailyCapacity !in 1..1000 -> {
                showError(
                    "Günlük kapasite 1 ile 1000 arasında olmalıdır."
                )
                return
            }
        }

        val address =
            current
                .buildFullAddress()
                .trim()

        if (address.length !in 10..500) {
            showError(
                "İşletme adresi 10 ile 500 karakter arasında olmalıdır."
            )
            return
        }

        submitApplicationJob?.cancel()

        submitApplicationJob =
            viewModelScope.launch {
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

                _uiState.value =
                    _uiState.value.copy(
                        isSubmitting = true,
                        errorMessage = null,
                        successMessage = null
                    )

                try {
                    val response =
                        producerRepository.apply(
                            request =
                                ProducerApplicationRequest(
                                    businessName =
                                        businessName,

                                    description =
                                        description,

                                    address =
                                        address,

                                    city =
                                        current.city.trim(),

                                    district =
                                        current.district.trim(),

                                    neighborhood =
                                        current
                                            .neighborhood
                                            .trim(),

                                    street =
                                        current.street.trim(),

                                    buildingNo =
                                        current
                                            .buildingNo
                                            .trim(),

                                    floor =
                                        current.floor
                                            .trim()
                                            .takeIf {
                                                it.isNotBlank()
                                            },

                                    apartmentNo =
                                        current.apartmentNo
                                            .trim()
                                            .takeIf {
                                                it.isNotBlank()
                                            },

                                    addressNote =
                                        current.addressNote
                                            .trim()
                                            .takeIf {
                                                it.isNotBlank()
                                            },

                                    latitude =
                                        location.latitude,

                                    longitude =
                                        location.longitude,

                                    dailyCapacity =
                                        dailyCapacity
                                )
                        )

                    val body =
                        response.body()

                    if (
                        response.isSuccessful &&
                        body?.success == true &&
                        body.data != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isSubmitting = false,
                                isFormVisible = false,

                                successMessage =
                                    body.message
                                        .ifBlank {
                                            "Üretici başvurusu başarıyla gönderildi."
                                        },

                                errorMessage = null
                            )

                        loadApplication()
                    } else {
                        showError(
                            parseErrorMessage(
                                response
                                    .errorBody()
                                    ?.string()
                            )
                                ?: "Üretici başvurusu gönderilemedi."
                        )
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Başvuru gönderilirken bir hata oluştu."
                    )
                }
            }
    }

    fun clearMessages() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = null,
                successMessage = null
            )
    }

    private fun updateAddressField(
        transform:
        CustomerProducerApplicationUiState.() ->
        CustomerProducerApplicationUiState
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
                isLoading = false,
                isSubmitting = false,
                isResolvingAddress = false,
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