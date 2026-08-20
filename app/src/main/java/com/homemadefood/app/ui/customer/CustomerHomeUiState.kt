package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.model.PopularProducerStorefrontResponse
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse

data class CustomerHomeUiState(
    val isDeliveryAddressLoading: Boolean = false,

    val deliveryAddresses:
    List<AddressResponse> =
        emptyList(),

    val selectedDeliveryAddress:
    AddressResponse? = null,

    val deliveryAddressErrorMessage:
    String? = null,

    val isCategoriesLoading: Boolean = false,

    val categories:
    List<CategoryResponse> =
        emptyList(),

    val selectedCategoryId:
    Int? = null,

    val searchQuery:
    String = "",

    /*
     * Normal işletme listesi:
     * kategori + işletme araması için kullanılır.
     */
    val isStorefrontsLoading:
    Boolean = false,

    val storefronts:
    List<ProducerStorefrontSummaryResponse> =
        emptyList(),

    /*
     * H4B:
     * Popüler İşletmeler normal storefront listesinden ayrı
     * state taşır. Böylece kategori/arama filtreleri popülerlik
     * listesinin veri kaynağını değiştirmez.
     */
    val isPopularStorefrontsLoading:
    Boolean = false,

    val popularStorefronts:
    List<PopularProducerStorefrontResponse> =
        emptyList(),

    val popularStorefrontErrorMessage:
    String? = null,

    val categoryErrorMessage:
    String? = null,

    val storefrontErrorMessage:
    String? = null
)