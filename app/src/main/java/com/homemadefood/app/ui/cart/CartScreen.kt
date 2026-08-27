package com.homemadefood.app.ui.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.CartItemResponse
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.FoodImage
import com.homemadefood.app.ui.customer.CustomerHomeColors
import com.homemadefood.app.ui.customer.CustomerHomeTheme
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
    CustomerHomeTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CustomerHomeColors.Cream)
        ) {
            CartHeader(
                showClearAction =
                    uiState.cart?.items?.isNotEmpty() == true,
                isClearEnabled =
                    !uiState.isClearingCart &&
                            uiState.updatingCartItemId == null,
                onBackClick = onBackClick,
                onClearCartClick = onClearCartClick
            )

            when {
                uiState.isLoading -> {
                    AppLoadingState(
                        modifier = Modifier.weight(1f),
                        message = "Sepetiniz yükleniyor..."
                    )
                }

                uiState.errorMessage != null -> {
                    AppErrorState(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                uiState.cart == null ||
                        uiState.cart.items.isEmpty() -> {
                    EmptyCartContent(
                        actionMessage = uiState.actionMessage,
                        onBackClick = onBackClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    val cart = uiState.cart

                    CartLoadedContent(
                        businessName = cart.businessName,
                        items = cart.items,
                        totalQuantity = cart.totalQuantity,
                        totalPrice = cart.totalPrice,
                        actionMessage = uiState.actionMessage,
                        updatingCartItemId =
                            uiState.updatingCartItemId,
                        isClearingCart = uiState.isClearingCart,
                        onIncreaseQuantityClick =
                            onIncreaseQuantityClick,
                        onDecreaseQuantityClick =
                            onDecreaseQuantityClick,
                        onRemoveItemClick = onRemoveItemClick,
                        onCreateOrderClick = onCreateOrderClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CartHeader(
    showClearAction: Boolean,
    isClearEnabled: Boolean,
    onBackClick: () -> Unit,
    onClearCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 14.dp,
                top = 16.dp,
                bottom = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable(onClick = onBackClick),
            shape = CircleShape,
            color = CustomerHomeColors.Surface,
            border = BorderStroke(
                1.dp,
                CustomerHomeColors.Outline
            ),
            shadowElevation = 2.dp
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    style = MaterialTheme.typography.titleLarge,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Sepetim",
                style = MaterialTheme.typography.headlineSmall,
                color = CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Siparişinizi gözden geçirin",
                style = MaterialTheme.typography.bodyMedium,
                color = CustomerHomeColors.TextMuted
            )
        }

        if (showClearAction) {
            TextButton(
                onClick = onClearCartClick,
                enabled = isClearEnabled
            ) {
                if (!isClearEnabled) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = CustomerHomeColors.Terracotta
                    )
                } else {
                    Text(
                        text = "Temizle",
                        color = CustomerHomeColors.Terracotta,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CartLoadedContent(
    businessName: String,
    items: List<CartItemResponse>,
    totalQuantity: Int,
    totalPrice: Double,
    actionMessage: String?,
    updatingCartItemId: Int?,
    isClearingCart: Boolean,
    onIncreaseQuantityClick: (
        cartItemId: Int,
        currentQuantity: Int
    ) -> Unit,
    onDecreaseQuantityClick: (
        cartItemId: Int,
        currentQuantity: Int
    ) -> Unit,
    onRemoveItemClick: (Int) -> Unit,
    onCreateOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (businessName.isNotBlank()) {
                item(
                    key = "cart_business"
                ) {
                    BusinessInfoCard(
                        businessName = businessName
                    )
                }
            }

            if (!actionMessage.isNullOrBlank()) {
                item(
                    key = "cart_action_message"
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CustomerHomeColors.OliveSoft
                    ) {
                        Text(
                            text = actionMessage,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = CustomerHomeColors.DeepOlive
                        )
                    }
                }
            }

            item(
                key = "cart_items_title"
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 6.dp,
                            bottom = 2.dp
                        ),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Sepetinizdeki Ürünler",
                        style = MaterialTheme.typography.titleLarge,
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$totalQuantity adet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomerHomeColors.TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            items(
                items = items,
                key = { item ->
                    item.cartItemId
                }
            ) { item ->
                CartItemCard(
                    item = item,
                    isUpdating =
                        updatingCartItemId ==
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

        CartBottomSummary(
            totalQuantity = totalQuantity,
            totalPrice = totalPrice,
            enabled =
                !isClearingCart &&
                        updatingCartItemId == null,
            onCreateOrderClick = onCreateOrderClick
        )
    }
}

@Composable
private fun BusinessInfoCard(
    businessName: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CustomerHomeColors.Surface,
        border = BorderStroke(
            1.dp,
            CustomerHomeColors.Outline
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = CustomerHomeColors.OliveSoft
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⌂",
                        style = MaterialTheme.typography.titleMedium,
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {
                Text(
                    text = "Sipariş verdiğiniz işletme",
                    style = MaterialTheme.typography.labelMedium,
                    color = CustomerHomeColors.TextMuted
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = businessName,
                    style = MaterialTheme.typography.titleMedium,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CustomerHomeColors.Surface
        ),
        border = BorderStroke(
            1.dp,
            CustomerHomeColors.Outline
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FoodImage(
                imageUrl = item.imageUrl,
                contentDescription = item.foodName,
                modifier = Modifier
                    .size(96.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.foodName,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    TextButton(
                        onClick = onRemoveClick,
                        enabled = !isUpdating,
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 0.dp
                        )
                    ) {
                        Text(
                            text = "Kaldır",
                            color = CustomerHomeColors.Terracotta,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                if (!item.isAvailable) {
                    Text(
                        text = "Şu anda satışta değil",
                        style = MaterialTheme.typography.bodySmall,
                        color = CustomerHomeColors.Error,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )
                }

                Text(
                    text = "${formatPrice(item.unitPrice)} / adet",
                    style = MaterialTheme.typography.bodySmall,
                    color = CustomerHomeColors.TextMuted
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatPrice(item.lineTotal),
                        style = MaterialTheme.typography.titleLarge,
                        color = CustomerHomeColors.Terracotta,
                        fontWeight = FontWeight.Bold
                    )

                    QuantitySelector(
                        quantity = item.quantity,
                        isUpdating = isUpdating,
                        decreaseEnabled =
                            !isUpdating,
                        increaseEnabled =
                            !isUpdating &&
                                    item.isAvailable &&
                                    item.quantity < 50,
                        onDecreaseClick =
                            onDecreaseClick,
                        onIncreaseClick =
                            onIncreaseClick
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    isUpdating: Boolean,
    decreaseEnabled: Boolean,
    increaseEnabled: Boolean,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CustomerHomeColors.SurfaceSoft,
        border = BorderStroke(
            1.dp,
            CustomerHomeColors.Outline
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuantityAction(
                text = "−",
                enabled = decreaseEnabled,
                onClick = onDecreaseClick
            )

            Box(
                modifier = Modifier.width(36.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = CustomerHomeColors.DeepOlive
                    )
                } else {
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = CustomerHomeColors.Text,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            QuantityAction(
                text = "+",
                enabled = increaseEnabled,
                onClick = onIncreaseClick
            )
        }
    }
}

@Composable
private fun QuantityAction(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = if (enabled) {
                CustomerHomeColors.DeepOlive
            } else {
                CustomerHomeColors.TextMuted.copy(
                    alpha = 0.45f
                )
            },
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CartBottomSummary(
    totalQuantity: Int,
    totalPrice: Double,
    enabled: Boolean,
    onCreateOrderClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CustomerHomeColors.Surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 14.dp,
                bottom = 16.dp
            )
        ) {
            CartSummaryRow(
                title = "Toplam ürün",
                value = "$totalQuantity adet"
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            HorizontalDivider(
                color = CustomerHomeColors.Outline
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Toplam",
                    style = MaterialTheme.typography.titleLarge,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatPrice(totalPrice),
                    style = MaterialTheme.typography.headlineSmall,
                    color = CustomerHomeColors.Terracotta,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Button(
                onClick = onCreateOrderClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = enabled,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomerHomeColors.Terracotta,
                    contentColor = CustomerHomeColors.Surface,
                    disabledContainerColor =
                        CustomerHomeColors.Terracotta.copy(
                            alpha = 0.45f
                        )
                )
            ) {
                Text(
                    text = "Sipariş Oluşturmaya Devam Et",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = CustomerHomeColors.TextMuted
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = CustomerHomeColors.Text,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyCartContent(
    actionMessage: String?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(86.dp),
            shape = CircleShape,
            color = CustomerHomeColors.OliveSoft
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🛒",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Sepetiniz şu anda boş",
            style = MaterialTheme.typography.titleLarge,
            color = CustomerHomeColors.DeepOlive,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = actionMessage
                ?: "Beğendiğiniz ev yemeklerini sepete ekleyerek devam edebilirsiniz.",
            style = MaterialTheme.typography.bodyMedium,
            color = CustomerHomeColors.TextMuted
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = onBackClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomerHomeColors.DeepOlive,
                contentColor = CustomerHomeColors.Surface
            )
        ) {
            Text(
                text = "Yemeklere Dön",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun formatPrice(
    price: Double
): String {
    val value = String.format(
        Locale("tr", "TR"),
        "%.2f",
        price
    )
        .removeSuffix(",00")

    return "$value ₺"
}