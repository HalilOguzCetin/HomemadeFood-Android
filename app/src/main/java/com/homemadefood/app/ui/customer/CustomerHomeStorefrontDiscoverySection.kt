package com.homemadefood.app.ui.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.homemadefood.app.data.model.DiscoverProducerStorefrontResponse
import com.homemadefood.app.data.model.NearbyProducerStorefrontResponse
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse
import com.homemadefood.app.data.remote.ApiConfig
import java.util.Locale

private const val DISCOVERY_TAB_NEARBY = "nearby"
private const val DISCOVERY_TAB_CITY = "city"

@Composable
fun CustomerHomeStorefrontDiscoverySection(
    storefronts: List<ProducerStorefrontSummaryResponse>,
    nearbyStorefronts: List<NearbyProducerStorefrontResponse>,
    cityStorefronts: List<DiscoverProducerStorefrontResponse>,
    selectedDeliveryAddressId: Int?,
    searchQuery: String,
    selectedCategoryId: Int?,
    isStorefrontsLoading: Boolean,
    isNearbyStorefrontsLoading: Boolean,
    isCityStorefrontsLoading: Boolean,
    storefrontErrorMessage: String?,
    nearbyStorefrontErrorMessage: String?,
    cityStorefrontErrorMessage: String?,
    onRetryStorefrontsClick: () -> Unit,
    onRetryNearbyStorefrontsClick: () -> Unit,
    onRetryCityStorefrontsClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onStorefrontClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isFiltered =
        searchQuery.isNotBlank() ||
                selectedCategoryId != null

    var selectedTab by rememberSaveable {
        mutableStateOf(DISCOVERY_TAB_NEARBY)
    }

    LaunchedEffect(selectedDeliveryAddressId) {
        if (selectedDeliveryAddressId == null) {
            selectedTab = DISCOVERY_TAB_NEARBY
        }
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text =
                if (isFiltered) {
                    "İşletmeler"
                } else {
                    "İşletmeleri Keşfet"
                },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = CustomerHomeColors.DeepOlive
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text =
                when {
                    searchQuery.isNotBlank() ->
                        "Aramanıza uygun işletmeler"

                    selectedCategoryId != null ->
                        "Seçili kategoride yemek sunan işletmeler"

                    else ->
                        "Yakınınızdaki veya şehrinizde öne çıkan ev mutfaklarını keşfedin"
                },
            style = MaterialTheme.typography.bodyMedium,
            color = CustomerHomeColors.TextMuted
        )

        if (!isFiltered) {
            Spacer(
                modifier = Modifier.height(14.dp)
            )

            DiscoveryTabs(
                selectedTab = selectedTab,
                onNearbyClick = {
                    selectedTab = DISCOVERY_TAB_NEARBY
                },
                onCityClick = {
                    selectedTab = DISCOVERY_TAB_CITY
                }
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        if (isFiltered) {
            NormalStorefrontContent(
                storefronts = storefronts,
                isLoading = isStorefrontsLoading,
                errorMessage = storefrontErrorMessage,
                categoryFiltered = selectedCategoryId != null,
                searchActive = searchQuery.isNotBlank(),
                onRetryClick = onRetryStorefrontsClick,
                onStorefrontClick = onStorefrontClick
            )

            return@Column
        }

        when (selectedTab) {
            DISCOVERY_TAB_CITY -> {
                CityStorefrontContent(
                    storefronts = cityStorefronts,
                    hasDeliveryAddress =
                        selectedDeliveryAddressId != null,
                    isLoading = isCityStorefrontsLoading,
                    errorMessage = cityStorefrontErrorMessage,
                    onRetryClick = onRetryCityStorefrontsClick,
                    onAddAddressClick = onAddAddressClick,
                    onStorefrontClick = onStorefrontClick
                )
            }

            else -> {
                NearbyStorefrontContent(
                    storefronts = nearbyStorefronts,
                    hasDeliveryAddress =
                        selectedDeliveryAddressId != null,
                    isLoading = isNearbyStorefrontsLoading,
                    errorMessage = nearbyStorefrontErrorMessage,
                    onRetryClick = onRetryNearbyStorefrontsClick,
                    onAddAddressClick = onAddAddressClick,
                    onShowCityClick = {
                        selectedTab = DISCOVERY_TAB_CITY
                    },
                    onStorefrontClick = onStorefrontClick
                )
            }
        }
    }
}

@Composable
private fun DiscoveryTabs(
    selectedTab: String,
    onNearbyClick: () -> Unit,
    onCityClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CustomerHomeColors.SurfaceSoft
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            DiscoveryTab(
                text = "Sana Yakın",
                selected =
                    selectedTab == DISCOVERY_TAB_NEARBY,
                onClick = onNearbyClick,
                modifier = Modifier.weight(1f)
            )

            DiscoveryTab(
                text = "Şehrimde",
                selected =
                    selectedTab == DISCOVERY_TAB_CITY,
                onClick = onCityClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DiscoveryTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier =
            modifier.clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(13.dp),
        color =
            if (selected) {
                CustomerHomeColors.DeepOlive
            } else {
                androidx.compose.ui.graphics.Color.Transparent
            }
    ) {
        Box(
            modifier =
                Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color =
                    if (selected) {
                        CustomerHomeColors.Surface
                    } else {
                        CustomerHomeColors.DeepOlive
                    }
            )
        }
    }
}

