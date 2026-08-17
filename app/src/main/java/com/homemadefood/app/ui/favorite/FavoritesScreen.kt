package com.homemadefood.app.ui.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import com.homemadefood.app.data.model.FavoriteResponse
import com.homemadefood.app.ui.components.AppEmptyState
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.AppMessageType
import com.homemadefood.app.ui.components.FoodImage
import java.util.Locale

@Composable
fun FavoritesScreen(
    uiState: FavoritesUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onRemoveFavoriteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        TextButton(
            onClick = onBackClick
        ) {
            Text("← Ana Sayfaya Dön")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Favorilerim",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        when {
            uiState.isLoading -> {
                AppLoadingState(
                    message = "Favoriler yükleniyor..."
                )
            }

            uiState.errorMessage != null -> {
                AppErrorState(
                    message = uiState.errorMessage,
                    onRetryClick = onRetryClick
                )
            }

            uiState.favorites.isEmpty() -> {
                AppEmptyState(
                    title = "Favori bulunmuyor",
                    message = "Henüz favoriye eklediğiniz bir yemek bulunmuyor."
                )
            }

            else -> {
                if (!uiState.actionMessage.isNullOrBlank()) {
                    AppInlineMessage(
                        message = uiState.actionMessage,
                        type = AppMessageType.Success
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.favorites,
                        key = { favorite ->
                            favorite.foodId
                        }
                    ) { favorite ->
                        FavoriteCard(
                            favorite = favorite,

                            isRemoving =
                                uiState.removingFoodId ==
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

@Composable
private fun FavoriteCard(
    favorite: FavoriteResponse,
    isRemoving: Boolean,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column {
            Box {
                FoodImage(
                    imageUrl = favorite.imageUrl,
                    contentDescription =
                        favorite.foodName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(
                            RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp
                            )
                        )
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(50.dp),
                    color =
                        if (favorite.isAvailable) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                ) {
                    Text(
                        text =
                            if (favorite.isAvailable) {
                                "Satışta"
                            } else {
                                "Satışta değil"
                            },
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        style =
                            MaterialTheme.typography.labelMedium,
                        color =
                            if (favorite.isAvailable) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = favorite.foodName,
                    style =
                        MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = favorite.businessName,
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color =
                            MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = favorite.categoryName,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            style =
                                MaterialTheme.typography.labelMedium
                        )
                    }

                    Text(
                        text = formatPrice(
                            favorite.price
                        ),
                        style =
                            MaterialTheme.typography.titleLarge,
                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                OutlinedButton(
                    onClick = onRemoveClick,
                    enabled = !isRemoving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isRemoving) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.height(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Favorilerden Çıkar")
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteInformationRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

private fun formatPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f TL",
        price
    )
}