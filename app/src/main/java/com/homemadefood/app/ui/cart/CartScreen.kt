package com.homemadefood.app.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.CartItemResponse
import com.homemadefood.app.ui.components.FoodImage
import java.util.Locale

@Composable
fun CartScreen(
    uiState: CartUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onIncreaseQuantityClick: (
        cartItemId: Int,
        currentQuantity: Int
    ) -> Unit,
    onDecreaseQuantityClick: (
        cartItemId: Int,
        currentQuantity: Int
    ) -> Unit,
    onRemoveItemClick: (Int) -> Unit,
    onClearCartClick: () -> Unit,
    onCreateOrderClick: () -> Unit,
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
            text = "Sepetim",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = onRetryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tekrar Dene")
                }
            }

            uiState.cart == null ||
                    uiState.cart.items.isEmpty() -> {

                if (!uiState.actionMessage.isNullOrBlank()) {
                    Text(
                        text = uiState.actionMessage,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sepetiniz şu anda boş.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                val cart = uiState.cart

                if (!uiState.actionMessage.isNullOrBlank()) {
                    Text(
                        text = uiState.actionMessage,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }

                if (cart.businessName.isNotBlank()) {
                    Text(
                        text = cart.businessName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = cart.items,
                        key = { item ->
                            item.cartItemId
                        }
                    ) { item ->
                        CartItemCard(
                            item = item,

                            isUpdating =
                                uiState.updatingCartItemId ==
                                        item.cartItemId,

                            onIncreaseClick = {
                                onIncreaseQuantityClick(
                                    item.cartItemId,
                                    item.quantity
                                )
                            },

                            onDecreaseClick = {
                                onDecreaseQuantityClick(
                                    item.cartItemId,
                                    item.quantity
                                )
                            },

                            onRemoveClick = {
                                onRemoveItemClick(
                                    item.cartItemId
                                )
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                HorizontalDivider()

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                CartSummaryRow(
                    title = "Toplam ürün",
                    value = "${cart.totalQuantity} adet"
                )

                CartSummaryRow(
                    title = "Toplam tutar",
                    value = formatPrice(cart.totalPrice)
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = onCreateOrderClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled =
                        !uiState.isClearingCart &&
                                uiState.updatingCartItemId == null
                ) {
                    Text("Sipariş Oluşturmaya Devam Et")
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Button(
                    onClick = onClearCartClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled =
                        !uiState.isClearingCart &&
                                uiState.updatingCartItemId == null
                ) {
                    if (uiState.isClearingCart) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Sepeti Temizle")
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItemResponse,
    isUpdating: Boolean,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                FoodImage(
                    imageUrl = item.imageUrl,
                    contentDescription =
                        item.foodName,
                    modifier = Modifier
                        .weight(0.32f)
                        .aspectRatio(1f)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                )

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(0.68f)
                ) {
                    Text(
                        text = item.foodName,
                        style =
                            MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Birim fiyat ${formatPrice(item.unitPrice)}",
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            formatPrice(item.lineTotal),
                        style =
                            MaterialTheme.typography.titleMedium,
                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }
            }

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
                OutlinedButton(
                    onClick = onDecreaseClick,
                    enabled = !isUpdating,
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                ) {
                    Text("−")
                }

                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.height(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text =
                            item.quantity.toString(),
                        style =
                            MaterialTheme.typography.titleLarge
                    )
                }

                OutlinedButton(
                    onClick = onIncreaseClick,
                    enabled =
                        !isUpdating &&
                                item.quantity < 50,
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                ) {
                    Text("+")
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            TextButton(
                onClick = onRemoveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating
            ) {
                Text(
                    text = "Sepetten Çıkar",
                    color =
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CartSummaryRow(
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