@Composable
private fun NormalStorefrontContent(
    storefronts: List<ProducerStorefrontSummaryResponse>,
    isLoading: Boolean,
    errorMessage: String?,
    categoryFiltered: Boolean,
    searchActive: Boolean,
    onRetryClick: () -> Unit,
    onStorefrontClick: (Int) -> Unit
) {
    when {
        isLoading -> {
            DiscoveryLoading()
        }

        errorMessage != null -> {
            DiscoveryError(
                message = errorMessage,
                onRetryClick = onRetryClick
            )
        }

        storefronts.isEmpty() -> {
            DiscoveryInfoCard(
                title = "İşletme bulunamadı",
                description =
                    when {
                        searchActive ->
                            "Aramanıza uygun işletme bulunamadı."

                        categoryFiltered ->
                            "Bu kategoride yemek sunan işletme bulunamadı."

                        else ->
                            "Şu anda gösterilebilecek işletme bulunmuyor."
                    }
            )
        }

        else -> {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                storefronts.forEach { storefront ->
                    CompactStorefrontCard(
                        businessName =
                            storefront.businessName,
                        businessImageUrl =
                            storefront.businessImageUrl,
                        rating =
                            storefront.rating,
                        district =
                            storefront.district,
                        city =
                            storefront.city,
                        foodCount =
                            storefront.availableFoodCount,
                        preparationMinutes =
                            storefront.minimumPreparationTimeMinutes,
                        distanceKm = null,
                        isCurrentlyOpen =
                            storefront.isCurrentlyOpen,
                        onClick = {
                            onStorefrontClick(
                                storefront.producerProfileId
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NearbyStorefrontContent(
    storefronts: List<NearbyProducerStorefrontResponse>,
    hasDeliveryAddress: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onShowCityClick: () -> Unit,
    onStorefrontClick: (Int) -> Unit
) {
    when {
        !hasDeliveryAddress -> {
            DiscoveryInfoCard(
                title = "Teslimat adresi gerekli",
                description =
                    "Yakınınızdaki işletmeleri görebilmek için teslimat adresi seçin veya yeni bir adres ekleyin.",
                actionText = "Adres Ekle",
                onActionClick = onAddAddressClick
            )
        }

        isLoading -> {
            DiscoveryLoading()
        }

        errorMessage != null -> {
            DiscoveryError(
                message = errorMessage,
                onRetryClick = onRetryClick
            )
        }

        storefronts.isEmpty() -> {
            DiscoveryInfoCard(
                title = "Yakınınızda işletme bulunamadı",
                description =
                    "Teslimat adresinize yakın uygun işletme bulunamadı. Şehrinizdeki diğer işletmelere göz atabilirsiniz.",
                actionText = "Şehrimdeki İşletmeler",
                onActionClick = onShowCityClick
            )
        }

        else -> {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                storefronts.forEach { storefront ->
                    CompactStorefrontCard(
                        businessName =
                            storefront.businessName,
                        businessImageUrl =
                            storefront.businessImageUrl,
                        rating =
                            storefront.rating,
                        district =
                            storefront.district,
                        city =
                            storefront.city,
                        foodCount =
                            storefront.availableFoodCount,
                        preparationMinutes =
                            storefront.minimumPreparationTimeMinutes,
                        distanceKm =
                            storefront.distanceKm,
                        isCurrentlyOpen =
                            storefront.isCurrentlyOpen,
                        onClick = {
                            onStorefrontClick(
                                storefront.producerProfileId
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CityStorefrontContent(
    storefronts: List<DiscoverProducerStorefrontResponse>,
    hasDeliveryAddress: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onStorefrontClick: (Int) -> Unit
) {
    when {
        !hasDeliveryAddress -> {
            DiscoveryInfoCard(
                title = "Teslimat adresi gerekli",
                description =
                    "Şehrinizdeki işletmeleri gösterebilmek için teslimat adresi seçin veya yeni bir adres ekleyin.",
                actionText = "Adres Ekle",
                onActionClick = onAddAddressClick
            )
        }

        isLoading -> {
            DiscoveryLoading()
        }

        errorMessage != null -> {
            DiscoveryError(
                message = errorMessage,
                onRetryClick = onRetryClick
            )
        }

        storefronts.isEmpty() -> {
            DiscoveryInfoCard(
                title = "Şehrinizde işletme bulunamadı",
                description =
                    "Seçili teslimat adresinizin şehrinde şu anda uygun işletme bulunmuyor."
            )
        }

        else -> {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                storefronts.forEach { storefront ->
                    CompactStorefrontCard(
                        businessName =
                            storefront.businessName,
                        businessImageUrl =
                            storefront.businessImageUrl,
                        rating =
                            storefront.rating,
                        district =
                            storefront.district,
                        city =
                            storefront.city,
                        foodCount =
                            storefront.availableFoodCount,
                        preparationMinutes =
                            storefront.minimumPreparationTimeMinutes,
                        distanceKm =
                            storefront.distanceKm,
                        isCurrentlyOpen =
                            storefront.isCurrentlyOpen,
                        onClick = {
                            onStorefrontClick(
                                storefront.producerProfileId
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactStorefrontCard(
    businessName: String,
    businessImageUrl: String?,
    rating: Double,
    district: String,
    city: String,
    foodCount: Int,
    preparationMinutes: Int?,
    distanceKm: Double?,
    isCurrentlyOpen: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),
        shape = RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isCurrentlyOpen) {
                        CustomerHomeColors.Surface
                    } else {
                        CustomerHomeColors.SurfaceSoft
                    }
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    if (isCurrentlyOpen) {
                        2.dp
                    } else {
                        0.dp
                    }
            )
    ) {
        Row(
            modifier =
                Modifier.padding(12.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Box {
                CompactStorefrontImage(
                    businessImageUrl =
                        businessImageUrl,
                    businessName =
                        businessName
                )

                if (!isCurrentlyOpen) {
                    Surface(
                        modifier =
                            Modifier
                                .matchParentSize(),
                        shape =
                            RoundedCornerShape(16.dp),
                        color =
                            CustomerHomeColors
                                .Surface
                                .copy(alpha = 0.55f)
                    ) {}
                }
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = businessName,
                        modifier = Modifier.weight(1f),
                        style =
                            MaterialTheme
                                .typography
                                .titleSmall,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            if (isCurrentlyOpen) {
                                CustomerHomeColors.Text
                            } else {
                                CustomerHomeColors.TextMuted
                            },
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            if (rating > 0.0) {
                                "★ ${
                                    String.format(
                                        Locale.US,
                                        "%.1f",
                                        rating
                                    )
                                }"
                            } else {
                                "Yeni"
                            },
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        color =
                            if (isCurrentlyOpen) {
                                CustomerHomeColors.Terracotta
                            } else {
                                CustomerHomeColors.TextMuted
                            },
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text =
                        listOf(
                            district,
                            city
                        )
                            .filter {
                                it.isNotBlank()
                            }
                            .joinToString(" • "),
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Row(
                        modifier =
                            Modifier.weight(1f),
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        DiscoveryMiniPill(
                            text = "$foodCount yemek"
                        )

                        preparationMinutes
                            ?.takeIf {
                                it > 0
                            }
                            ?.let { minutes ->
                                DiscoveryMiniPill(
                                    text = "$minutes dk"
                                )
                            }

                        distanceKm
                            ?.let { km ->
                                DiscoveryMiniPill(
                                    text =
                                        if (km < 1.0) {
                                            "${(km * 1000).toInt()} m"
                                        } else {
                                            "${
                                                String.format(
                                                    Locale.US,
                                                    "%.1f",
                                                    km
                                                )
                                            } km"
                                        }
                                )
                            }
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Surface(
                        shape =
                            RoundedCornerShape(50.dp),
                        color =
                            if (isCurrentlyOpen) {
                                CustomerHomeColors.OliveSoft
                            } else {
                                CustomerHomeColors.TerracottaSoft
                            }
                    ) {
                        Text(
                            text =
                                if (isCurrentlyOpen) {
                                    "Açık"
                                } else {
                                    "Kapalı"
                                },
                            modifier =
                                Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                ),
                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall,
                            color =
                                if (isCurrentlyOpen) {
                                    CustomerHomeColors.DeepOlive
                                } else {
                                    CustomerHomeColors.Terracotta
                                },
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                if (!isCurrentlyOpen) {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(10.dp),
                        color =
                            CustomerHomeColors
                                .TerracottaSoft
                                .copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = "Şu anda sipariş almıyor",
                            modifier =
                                Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 7.dp
                                ),
                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall,
                            color =
                                CustomerHomeColors.Terracotta,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactStorefrontImage(
    businessImageUrl: String?,
    businessName: String
) {
    val resolvedImageUrl =
        ApiConfig.resolveMediaUrl(
            businessImageUrl
        )

    Surface(
        modifier =
            Modifier
                .size(76.dp)
                .clip(
                    RoundedCornerShape(16.dp)
                ),
        shape =
            RoundedCornerShape(16.dp),
        color =
            CustomerHomeColors.SurfaceSoft
    ) {
        if (resolvedImageUrl.isNullOrBlank()) {
            Box(
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text =
                        businessName
                            .trim()
                            .take(1)
                            .uppercase(),
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        CustomerHomeColors
                            .DeepOlive
                )
            }
        } else {
            AsyncImage(
                model = resolvedImageUrl,
                contentDescription =
                    "$businessName işletme görseli",
                modifier =
                    Modifier.fillMaxWidth(),
                contentScale =
                    ContentScale.Crop
            )
        }
    }
}

@Composable
private fun DiscoveryMiniPill(
    text: String
) {
    Surface(
        shape =
            RoundedCornerShape(50.dp),
        color =
            CustomerHomeColors.SurfaceSoft
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
            style =
                MaterialTheme
                    .typography
                    .labelSmall,
            color =
                CustomerHomeColors.DeepOlive
        )
    }
}

@Composable
private fun DiscoveryLoading() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
        contentAlignment =
            Alignment.Center
    ) {
        CircularProgressIndicator(
            color =
                CustomerHomeColors.Terracotta
        )
    }
}

@Composable
private fun DiscoveryError(
    message: String,
    onRetryClick: () -> Unit
) {
    DiscoveryInfoCard(
        title = "İşletmeler yüklenemedi",
        description = message,
        actionText = "Tekrar Dene",
        onActionClick = onRetryClick
    )
}

@Composable
private fun DiscoveryInfoCard(
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color =
            CustomerHomeColors.SurfaceSoft
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme
                        .typography
                        .titleSmall,
                fontWeight =
                    FontWeight.Bold,
                color =
                    CustomerHomeColors.DeepOlive
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = description,
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    CustomerHomeColors.TextMuted
            )

            if (
                !actionText.isNullOrBlank() &&
                onActionClick != null
            ) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                TextButton(
                    onClick = onActionClick
                ) {
                    Text(
                        text = actionText,
                        color =
                            CustomerHomeColors
                                .Terracotta,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }
    }
}