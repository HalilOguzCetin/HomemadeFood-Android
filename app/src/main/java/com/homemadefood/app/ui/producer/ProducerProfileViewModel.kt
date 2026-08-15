package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.ProducerRepository
import com.homemadefood.app.data.upload.ProducerBusinessImageMultipartFactory
import com.homemadefood.app.ui.address.SelectedLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class ProducerProfileViewModel(
    private val producerRepository:
    ProducerRepository,

    private val addressRepository:
    AddressRepository,

    private val sessionManager:
    SessionManager,

    private val producerBusinessImageMultipartFactory:
    ProducerBusinessImageMultipartFactory
) : ViewModel() {

    private var loadProfileJob: Job? = null
    private var saveProfileJob: Job? = null
    private var reverseGeocodeJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            ProducerProfileUiState()
        )

    val uiState:
            StateFlow<ProducerProfileUiState> =
        _uiState.asStateFlow()

    fun loadProfile() {
        loadProfileJob?.cancel()

        loadProfileJob =
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
                    showLoadError(
                        "Oturum bilgisi bulunamadı."
                    )
                    return@launch
                }

                try {
                    val response =
                        producerRepository
                            .getMyProfile()

                    val responseBody =
                        response.body()

                    val profile =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        profile != null
                    ) {
                        applyLoadedProfile(
                            profile = profile
                        )
                    } else {
                        showLoadError(
                            parseErrorMessage(
                                response
                                    .errorBody()
                                    ?.string()
                            )
                                ?: "Üretici profili alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showLoadError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showLoadError(
                        "Üretici profili yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun startEditing() {
        val profile =
            _uiState.value.profile

        if (profile == null) {
            showActionError(
                "Üretici profil bilgisi bulunamadı."
            )
            return
        }

        if (!_uiState.value.canEdit) {
            showActionError(
                "Yalnızca onaylanmış üretici profilleri düzenlenebilir."
            )
            return
        }

        val location =
            profileLocation(
                profile
            )

        _uiState.value =
            _uiState.value.copy(
                isEditing = true,

                businessName =
                    profile.businessName,

                description =
                    profile.description,

                selectedBusinessImageUri =
                    null,

                city =
                    profile.city,

                district =
                    profile.district,

                neighborhood =
                    profile.neighborhood,

                street =
                    profile.street,

                buildingNo =
                    profile.buildingNo,

                floor =
                    profile.floor.orEmpty(),

                apartmentNo =
                    profile.apartmentNo.orEmpty(),

                addressNote =
                    profile.addressNote.orEmpty(),

                selectedLocation =
                    location,

                dailyCapacityText =
                    profile.dailyCapacity
                        .toString(),

                isAvailable =
                    profile.isAvailable,

                isResolvingAddress = false,
                locationLookupMessage = null,

                errorMessage = null,
                successMessage = null
            )

        val structuredAddressMissing =
            profile.city.isBlank() ||
                    profile.district.isBlank() ||
                    profile.neighborhood.isBlank() ||
                    profile.street.isBlank() ||
                    profile.buildingNo.isBlank()

        /*
         * Migration öncesi eski profillerde structured
         * kolonlar boş olabilir. Kayıtlı koordinat varsa
         * reverse geocoding ile tamamlamayı deneriz.
         */
        if (
            structuredAddressMissing &&
            location != null
        ) {
            updateSelectedLocation(
                latitude =
                    location.latitude,

                longitude =
                    location.longitude
            )
        }
    }

    fun cancelEditing() {
        if (_uiState.value.isSaving) {
            return
        }

        reverseGeocodeJob?.cancel()

        val profile =
            _uiState.value.profile

        if (profile == null) {
            _uiState.value =
                _uiState.value.copy(
                    isEditing = false,
                    isResolvingAddress = false,
                    locationLookupMessage = null,
                    errorMessage = null
                )
            return
        }

        _uiState.value =
            _uiState.value.copy(
                isEditing = false,

                businessName =
                    profile.businessName,

                description =
                    profile.description,

                selectedBusinessImageUri =
                    null,

                city =
                    profile.city,

                district =
                    profile.district,

                neighborhood =
                    profile.neighborhood,

                street =
                    profile.street,

                buildingNo =
                    profile.buildingNo,

                floor =
                    profile.floor.orEmpty(),

                apartmentNo =
                    profile.apartmentNo.orEmpty(),

                addressNote =
                    profile.addressNote.orEmpty(),

                selectedLocation =
                    profileLocation(profile),

                dailyCapacityText =
                    profile.dailyCapacity
                        .toString(),

                isAvailable =
                    profile.isAvailable,

                isResolvingAddress = false,
                locationLookupMessage = null,

                errorMessage = null
            )
    }

    fun onBusinessImageSelected(
        uri: String
    ) {
        if (
            _uiState.value.isSaving ||
            uri.isBlank()
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedBusinessImageUri =
                    uri,

                errorMessage = null,
                successMessage = null
            )
    }

    fun onRemoveSelectedBusinessImage() {
        if (_uiState.value.isSaving) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedBusinessImageUri =
                    null,

                errorMessage = null,
                successMessage = null
            )
    }

    fun updateBusinessName(
        value: String
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        if (value.length <= 150) {
            _uiState.value =
                _uiState.value.copy(
                    businessName = value,
                    errorMessage = null
                )
        }
    }

    fun updateDescription(
        value: String
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        if (value.length <= 1000) {
            _uiState.value =
                _uiState.value.copy(
                    description = value,
                    errorMessage = null
                )
        }
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
        if (
            !_uiState.value.isEditing ||
            _uiState.value.isSaving
        ) {
            return
        }

        val location =
            SelectedLocation(
                latitude = latitude,
                longitude = longitude
            )

        if (!location.isValid()) {
            showActionError(
                "Seçilen işletme konumu geçerli değil."
            )
            return
        }

        reverseGeocodeJob?.cancel()

        /*
         * Yeni konuma bağlı alanlar temizlenir.
         * Kat, daire/iş yeri no ve adres tarifi korunur.
         */
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
                            address.city.isNotBlank() &&
                                    address.district.isNotBlank() &&
                                    address.neighborhood.isNotBlank() &&
                                    address.street.isNotBlank() &&
                                    address.buildingNo.isNotBlank()

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
                                        "İşletme adresi otomatik dolduruldu. Kaydetmeden önce kontrol edin."
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
        if (_uiState.value.isSaving) {
            return
        }

        if (
            value.isEmpty() ||
            value.all {
                it.isDigit()
            }
        ) {
            if (value.length <= 4) {
                _uiState.value =
                    _uiState.value.copy(
                        dailyCapacityText =
                            value,

                        errorMessage = null
                    )
            }
        }
    }

    fun updateAvailability(
        value: Boolean
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                isAvailable = value,
                errorMessage = null
            )
    }

    fun saveProfile() {
        val current =
            _uiState.value

        if (
            current.isSaving ||
            !current.isEditing ||
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
                showActionError(
                    "İşletme adı 2 ile 150 karakter arasında olmalıdır."
                )
                return
            }

            description.length !in 10..1000 -> {
                showActionError(
                    "İşletme açıklaması 10 ile 1000 karakter arasında olmalıdır."
                )
                return
            }

            location == null ||
                    !location.isValid() -> {
                showActionError(
                    "Geçerli bir işletme konumu seçmelisiniz."
                )
                return
            }

            current.city.isBlank() -> {
                showActionError(
                    "İl alanı boş bırakılamaz."
                )
                return
            }

            current.district.isBlank() -> {
                showActionError(
                    "İlçe alanı boş bırakılamaz."
                )
                return
            }

            current.neighborhood.isBlank() -> {
                showActionError(
                    "Mahalle alanı boş bırakılamaz."
                )
                return
            }

            current.street.isBlank() -> {
                showActionError(
                    "Cadde / sokak alanı boş bırakılamaz."
                )
                return
            }

            current.buildingNo.isBlank() -> {
                showActionError(
                    "Bina numarası boş bırakılamaz."
                )
                return
            }

            dailyCapacity == null ||
                    dailyCapacity !in 1..1000 -> {
                showActionError(
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
            showActionError(
                "İşletme adresi 10 ile 500 karakter arasında olmalıdır."
            )
            return
        }

        saveProfileJob?.cancel()

        saveProfileJob =
            viewModelScope.launch {
                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    showSaveError(
                        "Oturum bilgisi bulunamadı."
                    )
                    return@launch
                }

                _uiState.value =
                    _uiState.value.copy(
                        isSaving = true,
                        errorMessage = null,
                        successMessage = null
                    )

                try {
                    val businessImagePart =
                        current
                            .selectedBusinessImageUri
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?.let { uri ->
                                producerBusinessImageMultipartFactory
                                    .createPart(uri)
                            }

                    val response =
                        producerRepository
                            .updateMyProfile(
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
                                    dailyCapacity,

                                isAvailable =
                                    current.isAvailable,

                                businessImage =
                                    businessImagePart
                            )

                    val responseBody =
                        response.body()

                    val updatedProfile =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        updatedProfile != null
                    ) {
                        applySavedProfile(
                            profile =
                                updatedProfile,

                            message =
                                responseBody.message
                                    .ifBlank {
                                        "Üretici profili başarıyla güncellendi."
                                    }
                        )
                    } else {
                        showSaveError(
                            parseErrorMessage(
                                response
                                    .errorBody()
                                    ?.string()
                            )
                                ?: "Üretici profili güncellenemedi."
                        )
                    }
                } catch (
                    exception:
                    IllegalArgumentException
                ) {
                    showSaveError(
                        exception.message
                            ?: "Seçilen işletme görseli geçersiz."
                    )
                } catch (_: SecurityException) {
                    showSaveError(
                        "Seçilen işletme görseline erişilemiyor. Görseli yeniden seçin."
                    )
                } catch (_: IOException) {
                    showSaveError(
                        "Sunucuya bağlanılamadı veya seçilen görsel okunamadı."
                    )
                } catch (_: Exception) {
                    showSaveError(
                        "Üretici profili güncellenirken bir hata oluştu."
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

    private fun applyLoadedProfile(
        profile:
        ProducerApplicationStatusResponse
    ) {
        _uiState.value =
            ProducerProfileUiState(
                isLoading = false,

                profile = profile,

                businessName =
                    profile.businessName,

                description =
                    profile.description,

                city =
                    profile.city,

                district =
                    profile.district,

                neighborhood =
                    profile.neighborhood,

                street =
                    profile.street,

                buildingNo =
                    profile.buildingNo,

                floor =
                    profile.floor.orEmpty(),

                apartmentNo =
                    profile.apartmentNo.orEmpty(),

                addressNote =
                    profile.addressNote.orEmpty(),

                selectedLocation =
                    profileLocation(profile),

                dailyCapacityText =
                    profile.dailyCapacity
                        .toString(),

                isAvailable =
                    profile.isAvailable
            )
    }

    private fun applySavedProfile(
        profile:
        ProducerApplicationStatusResponse,

        message: String
    ) {
        _uiState.value =
            ProducerProfileUiState(
                isLoading = false,
                isSaving = false,

                profile = profile,
                isEditing = false,

                businessName =
                    profile.businessName,

                description =
                    profile.description,

                city =
                    profile.city,

                district =
                    profile.district,

                neighborhood =
                    profile.neighborhood,

                street =
                    profile.street,

                buildingNo =
                    profile.buildingNo,

                floor =
                    profile.floor.orEmpty(),

                apartmentNo =
                    profile.apartmentNo.orEmpty(),

                addressNote =
                    profile.addressNote.orEmpty(),

                selectedLocation =
                    profileLocation(profile),

                dailyCapacityText =
                    profile.dailyCapacity
                        .toString(),

                isAvailable =
                    profile.isAvailable,

                successMessage =
                    message
            )
    }

    private fun profileLocation(
        profile:
        ProducerApplicationStatusResponse
    ): SelectedLocation? {
        return SelectedLocation(
            latitude =
                profile.latitude,

            longitude =
                profile.longitude
        ).takeIf {
            it.isValid()
        }
    }

    private fun updateAddressField(
        transform:
        ProducerProfileUiState.() ->
        ProducerProfileUiState
    ) {
        if (_uiState.value.isSaving) {
            return
        }

        _uiState.value =
            _uiState.value
                .transform()
                .copy(
                    errorMessage = null
                )
    }

    private fun showLoadError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                errorMessage = message
            )
    }

    private fun showActionError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = message,
                successMessage = null
            )
    }

    private fun showSaveError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isSaving = false,
                errorMessage = message,
                successMessage = null
            )
    }

    private fun parseErrorMessage(
        errorJson: String?
    ): String? {
        if (errorJson.isNullOrBlank()) {
            return null
        }

        return runCatching {
            val root =
                JSONObject(errorJson)

            val data =
                root.optJSONObject("data")
                    ?: root.optJSONObject("Data")

            val errors =
                data?.optJSONObject("errors")
                    ?: data?.optJSONObject("Errors")

            if (errors != null) {
                val keys =
                    errors.keys()

                if (keys.hasNext()) {
                    val field =
                        keys.next()

                    val fieldErrors =
                        errors.optJSONArray(field)

                    val firstError =
                        fieldErrors
                            ?.optString(0)
                            ?.takeIf {
                                it.isNotBlank()
                            }

                    if (firstError != null) {
                        return@runCatching "$field: $firstError"
                    }
                }
            }

            root.optString("message")
                .takeIf {
                    it.isNotBlank()
                }
        }.getOrNull()
    }
}