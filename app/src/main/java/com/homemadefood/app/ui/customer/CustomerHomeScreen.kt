package com.homemadefood.app.ui.customer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomerHomeScreen(
    uiState: CustomerHomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCategoryClick: (Int?) -> Unit,
    onClearFiltersClick: () -> Unit,
    onRetryDeliveryAddressesClick: () -> Unit,
    onDeliveryAddressSelected: (Int) -> Unit,
    onAddAddressClick: () -> Unit,
    onManageAddressesClick: () -> Unit,
    onRetryCategoriesClick: () -> Unit,
    onRetryStorefrontsClick: () -> Unit,
    onRetryNearbyStorefrontsClick: () -> Unit,
    onRetryCityStorefrontsClick: () -> Unit,
    onStorefrontClick: (Int) -> Unit,
    onFoodClick: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    cartTotalQuantity: Int,
    onCartClick: () -> Unit,
    onRecommendationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        LazyColumn(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(
                        CustomerHomeColors
                            .Cream
                    ),

            contentPadding =
                PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 18.dp,
                    bottom = 18.dp
                )
        ) {
            item {
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    end = 16.dp
                                )
                    ) {
                        Text(
                            text =
                                "Hoş geldiniz 👋",

                            style =
                                MaterialTheme
                                    .typography
                                    .headlineSmall,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                CustomerHomeColors
                                    .DeepOlive
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    3.dp
                                )
                        )

                        Text(
                            text =
                                "Bugün hangi mutfaktan yemek istersiniz?",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,

                            color =
                                CustomerHomeColors
                                    .TextMuted
                        )
                    }

                    CustomerHomeCartButton(
                        totalQuantity =
                            cartTotalQuantity,

                        onClick =
                            onCartClick
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )

                CustomerDeliveryAddressPicker(
                    isLoading =
                        uiState
                            .isDeliveryAddressLoading,

                    addresses =
                        uiState
                            .deliveryAddresses,

                    selectedAddress =
                        uiState
                            .selectedDeliveryAddress,

                    errorMessage =
                        uiState
                            .deliveryAddressErrorMessage,

                    onRetryClick =
                        onRetryDeliveryAddressesClick,

                    onAddressSelected =
                        onDeliveryAddressSelected,

                    onAddAddressClick =
                        onAddAddressClick,

                    onManageAddressesClick =
                        onManageAddressesClick,

                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            18.dp
                        )
                )

                CustomerHomeCampaignSection(
                    modifier =
                        Modifier.fillMaxWidth()
                )

                /*
                 * İlk görünümde arama kartının eski konumunu korur.
                 * Sticky header'ın üst padding'iyle birlikte önceki
                 * tasarımdaki 18dp aralık hissi korunur.
                 */
                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )
            }

            stickyHeader(
                key = "customer_home_search"
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                CustomerHomeColors
                                    .Cream
                            )
                            .padding(
                                top = 8.dp,
                                bottom = 8.dp
                            )
                ) {
                    CustomerHomeSearchField(
                        searchQuery =
                            uiState
                                .searchQuery,

                        isStorefrontsLoading =
                            uiState
                                .isStorefrontsLoading,

                        onSearchQueryChange =
                            onSearchQueryChange,

                        onSearchClick =
                            onSearchClick,

                        onRecommendationClick =
                            onRecommendationClick,

                        modifier =
                            Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )

                CustomerHomeCategorySection(
                    searchQuery =
                        uiState
                            .searchQuery,

                    categories =
                        uiState
                            .categories,

                    selectedCategoryId =
                        uiState
                            .selectedCategoryId,

                    isCategoriesLoading =
                        uiState
                            .isCategoriesLoading,

                    categoryErrorMessage =
                        uiState
                            .categoryErrorMessage,

                    onCategoryClick =
                        onCategoryClick,

                    onClearFiltersClick =
                        onClearFiltersClick,

                    onRetryCategoriesClick =
                        onRetryCategoriesClick,

                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            26.dp
                        )
                )
            }

            if (
                uiState.searchQuery.isBlank() &&
                uiState.selectedCategoryId == null &&
                !uiState.isPopularStorefrontsLoading &&
                uiState.popularStorefronts.isNotEmpty()
            ) {
                item {
                    CustomerHomePopularStorefrontSection(
                        storefronts =
                            uiState
                                .popularStorefronts,

                        onStorefrontClick =
                            onStorefrontClick,

                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                30.dp
                            )
                    )
                }
            }

            if (
                uiState.searchQuery.isBlank() &&
                uiState.selectedCategoryId == null &&
                !uiState.isPopularFoodsLoading &&
                uiState.popularFoods.isNotEmpty()
            ) {
                item {
                    CustomerHomePopularFoodSection(
                        foods =
                            uiState
                                .popularFoods,

                        favoriteFoodIds =
                            uiState
                                .homeFavoriteFoodIds,

                        isFavoritesLoading =
                            uiState
                                .isHomeFavoritesLoading,

                        favoriteActionFoodId =
                            uiState
                                .homeFavoriteActionFoodId,

                        favoriteErrorMessage =
                            uiState
                                .homeFavoriteErrorMessage,

                        onFoodClick =
                            onFoodClick,

                        onFavoriteClick =
                            onToggleFavorite,

                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                30.dp
                            )
                    )
                }
            }

            item {
                CustomerHomeStorefrontDiscoverySection(
                    storefronts =
                        uiState
                            .storefronts,

                    nearbyStorefronts =
                        uiState
                            .nearbyStorefronts,

                    cityStorefronts =
                        uiState
                            .cityStorefronts,

                    selectedDeliveryAddressId =
                        uiState
                            .selectedDeliveryAddress
                            ?.id,

                    searchQuery =
                        uiState
                            .searchQuery,

                    selectedCategoryId =
                        uiState
                            .selectedCategoryId,

                    isStorefrontsLoading =
                        uiState
                            .isStorefrontsLoading,

                    isNearbyStorefrontsLoading =
                        uiState
                            .isNearbyStorefrontsLoading,

                    isCityStorefrontsLoading =
                        uiState
                            .isCityStorefrontsLoading,

                    storefrontErrorMessage =
                        uiState
                            .storefrontErrorMessage,

                    nearbyStorefrontErrorMessage =
                        uiState
                            .nearbyStorefrontErrorMessage,

                    cityStorefrontErrorMessage =
                        uiState
                            .cityStorefrontErrorMessage,

                    onRetryStorefrontsClick =
                        onRetryStorefrontsClick,

                    onRetryNearbyStorefrontsClick =
                        onRetryNearbyStorefrontsClick,

                    onRetryCityStorefrontsClick =
                        onRetryCityStorefrontsClick,

                    onAddAddressClick =
                        onAddAddressClick,

                    onStorefrontClick =
                        onStorefrontClick,

                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            28.dp
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )
            }
        }
    }
}