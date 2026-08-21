package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.model.DiscoverProducerStorefrontResponse
import com.homemadefood.app.data.model.NearbyProducerStorefrontResponse
import com.homemadefood.app.data.model.PopularFoodResponse
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
     * Popüler İşletmeler ayrı endpoint/state.
     */
    val isPopularStorefrontsLoading:
    Boolean = false,

    val popularStorefronts:
    List<PopularProducerStorefrontResponse> =
        emptyList(),

    val popularStorefrontErrorMessage:
    String? = null,

    /*
     * H6C:
     * Seçili teslimat adresine göre backend'in gerçek
     * mesafe hesabıyla döndürdüğü yakın işletmeler.
     */
    val isNearbyStorefrontsLoading:
    Boolean = false,

    val nearbyStorefronts:
    List<NearbyProducerStorefrontResponse> =
        emptyList(),

    val nearbyStorefrontErrorMessage:
    String? = null,

    /*
     * H8E-2:
     * Aktif teslimat adresinin şehrindeki işletmeler.
     * Backend sıralaması:
     * popularity DESC -> rating DESC -> distance ASC.
     */
    val isCityStorefrontsLoading:
    Boolean = false,

    val cityStorefronts:
    List<DiscoverProducerStorefrontResponse> =
        emptyList(),

    val cityStorefrontErrorMessage:
    String? = null,

    /*
     * H5B:
     * Popüler Yemekler normal işletme ve kategori
     * state'lerinden tamamen ayrıdır.
     */
    val isPopularFoodsLoading:
    Boolean = false,

    val popularFoods:
    List<PopularFoodResponse> =
        emptyList(),

    val popularFoodErrorMessage:
    String? = null,

    /*
     * H5C:
     * Home üzerindeki yemek favorileri backend'den
     * okunur. UI yalnız bu state'i gösterir.
     */
    val isHomeFavoritesLoading:
    Boolean = false,

    val homeFavoriteFoodIds:
    Set<Int> = emptySet(),

    /*
     * Aynı anda tek favori aksiyonu:
     * çift tıklama / çakışan add-remove isteklerini engeller.
     */
    val homeFavoriteActionFoodId:
    Int? = null,

    val homeFavoriteMessage:
    String? = null,

    val homeFavoriteErrorMessage:
    String? = null,

    val categoryErrorMessage:
    String? = null,

    val storefrontErrorMessage:
    String? = null
)