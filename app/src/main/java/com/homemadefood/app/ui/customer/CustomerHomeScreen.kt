package com.homemadefood.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse
import com.homemadefood.app.data.remote.ApiConfig
import java.util.Locale

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
    onStorefrontClick: (Int) -> Unit,
    cartTotalQuantity: Int,
    onCartClick: () -> Unit,
    onRecommendationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    CustomerHomeColors.Cream
                )
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 18.dp
                )
        ) {
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
                            Modifier.height(3.dp)
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
                    Modifier.height(14.dp)
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
                    Modifier.height(18.dp)
            )

            CustomerHomeCampaignSection(
                modifier =
                    Modifier.fillMaxWidth()
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            CustomerHomeSearchAndCategorySection(
                searchQuery =
                    uiState.searchQuery,

                categories =
                    uiState.categories,

                selectedCategoryId =
                    uiState.selectedCategoryId,

                isCategoriesLoading =
                    uiState.isCategoriesLoading,

                categoryErrorMessage =
                    uiState.categoryErrorMessage,

                isStorefrontsLoading =
                    uiState.isStorefrontsLoading,

                onSearchQueryChange =
                    onSearchQueryChange,

                onSearchClick =
                    onSearchClick,

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
                    Modifier.height(26.dp)
            )

            if (
                uiState.searchQuery.isBlank() &&
                uiState.selectedCategoryId == null &&
                !uiState.isPopularStorefrontsLoading &&
                uiState.popularStorefronts.isNotEmpty()
            ) {
                CustomerHomePopularStorefrontSection(
                    storefronts =
                        uiState.popularStorefronts,

                    onStorefrontClick =
                        onStorefrontClick,

                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier =
                        Modifier.height(30.dp)
                )
            }

            Text(
                text = "İşletmeler",

                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Text(
                text =
                    if (
                        uiState.selectedCategoryId ==
                        null
                    ) {
                        "Aktif yemekleri bulunan işletmeler"
                    } else {
                        "Seçili kategoride yemek sunan işletmeler"
                    },

                modifier =
                    Modifier.padding(
                        top = 4.dp
                    ),

                style =
                    MaterialTheme.typography
                        .bodyMedium,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            when {
                uiState.isStorefrontsLoading -> {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.align(
                                Alignment.CenterHorizontally
                            )
                    )
                }

                uiState.storefronts.isEmpty() &&
                        uiState
                            .storefrontErrorMessage != null -> {

                    Text(
                        text =
                            uiState
                                .storefrontErrorMessage,

                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Button(
                        onClick =
                            onRetryStorefrontsClick,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "İşletmeleri Tekrar Yükle"
                        )
                    }
                }

                uiState.storefronts.isEmpty() -> {
                    Text(
                        text =
                            if (
                                uiState.searchQuery
                                    .isNotBlank()
                            ) {
                                "Aramanıza uygun işletme bulunamadı."
                            } else if (
                                uiState
                                    .selectedCategoryId !=
                                null
                            ) {
                                "Bu kategoride aktif yemek sunan işletme bulunamadı."
                            } else {
                                "Şu anda listelenecek aktif işletme bulunamadı."
                            },

                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )
                }

                else -> {
                    uiState.storefronts
                        .forEach {
                                storefront ->

                            StorefrontCard(
                                storefront =
                                    storefront,

                                categoryFiltered =
                                    uiState
                                        .selectedCategoryId !=
                                            null,

                                onClick = {
                                    onStorefrontClick(
                                        storefront
                                            .producerProfileId
                                    )
                                }
                            )
                        }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )

            Text(
                text = "Hızlı İşlemler",

                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Button(
                onClick =
                    onRecommendationClick,

                modifier =
                    Modifier.fillMaxWidth()
            ) {
                Text(
                    "Akıllı Üretici Önerisi"
                )
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )
        }
    }

}

@Composable
private fun StorefrontCard(
    storefront:
    ProducerStorefrontSummaryResponse,

    categoryFiltered: Boolean,

    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 7.dp
                )
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(
                18.dp
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {
        Column {
            StorefrontImage(
                businessImageUrl =
                    storefront
                        .businessImageUrl,

                businessName =
                    storefront
                        .businessName
            )

            Column(
                modifier =
                    Modifier.padding(
                        18.dp
                    )
            ) {
                Text(
                    text =
                        storefront.businessName,

                    style =
                        MaterialTheme.typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text =
                            if (
                                storefront.rating >
                                0.0
                            ) {
                                "★ ${
                                    formatRating(
                                        storefront.rating
                                    )
                                }"
                            } else {
                                "Yeni işletme"
                            },

                        style =
                            MaterialTheme.typography
                                .labelLarge,

                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )

                    Text(
                        text =
                            listOf(
                                storefront.district,
                                storefront.city
                            )
                                .filter {
                                    it.isNotBlank()
                                }
                                .joinToString(
                                    " / "
                                ),

                        style =
                            MaterialTheme.typography
                                .bodySmall
                    )
                }

                if (
                    storefront.description
                        .isNotBlank()
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            storefront.description,

                        style =
                            MaterialTheme.typography
                                .bodyMedium,

                        maxLines = 3,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Text(
                    text =
                        if (categoryFiltered) {
                            "Bu kategoride ${storefront.matchingFoodCount} aktif yemek"
                        } else {
                            "${storefront.availableFoodCount} aktif yemek"
                        },

                    style =
                        MaterialTheme.typography
                            .labelLarge
                )

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        "${storefront.availableCategoryCount} kategoride menü",

                    style =
                        MaterialTheme.typography
                            .bodySmall
                )

                storefront
                    .minimumPreparationTimeMinutes
                    ?.let {
                            minutes ->

                        Spacer(
                            modifier =
                                Modifier.height(
                                    5.dp
                                )
                        )

                        Text(
                            text =
                                "En kısa hazırlama: $minutes dk",

                            style =
                                MaterialTheme.typography
                                    .bodySmall
                        )
                    }
            }
        }
    }
}

@Composable
private fun StorefrontImage(
    businessImageUrl: String?,
    businessName: String
) {
    val resolvedImageUrl =
        ApiConfig.resolveMediaUrl(
            businessImageUrl
        )

    if (resolvedImageUrl == null) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(190.dp),

            tonalElevation = 1.dp
        ) {
            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text =
                        "İşletme görseli bulunmuyor",

                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }
        }

        return
    }

    AsyncImage(
        model =
            resolvedImageUrl,

        contentDescription =
            "$businessName işletme vitrin görseli",

        modifier =
            Modifier
                .fillMaxWidth()
                .height(190.dp),

        contentScale =
            ContentScale.Crop
    )
}

private fun formatRating(
    rating: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.1f",
        rating
    )
}