package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.FavoriteRepository
import com.homemadefood.app.data.repository.FoodRepository
import com.homemadefood.app.data.repository.StorefrontRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CustomerHomeViewModel(
    private val categoryRepository:
    CategoryRepository,

    private val storefrontRepository:
    StorefrontRepository,

    private val foodRepository:
    FoodRepository,

    private val favoriteRepository:
    FavoriteRepository,

    private val addressRepository:
    AddressRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var storefrontLoadJob: Job? =
        null

    private var popularStorefrontLoadJob:
            Job? = null

    private var nearbyStorefrontLoadJob:
            Job? = null

    private var cityStorefrontLoadJob:
            Job? = null

    private var popularFoodLoadJob:
            Job? = null

    private var loadedStorefronts:
            List<ProducerStorefrontSummaryResponse> =
        emptyList()

    private val _uiState =
        MutableStateFlow(
            CustomerHomeUiState(
                isDeliveryAddressLoading = true,
                isCategoriesLoading = true,
                isStorefrontsLoading = true,
                isPopularStorefrontsLoading = true,
                isNearbyStorefrontsLoading = true,
                isCityStorefrontsLoading = true,
                isPopularFoodsLoading = true,
                isHomeFavoritesLoading = true
            )
        )

    val uiState:
            StateFlow<CustomerHomeUiState> =
        _uiState.asStateFlow()

    init {
        loadDeliveryAddresses()
        loadCategories()
        loadStorefronts()
        loadPopularStorefronts()
        loadPopularFoods()
        loadHomeFavorites()
    }

    fun loadDeliveryAddresses() {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isDeliveryAddressLoading = true,
                    deliveryAddressErrorMessage = null
                )

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
                    resolveAndApplyDeliveryAddress(
                        addresses = addresses
                    )
                } else {
                    showDeliveryAddressError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Teslimat adresleri alınamadı."
                    )
                }
            } catch (_: IOException) {
                showDeliveryAddressError(
                    "Teslimat adresleri için sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showDeliveryAddressError(
                    "Teslimat adresleri yüklenirken bir hata oluştu."
                )
            }
        }
    }

    fun selectDeliveryAddress(
        addressId: Int
    ) {
        val selectedAddress =
            _uiState.value
                .deliveryAddresses
                .firstOrNull {
                    it.id == addressId
                }
                ?: return

        viewModelScope.launch {
            sessionManager
                .setSelectedDeliveryAddressId(
                    selectedAddress.id
                )

            _uiState.value =
                _uiState.value.copy(
                    selectedDeliveryAddress =
                        selectedAddress,

                    deliveryAddressErrorMessage =
                        null
                )

            loadNearbyStorefronts(
                address =
                    selectedAddress
            )

            loadCityStorefronts(
                address =
                    selectedAddress
            )
        }
    }

    private suspend fun resolveAndApplyDeliveryAddress(
        addresses: List<AddressResponse>
    ) {
        if (addresses.isEmpty()) {
            sessionManager
                .clearSelectedDeliveryAddress()

            _uiState.value =
                _uiState.value.copy(
                    isDeliveryAddressLoading =
                        false,

                    deliveryAddresses =
                        emptyList(),

                    selectedDeliveryAddress =
                        null,

                    deliveryAddressErrorMessage =
                        null,

                    isNearbyStorefrontsLoading =
                        false,

                    nearbyStorefronts =
                        emptyList(),

                    nearbyStorefrontErrorMessage =
                        null,

                    isCityStorefrontsLoading =
                        false,

                    cityStorefronts =
                        emptyList(),

                    cityStorefrontErrorMessage =
                        null
                )

            return
        }

        val storedAddressId =
            sessionManager
                .selectedDeliveryAddressId
                .first()

        val storedAddress =
            storedAddressId?.let {
                    selectedId ->

                addresses.firstOrNull {
                        address ->
                    address.id == selectedId
                }
            }

        val resolvedAddress =
            storedAddress
                ?: addresses
                    .firstOrNull {
                        it.isDefault
                    }
                ?: addresses
                    .first()

        if (
            storedAddressId !=
            resolvedAddress.id
        ) {
            sessionManager
                .setSelectedDeliveryAddressId(
                    resolvedAddress.id
                )
        }

        _uiState.value =
            _uiState.value.copy(
                isDeliveryAddressLoading =
                    false,

                deliveryAddresses =
                    addresses,

                selectedDeliveryAddress =
                    resolvedAddress,

                deliveryAddressErrorMessage =
                    null
            )

        loadNearbyStorefronts(
            address =
                resolvedAddress
        )

        loadCityStorefronts(
            address =
                resolvedAddress
        )
    }

    private fun showDeliveryAddressError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isDeliveryAddressLoading =
                    false,

                deliveryAddressErrorMessage =
                    message,

                isNearbyStorefrontsLoading =
                    false,

                nearbyStorefronts =
                    emptyList(),

                isCityStorefrontsLoading =
                    false,

                cityStorefronts =
                    emptyList()
            )
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isCategoriesLoading = true,
                    categoryErrorMessage = null
                )

            try {
                val response =
                    categoryRepository
                        .getCategories()

                val responseBody =
                    response.body()

                val categories =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    categories != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            isCategoriesLoading = false,

                            categories =
                                categories.filter {
                                    it.isActive != false
                                },

                            categoryErrorMessage = null
                        )
                } else {
                    showCategoryError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Kategoriler alınamadı."
                    )
                }
            } catch (_: IOException) {
                showCategoryError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showCategoryError(
                    "Kategoriler yüklenirken bir hata oluştu."
                )
            }
        }
    }

    /*
     * H6C:
     * Seçili teslimat adresini backend nearby endpoint'ine
     * gönderir. Mesafe Android'de hesaplanmaz.
     */
    fun loadNearbyStorefronts(
        address: AddressResponse? =
            _uiState.value
                .selectedDeliveryAddress,
        radiusKm: Double = 15.0,
        limit: Int = 6
    ) {
        nearbyStorefrontLoadJob
            ?.cancel()

        if (address == null) {
            _uiState.value =
                _uiState.value.copy(
                    isNearbyStorefrontsLoading =
                        false,
                    nearbyStorefronts =
                        emptyList(),
                    nearbyStorefrontErrorMessage =
                        null
                )

            return
        }

        nearbyStorefrontLoadJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isNearbyStorefrontsLoading =
                            true,
                        nearbyStorefrontErrorMessage =
                            null
                    )

                try {
                    val response =
                        storefrontRepository
                            .getNearbyStorefronts(
                                latitude =
                                    address.latitude,

                                longitude =
                                    address.longitude,

                                radiusKm =
                                    radiusKm,

                                limit =
                                    limit
                            )

                    val responseBody =
                        response.body()

                    val storefronts =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        storefronts != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isNearbyStorefrontsLoading =
                                    false,

                                nearbyStorefronts =
                                    storefronts,

                                nearbyStorefrontErrorMessage =
                                    null
                            )
                    } else {
                        showNearbyStorefrontError(
                            parseErrorMessage(
                                response
                                    .errorBody()
                                    ?.string()
                            )
                                ?: "Yakındaki işletmeler alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showNearbyStorefrontError(
                        "Yakındaki işletmeler için sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showNearbyStorefrontError(
                        "Yakındaki işletmeler yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    /*
     * H8E-2:
     * Aktif teslimat adresinin şehrindeki işletmeleri getirir.
     *
     * Android tarafında şehir/mesafe sıralaması yapılmaz.
     * Backend'in /api/Producer/storefronts/city sonucu aynen korunur.
     */
    fun loadCityStorefronts(
        address: AddressResponse? =
            _uiState.value
                .selectedDeliveryAddress,
        limit: Int = 8
    ) {
        cityStorefrontLoadJob
            ?.cancel()

        if (address == null) {
            _uiState.value =
                _uiState.value.copy(
                    isCityStorefrontsLoading =
                        false,

                    cityStorefronts =
                        emptyList(),

                    cityStorefrontErrorMessage =
                        null
                )

            return
        }

        val city =
            address.city.trim()

        if (city.isBlank()) {
            _uiState.value =
                _uiState.value.copy(
                    isCityStorefrontsLoading =
                        false,

                    cityStorefronts =
                        emptyList(),

                    cityStorefrontErrorMessage =
                        "Teslimat adresinin şehir bilgisi bulunamadı."
                )

            return
        }

        cityStorefrontLoadJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isCityStorefrontsLoading =
                            true,

                        cityStorefrontErrorMessage =
                            null
                    )

                try {
                    val response =
                        storefrontRepository
                            .getCityStorefronts(
                                city = city,

                                latitude =
                                    address.latitude,

                                longitude =
                                    address.longitude,

                                limit = limit
                            )

                    val responseBody =
                        response.body()

                    val storefronts =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        storefronts != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isCityStorefrontsLoading =
                                    false,

                                cityStorefronts =
                                    storefronts,

                                cityStorefrontErrorMessage =
                                    null
                            )
                    } else {
                        showCityStorefrontError(
                            parseErrorMessage(
                                response
                                    .errorBody()
                                    ?.string()
                            )
                                ?: "Şehrinizdeki işletmeler alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showCityStorefrontError(
                        "Şehrinizdeki işletmeler için sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showCityStorefrontError(
                        "Şehrinizdeki işletmeler yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    /*
     * H4B:
     * Popüler işletmeler normal storefront yükleme işinden
     * bağımsızdır. Arama veya kategori değişimi bu listeyi
     * yeniden sıralamaz.
     */
    fun loadPopularStorefronts(
        limit: Int = 6
    ) {
        popularStorefrontLoadJob
            ?.cancel()

        popularStorefrontLoadJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isPopularStorefrontsLoading =
                            true,
                        popularStorefrontErrorMessage =
                            null
                    )

                try {
                    val response =
                        storefrontRepository
                            .getPopularStorefronts(
                                limit = limit
                            )

                    val responseBody =
                        response.body()

                    val storefronts =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        storefronts != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isPopularStorefrontsLoading =
                                    false,
                                popularStorefronts =
                                    storefronts,
                                popularStorefrontErrorMessage =
                                    null
                            )
                    } else {
                        showPopularStorefrontError(
                            parseErrorMessage(
                                response
                                    .errorBody()
                                    ?.string()
                            )
                                ?: "Popüler işletmeler alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showPopularStorefrontError(
                        "Popüler işletmeler için sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showPopularStorefrontError(
                        "Popüler işletmeler yüklenirken bir hata oluştu."
                    )
                }
            }
    }


    /*
     * H5B:
     * Popüler yemekler backend'in /api/Food/popular endpoint'inden
     * bağımsız olarak yüklenir.
     *
     * Arama/kategori değişiklikleri bu listeyi etkilemez.
     */
    fun loadPopularFoods(
        limit: Int = 8
    ) {
        popularFoodLoadJob
            ?.cancel()

        popularFoodLoadJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isPopularFoodsLoading =
                            true,
                        popularFoodErrorMessage =
                            null
                    )

                try {
                    val response =
                        foodRepository
                            .getPopularFoods(
                                limit = limit
                            )

                    val responseBody =
                        response.body()

                    val foods =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        foods != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isPopularFoodsLoading =
                                    false,
                                popularFoods =
                                    foods,
                                popularFoodErrorMessage =
                                    null
                            )
                    } else {
                        showPopularFoodError(
                            parseErrorMessage(
                                response
                                    .errorBody()
                                    ?.string()
                            )
                                ?: "Popüler yemekler alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showPopularFoodError(
                        "Popüler yemekler için sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showPopularFoodError(
                        "Popüler yemekler yüklenirken bir hata oluştu."
                    )
                }
            }
    }


    /*
     * H5C-1:
     * Home açıldığında mevcut favori yemek ID'leri
     * gerçek Favorite endpoint'inden okunur.
     */
    fun loadHomeFavorites() {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isHomeFavoritesLoading = true,
                    homeFavoriteErrorMessage = null
                )

            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                _uiState.value =
                    _uiState.value.copy(
                        isHomeFavoritesLoading = false,
                        homeFavoriteFoodIds = emptySet(),
                        homeFavoriteErrorMessage =
                            "Oturum bilgisi bulunamadı."
                    )

                return@launch
            }

            try {
                val response =
                    favoriteRepository
                        .getFavorites()

                val responseBody =
                    response.body()

                val favorites =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    favorites != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            isHomeFavoritesLoading = false,
                            homeFavoriteFoodIds =
                                favorites
                                    .map { favorite ->
                                        favorite.foodId
                                    }
                                    .toSet(),
                            homeFavoriteErrorMessage = null
                        )
                } else {
                    showHomeFavoriteLoadError(
                        parseErrorMessage(
                            response
                                .errorBody()
                                ?.string()
                        )
                            ?: "Favoriler alınamadı."
                    )
                }
            } catch (_: IOException) {
                showHomeFavoriteLoadError(
                    "Favoriler için sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showHomeFavoriteLoadError(
                    "Favoriler yüklenirken bir hata oluştu."
                )
            }
        }
    }

    /*
     * H5C-1:
     * Popüler yemek kartından kullanılacak gerçek
     * add/remove favorite aksiyonu.
     *
     * UI H5C-2'de bu fonksiyona bağlanacak.
     */
    fun toggleHomeFavorite(
        foodId: Int
    ) {
        val currentState =
            _uiState.value

        if (
            currentState.isHomeFavoritesLoading ||
            currentState.homeFavoriteActionFoodId != null
        ) {
            return
        }

        viewModelScope.launch {
            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                showHomeFavoriteActionError(
                    "Oturum bilgisi bulunamadı. Yeniden giriş yapın."
                )

                return@launch
            }

            val wasFavorite =
                _uiState.value
                    .homeFavoriteFoodIds
                    .contains(foodId)

            _uiState.value =
                _uiState.value.copy(
                    homeFavoriteActionFoodId = foodId,
                    homeFavoriteMessage = null,
                    homeFavoriteErrorMessage = null
                )

            try {
                val response =
                    if (wasFavorite) {
                        favoriteRepository
                            .removeFavorite(
                                foodId = foodId
                            )
                    } else {
                        favoriteRepository
                            .addFavorite(
                                foodId = foodId
                            )
                    }

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    val updatedIds =
                        if (wasFavorite) {
                            _uiState.value
                                .homeFavoriteFoodIds -
                                    foodId
                        } else {
                            _uiState.value
                                .homeFavoriteFoodIds +
                                    foodId
                        }

                    _uiState.value =
                        _uiState.value.copy(
                            homeFavoriteFoodIds =
                                updatedIds,

                            homeFavoriteActionFoodId =
                                null,

                            homeFavoriteMessage =
                                responseBody.message.ifBlank {
                                    if (wasFavorite) {
                                        "Yemek favorilerden çıkarıldı."
                                    } else {
                                        "Yemek favorilere eklendi."
                                    }
                                },

                            homeFavoriteErrorMessage =
                                null
                        )
                } else {
                    showHomeFavoriteActionError(
                        parseErrorMessage(
                            response
                                .errorBody()
                                ?.string()
                        )
                            ?: "Favori işlemi gerçekleştirilemedi."
                    )
                }
            } catch (_: IOException) {
                showHomeFavoriteActionError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showHomeFavoriteActionError(
                    "Favori işlemi sırasında bir hata oluştu."
                )
            }
        }
    }

    fun clearHomeFavoriteMessage() {
        _uiState.value =
            _uiState.value.copy(
                homeFavoriteMessage = null,
                homeFavoriteErrorMessage = null
            )
    }

    fun loadStorefronts(
        categoryId: Int? =
            _uiState.value.selectedCategoryId
    ) {
        storefrontLoadJob?.cancel()

        storefrontLoadJob =
            viewModelScope.launch {
                loadedStorefronts =
                    emptyList()

                _uiState.value =
                    _uiState.value.copy(
                        isStorefrontsLoading = true,
                        storefronts = emptyList(),
                        storefrontErrorMessage = null
                    )

                try {
                    val response =
                        storefrontRepository
                            .getStorefronts(
                                categoryId =
                                    categoryId
                            )

                    val responseBody =
                        response.body()

                    val storefronts =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        storefronts != null
                    ) {
                        loadedStorefronts =
                            storefronts

                        _uiState.value =
                            _uiState.value.copy(
                                isStorefrontsLoading = false,
                                storefrontErrorMessage = null
                            )

                        applySearchFilter()
                    } else {
                        showStorefrontError(
                            parseErrorMessage(
                                response
                                    .errorBody()
                                    ?.string()
                            )
                                ?: "İşletmeler alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showStorefrontError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showStorefrontError(
                        "İşletmeler yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun updateSearchQuery(
        query: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                searchQuery = query
            )
    }

    fun searchStorefronts() {
        applySearchFilter()
    }

    fun selectCategory(
        categoryId: Int?
    ) {
        _uiState.value =
            _uiState.value.copy(
                selectedCategoryId =
                    categoryId
            )

        loadStorefronts(
            categoryId = categoryId
        )
    }

    fun clearFilters() {
        _uiState.value =
            _uiState.value.copy(
                selectedCategoryId = null,
                searchQuery = ""
            )

        loadStorefronts(
            categoryId = null
        )
    }

    private fun applySearchFilter() {
        val query =
            _uiState.value
                .searchQuery
                .trim()

        val filtered =
            if (query.isBlank()) {
                loadedStorefronts
            } else {
                loadedStorefronts.filter {
                        storefront ->

                    storefront.businessName
                        .contains(
                            query,
                            ignoreCase = true
                        ) ||
                            storefront.description
                                .contains(
                                    query,
                                    ignoreCase = true
                                ) ||
                            storefront.city
                                .contains(
                                    query,
                                    ignoreCase = true
                                ) ||
                            storefront.district
                                .contains(
                                    query,
                                    ignoreCase = true
                                )
                }
            }

        _uiState.value =
            _uiState.value.copy(
                storefronts = filtered
            )
    }

    private fun showCategoryError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isCategoriesLoading = false,
                categoryErrorMessage = message
            )
    }

    private fun showStorefrontError(
        message: String
    ) {
        loadedStorefronts =
            emptyList()

        _uiState.value =
            _uiState.value.copy(
                isStorefrontsLoading = false,
                storefronts = emptyList(),
                storefrontErrorMessage = message
            )
    }

    private fun showNearbyStorefrontError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isNearbyStorefrontsLoading =
                    false,

                nearbyStorefronts =
                    emptyList(),

                nearbyStorefrontErrorMessage =
                    message
            )
    }

    private fun showCityStorefrontError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isCityStorefrontsLoading =
                    false,

                cityStorefronts =
                    emptyList(),

                cityStorefrontErrorMessage =
                    message
            )
    }

    private fun showPopularStorefrontError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isPopularStorefrontsLoading =
                    false,
                popularStorefronts =
                    emptyList(),
                popularStorefrontErrorMessage =
                    message
            )
    }


    private fun showPopularFoodError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isPopularFoodsLoading =
                    false,
                popularFoods =
                    emptyList(),
                popularFoodErrorMessage =
                    message
            )
    }


    private fun showHomeFavoriteLoadError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isHomeFavoritesLoading = false,
                homeFavoriteFoodIds = emptySet(),
                homeFavoriteErrorMessage = message
            )
    }

    private fun showHomeFavoriteActionError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                homeFavoriteActionFoodId = null,
                homeFavoriteErrorMessage = message
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