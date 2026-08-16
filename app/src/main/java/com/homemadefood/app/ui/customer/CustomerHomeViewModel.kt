package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.DeliveryAddressSelectionManager
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.StorefrontRepository
import com.homemadefood.app.data.remote.ApiErrorParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class CustomerHomeViewModel(
    private val categoryRepository:
    CategoryRepository,

    private val storefrontRepository:
    StorefrontRepository,

    private val addressRepository:
    AddressRepository,

    private val deliveryAddressSelectionManager:
    DeliveryAddressSelectionManager
) : ViewModel() {

    private var storefrontLoadJob: Job? =
        null

    /*
     * Backend kategori filtresini uygular.
     * Arama metni ise o anda yüklenmiş işletmeler üzerinde
     * yalnızca vitrin adı/açıklama/konum alanlarında uygulanır.
     *
     * AŞAMA 4'ün amacı yemek araması değil,
     * işletme keşif ekranıdır.
     */
    private var loadedStorefronts:
            List<ProducerStorefrontSummaryResponse> =
        emptyList()

    private val _uiState =
        MutableStateFlow(
            CustomerHomeUiState(
                isDeliveryAddressLoading = true,
                isCategoriesLoading = true,
                isStorefrontsLoading = true
            )
        )

    val uiState:
            StateFlow<CustomerHomeUiState> =
        _uiState.asStateFlow()

    init {
        loadDeliveryAddresses()
        loadCategories()
        loadStorefronts()
    }

    /*
     * C4B aktif teslimat adresi çözümleme algoritması:
     *
     * 1) DataStore'daki selectedDeliveryAddressId hâlâ
     *    kullanıcı adresleri arasında ise onu kullan.
     *
     * 2) Yoksa backend IsDefault adresini kullan.
     *
     * 3) O da yoksa listenin ilk adresini kullan.
     *
     * 4) Adres hiç yoksa seçimi temizle.
     *
     * Böylece backend'deki "varsayılan adres" ile
     * uygulamadaki "şu an seçili teslimat adresi"
     * birbirinden ayrılmış olur.
     */
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

    /*
     * C4C hızlı seçim ekranı bu metodu kullanacak.
     * Şimdiden altyapıya ekliyoruz.
     *
     * Yalnız gerçekten bu kullanıcıya ait ve o anda
     * yüklenmiş adresler seçilebilir.
     */
    fun selectDeliveryAddress(
        addressId: Int
    ) {
        viewModelScope.launch {
            val selectedAddress =
                deliveryAddressSelectionManager
                    .select(
                        addressId =
                            addressId,

                        addresses =
                            _uiState.value
                                .deliveryAddresses
                    )
                    ?: return@launch

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
        val resolvedAddress =
            deliveryAddressSelectionManager
                .resolve(
                    addresses
                )

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
        /*
         * Geçici ağ hatasında son başarılı seçimi ekrandan
         * silmiyoruz. C4C'de kullanıcı mevcut address label'ını
         * görmeye devam edebilir ve Retry kullanabilir.
         */
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