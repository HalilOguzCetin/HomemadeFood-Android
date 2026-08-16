package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse

data class CustomerHomeUiState(
    /*
     * C4:
     * Home'da kullanılacak aktif teslimat adresi.
     *
     * deliveryAddresses C4C hızlı seçim arayüzünde
     * doğrudan kullanılacak.
     */
    val isDeliveryAddressLoading: Boolean = false,

    val deliveryAddresses:
    List<AddressResponse> =
        emptyList(),

    val selectedDeliveryAddress:
    AddressResponse? = null,

    val deliveryAddressErrorMessage:
    String? = null,

    val isCategoriesLoading: Boolean = false,

    val categories: List<CategoryResponse> =
        emptyList(),

    val selectedCategoryId: Int? = null,

    /*
     * AŞAMA 4 ile arama artık doğrudan ana sayfadaki
     * işletme/vitrin kartlarını filtreler.
     */
    val searchQuery: String = "",

    val isStorefrontsLoading: Boolean = false,

    val storefronts:
    List<ProducerStorefrontSummaryResponse> =
        emptyList(),

    val categoryErrorMessage: String? = null,

    val storefrontErrorMessage: String? = null
)