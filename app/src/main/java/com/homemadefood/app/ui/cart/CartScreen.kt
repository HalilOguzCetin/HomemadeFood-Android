package com.homemadefood.app.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.CartItemResponse
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.foodName,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            CartSummaryRow(
                title = "Birim fiyat",
                value = formatPrice(item.unitPrice)
            )

            CartSummaryRow(
                title = "Ara toplam",
                value = formatPrice(item.lineTotal)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Button(
                    onClick = onDecreaseClick,
                    enabled = !isUpdating
                ) {
                    Text("-")
                }

                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = item.quantity.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Button(
                    onClick = onIncreaseClick,
                    enabled =
                        !isUpdating &&
                                item.quantity < 50
                ) {
                    Text("+")
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = onRemoveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating
            ) {
                Text("Ürünü Sepetten Çıkar")
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