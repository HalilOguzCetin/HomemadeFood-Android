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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.homemadefood.app.data.model.PopularFoodResponse
import com.homemadefood.app.data.remote.ApiConfig
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CustomerHomePopularFoodSection(
    foods: List<PopularFoodResponse>,
    favoriteFoodIds: Set<Int>,
    isFavoritesLoading: Boolean,
    favoriteActionFoodId: Int?,
    favoriteErrorMessage: String?,
    onFoodClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (foods.isEmpty()) {
        return
    }

    Column(
        modifier = modifier
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
                    Modifier.weight(1f)
            ) {
                Text(
                    text = "Popüler Yemekler",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        CustomerHomeColors
                            .DeepOlive
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        "Son dönemde en çok ilgi gören lezzetler",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted
                )
            }

            Text(
                text = "Kaydır →",
                style =
                    MaterialTheme
                        .typography
                        .labelMedium,
                color =
                    CustomerHomeColors
                        .Terracotta,
                fontWeight =
                    FontWeight.SemiBold
            )
        }

        if (
            !favoriteErrorMessage
                .isNullOrBlank()
        ) {
            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    favoriteErrorMessage,
                style =
                    MaterialTheme
                        .typography
                        .labelSmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(
                    12.dp
                ),
            contentPadding =
                PaddingValues(
                    end = 6.dp
                )
        ) {
            items(
                items = foods,
                key = { food ->
                    food.id
                }
            ) { food ->
                val isFavorite =
                    favoriteFoodIds
                        .contains(
                            food.id
                        )

                val isFavoriteActionLoading =
                    favoriteActionFoodId ==
                            food.id

                PopularFoodCard(
                    food = food,
                    isFavorite =
                        isFavorite,
                    isFavoriteChecking =
                        isFavoritesLoading,
                    isFavoriteActionLoading =
                        isFavoriteActionLoading,
                    onFavoriteClick = {
                        onFavoriteClick(
                            food.id
                        )
                    },
                    onClick = {
                        onFoodClick(
                            food.id
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun PopularFoodCard(
    food: PopularFoodResponse,
    isFavorite: Boolean,
    isFavoriteChecking: Boolean,
    isFavoriteActionLoading: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .width(210.dp)
                .clickable(
                    onClick = onClick
                ),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    CustomerHomeColors
                        .Surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {
        Column {
            Box(
                modifier =
                    Modifier.fillMaxWidth()
            ) {
                PopularFoodImage(
                    imageUrl =
                        food.imageUrl,
                    foodName =
                        food.name
                )

                FavoriteHeartButton(
                    isFavorite =
                        isFavorite,
                    isChecking =
                        isFavoriteChecking,
                    isActionLoading =
                        isFavoriteActionLoading,
                    onClick =
                        onFavoriteClick,
                    modifier =
                        Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .padding(
                                10.dp
                            )
                )
            }

            Column(
                modifier =
                    Modifier.padding(
                        start = 13.dp,
                        end = 13.dp,
                        top = 11.dp,
                        bottom = 13.dp
                    )
            ) {
                Surface(
                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),
                    color =
                        CustomerHomeColors
                            .TerracottaSoft
                ) {
                    Text(
                        text = "Popüler",
                        modifier =
                            Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 4.dp
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
                        color =
                            CustomerHomeColors
                                .Terracotta,
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text = food.name,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        CustomerHomeColors
                            .Text,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        food.businessName,
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
                    modifier =
                        Modifier.height(10.dp)
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
                            "${food.preparationTimeMinutes} dk",
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        color =
                            CustomerHomeColors
                                .DeepOlive
                    )

                    Text(
                        text =
                            formatFoodPrice(
                                food.price
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .titleSmall,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            CustomerHomeColors
                                .Terracotta
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteHeartButton(
    isFavorite: Boolean,
    isChecking: Boolean,
    isActionLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading =
        isChecking ||
                isActionLoading

    Box(
        modifier =
            modifier
                .size(38.dp)
                .clip(
                    CircleShape
                )
                .background(
                    CustomerHomeColors
                        .Surface
                        .copy(
                            alpha = 0.94f
                        )
                )
                .clickable(
                    enabled =
                        !isLoading,
                    onClick =
                        onClick
                ),
        contentAlignment =
            Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier =
                    Modifier.size(
                        18.dp
                    ),
                strokeWidth =
                    2.dp,
                color =
                    CustomerHomeColors
                        .Terracotta
            )
        } else {
            Text(
                text =
                    if (isFavorite) {
                        "♥"
                    } else {
                        "♡"
                    },
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                color =
                    if (isFavorite) {
                        CustomerHomeColors
                            .Terracotta
                    } else {
                        CustomerHomeColors
                            .DeepOlive
                    },
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PopularFoodImage(
    imageUrl: String?,
    foodName: String
) {
    val resolvedImageUrl =
        ApiConfig.resolveMediaUrl(
            imageUrl
        )

    if (resolvedImageUrl == null) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(138.dp),
            contentAlignment =
                Alignment.Center
        ) {
            Text(
                text = "🍲",
                style =
                    MaterialTheme
                        .typography
                        .headlineLarge
            )
        }

        return
    }

    AsyncImage(
        model =
            resolvedImageUrl,
        contentDescription =
            "$foodName yemek görseli",
        modifier =
            Modifier
                .fillMaxWidth()
                .height(138.dp),
        contentScale =
            ContentScale.Crop
    )
}

private fun formatFoodPrice(
    price: Double
): String {
    val formatter =
        NumberFormat.getNumberInstance(
            Locale("tr", "TR")
        )

    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = 2

    return "${formatter.format(price)} ₺"
}