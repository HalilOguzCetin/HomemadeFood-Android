package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.CategoryRepository
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

    private val addressRepository:
    AddressRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var storefrontLoadJob: Job? =
        null

    private var popularStorefrontLoadJob:
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
                isPopularStorefrontsLoading = true
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
    }

    private fun showDeliveryAddressError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isDeliveryAddressLoading =
                    false,

                deliveryAddressErrorMessage =
                    message
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