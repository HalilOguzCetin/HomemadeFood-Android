package com.homemadefood.app.ui.favorite

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.FavoriteResponse
import com.homemadefood.app.ui.components.AppEmptyState
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.AppMessageType
import com.homemadefood.app.ui.components.FoodImage
import com.homemadefood.app.ui.customer.CustomerHomeColors
import com.homemadefood.app.ui.customer.CustomerHomeTheme
import java.util.Locale

@Composable
fun FavoritesScreen(
    uiState: FavoritesUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onRemoveFavoriteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    CustomerHomeColors.Cream
                )
        ) {
            FavoritesHeader(
                favoriteCount =
                    uiState.favorites.size,
                onBackClick = onBackClick
            )

            when {
                uiState.isLoading -> {
                    AppLoadingState(
                        message =
                            "Favorileriniz yükleniyor...",
                        modifier =
                            Modifier.weight(1f)
                    )
                }

                uiState.errorMessage != null &&
                        uiState.favorites.isEmpty() -> {
                    AppErrorState(
                        message =
                            uiState.errorMessage,
                        onRetryClick =
                            onRetryClick,
                        modifier =
                            Modifier.weight(1f)
                    )
                }

                uiState.favorites.isEmpty() -> {
                    AppEmptyState(
                        title =
                            "Favori yemeğiniz yok",
                        message =
                            "Beğendiğiniz yemekleri favoriye eklediğinizde burada görebilirsiniz.",
                        modifier =
                            Modifier.weight(1f)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize(),
                        contentPadding =
                            PaddingValues(
                                start = 20.dp,
                                end = 20.dp,
                                top = 4.dp,
                                bottom = 24.dp
                            ),
                        verticalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {
                        if (
                            !uiState.actionMessage
                                .isNullOrBlank()
                        ) {
                            item(
                                key =
                                    "favorites_action_message"
                            ) {
                                AppInlineMessage(
                                    message =
                                        uiState
                                            .actionMessage,
                                    type =
                                        AppMessageType
                                            .Success
                                )
                            }
                        }

                        if (
                            !uiState.errorMessage
                                .isNullOrBlank()
                        ) {
                            item(
                                key =
                                    "favorites_inline_error"
                            ) {
                                AppInlineMessage(
                                    message =
                                        uiState
                                            .errorMessage,
                                    type =
                                        AppMessageType
                                            .Error
                                )
                            }
                        }

                        item(
                            key =
                                "favorites_list_intro"
                        ) {
                            FavoritesIntroCard(
                                count =
                                    uiState
                                        .favorites
                                        .size
                            )
                        }

                        items(
                            items =
                                uiState.favorites,
                            key = { favorite ->
                                favorite.foodId
                            }
                        ) { favorite ->
                            FavoriteFoodCard(
                                favorite =
                                    favorite,
                                isRemoving =
                                    uiState
                                        .removingFoodId ==
                                            favorite.foodId,
                                onRemoveClick = {
                                    onRemoveFavoriteClick(
                                        favorite.foodId
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritesHeader(
    favoriteCount: Int,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 14.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            contentPadding =
                PaddingValues(
                    horizontal = 0.dp,
                    vertical = 4.dp
                )
        ) {
            Text(
                text = "←  Hesabıma Dön",
                color =
                    CustomerHomeColors
                        .DeepOlive,
                fontWeight =
                    FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Favorilerim",
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        if (favoriteCount == 0) {
                            "Beğendiğiniz yemekleri saklayın"
                        } else {
                            "$favoriteCount favori yemek"
                        },
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        CustomerHomeColors
                            .TextMuted
                )
            }

            Surface(
                modifier =
                    Modifier.size(44.dp),
                shape = CircleShape,
                color =
                    CustomerHomeColors
                        .TerracottaSoft
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center
                ) {
                    Text(
                        text = "♥",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        color =
                            CustomerHomeColors
                                .Terracotta,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesIntroCard(
    count: Int
) {
    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        color =
            CustomerHomeColors
                .OliveSoft
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement =
                Arrangement.spacedBy(11.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                modifier =
                    Modifier.size(40.dp),
                shape = CircleShape,
                color =
                    CustomerHomeColors
                        .DeepOlive
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        color = Color.White,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {
                Text(
                    text =
                        "Kaydettiğiniz Lezzetler",
                    style =
                        MaterialTheme
                            .typography
                            .titleSmall,
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        "Favoriden çıkardığınız yemek bu listeden kaldırılır.",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted
                )
            }
        }
    }
}

@Composable
private fun FavoriteFoodCard(
    favorite: FavoriteResponse,
    isRemoving: Boolean,
    onRemoveClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(22.dp),
        color =
            CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    CustomerHomeColors
                        .Outline
            )
    ) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 12.dp,
                        end = 14.dp,
                        top = 12.dp,
                        bottom = 10.dp
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        13.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Box {
                    FoodImage(
                        imageUrl =
                            favorite.imageUrl,
                        contentDescription =
                            favorite.foodName,
                        modifier = Modifier
                            .size(108.dp)
                            .clip(
                                RoundedCornerShape(
                                    17.dp
                                )
                            )
                    )

                    FavoriteAvailabilityBadge(
                        isAvailable =
                            favorite.isAvailable,
                        modifier = Modifier
                            .align(
                                Alignment
                                    .BottomStart
                            )
                            .padding(6.dp)
                    )
                }

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {
                    Text(
                        text =
                            favorite.foodName,
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        color =
                            CustomerHomeColors.Text,
                        fontWeight =
                            FontWeight.Bold,
                        maxLines = 2,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            favorite.businessName,
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            CustomerHomeColors
                                .DeepOlive,
                        fontWeight =
                            FontWeight.SemiBold,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier =
                            Modifier.height(9.dp)
                    )

                    Surface(
                        shape =
                            RoundedCornerShape(
                                50.dp
                            ),
                        color =
                            CustomerHomeColors
                                .SurfaceSoft
                    ) {
                        Text(
                            text =
                                favorite
                                    .categoryName,
                            modifier =
                                Modifier.padding(
                                    horizontal =
                                        10.dp,
                                    vertical =
                                        5.dp
                                ),
                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium,
                            color =
                                CustomerHomeColors
                                    .TextMuted,
                            fontWeight =
                                FontWeight
                                    .Medium,
                            maxLines = 1,
                            overflow =
                                TextOverflow
                                    .Ellipsis
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            formatFavoritePrice(
                                favorite.price
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        color =
                            CustomerHomeColors
                                .Terracotta,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                color =
                    CustomerHomeColors
                        .SurfaceSoft
            ) {
                OutlinedButton(
                    onClick =
                        onRemoveClick,
                    enabled =
                        !isRemoving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 10.dp
                        )
                        .height(44.dp),
                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),
                    border =
                        BorderStroke(
                            width = 1.dp,
                            color =
                                CustomerHomeColors
                                    .Terracotta
                                    .copy(
                                        alpha =
                                            0.35f
                                    )
                        )
                ) {
                    if (isRemoving) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(
                                    19.dp
                                ),
                            strokeWidth = 2.dp,
                            color =
                                CustomerHomeColors
                                    .Terracotta
                        )
                    } else {
                        Text(
                            text =
                                "♥  Favorilerden Çıkar",
                            color =
                                CustomerHomeColors
                                    .Terracotta,
                            fontWeight =
                                FontWeight
                                    .SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteAvailabilityBadge(
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape =
            RoundedCornerShape(50.dp),
        color =
            if (isAvailable) {
                CustomerHomeColors
                    .OliveSoft
                    .copy(alpha = 0.95f)
            } else {
                CustomerHomeColors
                    .TerracottaSoft
                    .copy(alpha = 0.95f)
            }
    ) {
        Text(
            text =
                if (isAvailable) {
                    "Satışta"
                } else {
                    "Satışta değil"
                },
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
                if (isAvailable) {
                    CustomerHomeColors
                        .DeepOlive
                } else {
                    CustomerHomeColors
                        .Error
                },
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatFavoritePrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f ₺",
        price
    )
}