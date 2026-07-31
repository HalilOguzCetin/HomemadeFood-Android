package com.homemadefood.app.ui.recommendation

import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.ProducerRecommendationResponse
import com.homemadefood.app.data.model.ProducerRecommendationSelectionResponse

data class RecommendationUiState(
    val isLoadingAddresses: Boolean = true,

    val addresses: List<AddressResponse> =
        emptyList(),

    val selectedAddressId: Int? = null,

    val searchText: String = "",

    val quantityText: String = "1",

    val isSearching: Boolean = false,

    val recommendationSearchId: Int? = null,

    val recommendations:
    List<ProducerRecommendationResponse> =
        emptyList(),


    val selectingFoodId: Int? = null,

    val selectedFoodId: Int? = null,

    val isAddingToCart: Boolean = false,

    val cartMessage: String? = null,
    val addedToCartFoodId: Int? = null,

    val selectedRecommendation:
    ProducerRecommendationSelectionResponse? =
        null,

    val errorMessage: String? = null,

    val actionMessage: String? = null

)