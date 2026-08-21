package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.model.DiscoverFoodResponse
import com.homemadefood.app.data.model.DiscoverProducerStorefrontResponse

enum class CustomerExploreContentType {
    FOODS,
    STOREFRONTS
}

data class CustomerExploreUiState(
    val isDeliveryAddressLoading: Boolean = true,
    val deliveryAddresses:
    List<AddressResponse> =
        emptyList(),

    val selectedDeliveryAddress:
    AddressResponse? = null,

    val deliveryAddressErrorMessage:
    String? = null,

    val isCategoriesLoading: Boolean = true,
    val categories:
    List<CategoryResponse> =
        emptyList(),
    val categoryErrorMessage:
    String? = null,

    val searchQuery: String = "",
    val selectedCategoryId: Int? = null,

    val selectedContentType:
    CustomerExploreContentType =
        CustomerExploreContentType.FOODS,

    val isFoodsLoading: Boolean = false,
    val isFoodsLoadingMore: Boolean = false,
    val foods:
    List<DiscoverFoodResponse> =
        emptyList(),
    val foodPage: Int = 0,
    val foodTotalCount: Int = 0,
    val foodHasNextPage: Boolean = false,
    val foodErrorMessage: String? = null,

    val isStorefrontsLoading: Boolean = false,
    val isStorefrontsLoadingMore: Boolean = false,
    val storefronts:
    List<DiscoverProducerStorefrontResponse> =
        emptyList(),
    val storefrontPage: Int = 0,
    val storefrontTotalCount: Int = 0,
    val storefrontHasNextPage: Boolean = false,
    val storefrontErrorMessage: String? = null
)