package com.homemadefood.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.homemadefood.app.data.model.PopularProducerStorefrontResponse
import com.homemadefood.app.data.remote.ApiConfig
import java.util.Locale

@Composable
fun CustomerHomePopularStorefrontSection(
    storefronts: List<PopularProducerStorefrontResponse>,
    onStorefrontClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val rankedStorefronts =
        storefronts.take(6)

    if (rankedStorefronts.isEmpty()) {
        return
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(30.dp),
                    shape = CircleShape,
                    color = CustomerHomeColors.TerracottaSoft
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "★",
                            color = CustomerHomeColors.Terracotta,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(9.dp)
                )

                Column {
                    Text(
                        text = "Popüler İşletmeler",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CustomerHomeColors.DeepOlive
                    )

                    Text(
                        text = "Öne çıkan mutfakları keşfedin",
                        style = MaterialTheme.typography.bodySmall,
                        color = CustomerHomeColors.TextMuted
                    )
                }
            }

            Text(
                text = "Kaydır →",
                style = MaterialTheme.typography.labelMedium,
                color = CustomerHomeColors.Terracotta,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 6.dp)
        ) {
            items(
                items = rankedStorefronts,
                key = { storefront ->
                    storefront.producerProfileId
                }
            ) { storefront ->
                PopularStorefrontCard(
                    storefront = storefront,
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

@Composable
private fun PopularStorefrontCard(
    storefront: PopularProducerStorefrontResponse,
    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .width(252.dp)
                .clickable(
                    onClick = onClick
                ),
        shape = RoundedCornerShape(22.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = CustomerHomeColors.Surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {
        Column {
            PopularStorefrontImage(
                businessImageUrl = storefront.businessImageUrl,
                businessName = storefront.businessName
            )

            Column(
                modifier =
                    Modifier.padding(
                        start = 14.dp,
                        end = 14.dp,
                        top = 12.dp,
                        bottom = 13.dp
                    )
            ) {
                Text(
                    text = storefront.businessName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CustomerHomeColors.Text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text =
                            if (storefront.rating > 0.0) {
                                "★ ${formatPopularRating(storefront.rating)}"
                            } else {
                                "Yeni işletme"
                            },
                        style = MaterialTheme.typography.labelLarge,
                        color =
                            if (storefront.rating > 0.0) {
                                CustomerHomeColors.Gold
                            } else {
                                CustomerHomeColors.Terracotta
                            },
                        fontWeight = FontWeight.SemiBold
                    )

                    val location =
                        listOf(
                            storefront.district,
                            storefront.city
                        )
                            .filter {
                                it.isNotBlank()
                            }
                            .joinToString(" / ")

                    if (location.isNotBlank()) {
                        Text(
                            text = location,
                            modifier = Modifier.width(112.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomerHomeColors.TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    HomeStorefrontInfoPill(
                        text = "${storefront.availableFoodCount} yemek"
                    )

                    storefront
                        .minimumPreparationTimeMinutes
                        ?.let { minutes ->
                            HomeStorefrontInfoPill(
                                text = "$minutes dk"
                            )
                        }
                }
            }
        }
    }
}

@Composable
private fun PopularStorefrontImage(
    businessImageUrl: String?,
    businessName: String
) {
    val resolvedImageUrl =
        ApiConfig.resolveMediaUrl(
            businessImageUrl
        )

    if (resolvedImageUrl == null) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 22.dp,
                            topEnd = 22.dp
                        )
                    )
                    .background(
                        Brush.linearGradient(
                            colors =
                                listOf(
                                    CustomerHomeColors.OliveSoft,
                                    CustomerHomeColors.SurfaceSoft
                                )
                        )
                    ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text =
                    businessName
                        .trim()
                        .take(1)
                        .uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = CustomerHomeColors.DeepOlive
            )
        }

        return
    }

    AsyncImage(
        model = resolvedImageUrl,
        contentDescription = "$businessName işletme görseli",
        modifier =
            Modifier
                .fillMaxWidth()
                .height(128.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun HomeStorefrontInfoPill(
    text: String
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = CustomerHomeColors.OliveSoft
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = 9.dp,
                    vertical = 5.dp
                ),
            style = MaterialTheme.typography.labelSmall,
            color = CustomerHomeColors.DeepOlive,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatPopularRating(
    rating: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.1f",
        rating
    )
}