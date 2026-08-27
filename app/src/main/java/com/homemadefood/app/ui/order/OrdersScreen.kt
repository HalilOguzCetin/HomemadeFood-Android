package com.homemadefood.app.ui.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.OrderResponse
import com.homemadefood.app.data.model.OrderStatus
import com.homemadefood.app.ui.components.AppEmptyState
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.AppMessageType
import com.homemadefood.app.ui.customer.CustomerHomeColors
import com.homemadefood.app.ui.customer.CustomerHomeTheme
import java.util.Locale

private enum class OrderCategory(
    val title: String
) {
    ALL("Tümü"),
    WAITING("Bekliyor"),
    PREPARING("Hazırlanıyor"),
    ON_THE_WAY("Yolda"),
    DELIVERED("Teslim Edildi"),
    CANCELLED("İptal")
}

@Composable
fun OrdersScreen(
    uiState: OrdersUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onCancelOrderClick: (Int) -> Unit,
    onOrderClick: (Int) -> Unit,
    showBackButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    var orderIdWaitingForCancellation by remember {
        mutableStateOf<Int?>(null)
    }

    var selectedCategory by remember {
        mutableStateOf(OrderCategory.ALL)
    }

    val filteredOrders = remember(
        uiState.orders,
        selectedCategory
    ) {
        if (selectedCategory == OrderCategory.ALL) {
            uiState.orders
        } else {
            uiState.orders.filter { order ->
                orderMatchesCategory(
                    orderStatus = order.orderStatus,
                    category = selectedCategory
                )
            }
        }
    }

    if (orderIdWaitingForCancellation != null) {
        AlertDialog(
            onDismissRequest = {
                orderIdWaitingForCancellation = null
            },
            title = {
                Text("Siparişi İptal Et")
            },
            text = {
                Text(
                    "Bu siparişi iptal etmek istediğinizden emin misiniz?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val orderId =
                            orderIdWaitingForCancellation

                        orderIdWaitingForCancellation = null

                        if (orderId != null) {
                            onCancelOrderClick(orderId)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomerHomeColors.Error
                    )
                ) {
                    Text("Evet, İptal Et")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        orderIdWaitingForCancellation = null
                    }
                ) {
                    Text("Vazgeç")
                }
            }
        )
    }

    CustomerHomeTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(
                modifier = Modifier.height(18.dp)
            )

            if (showBackButton) {
                TextButton(
                    onClick = onBackClick,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 0.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text(
                        text = "←  Geri",
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            }

            Text(
                text = "Siparişlerim",
                style = MaterialTheme.typography.headlineMedium,
                color = CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Geçmiş ve aktif siparişlerinizi görüntüleyin",
                style = MaterialTheme.typography.bodyMedium,
                color = CustomerHomeColors.TextMuted
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            OrderCategoryBar(
                selectedCategory = selectedCategory,
                onCategoryClick = { category ->
                    selectedCategory = category
                }
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            when {
                uiState.isLoading -> {
                    AppLoadingState(
                        message = "Siparişler yükleniyor..."
                    )
                }

                uiState.errorMessage != null &&
                        uiState.orders.isEmpty() -> {
                    AppErrorState(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick
                    )
                }

                uiState.orders.isEmpty() -> {
                    AppEmptyState(
                        title = "Sipariş bulunmuyor",
                        message = "Henüz oluşturulmuş bir siparişiniz yok."
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

                    if (!uiState.errorMessage.isNullOrBlank()) {
                        AppInlineMessage(
                            message = uiState.errorMessage,
                            type = AppMessageType.Error
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )
                    }

                    if (filteredOrders.isEmpty()) {
                        AppEmptyState(
                            title = "Bu kategoride sipariş yok",
                            message = "${selectedCategory.title} kategorisinde henüz bir siparişiniz bulunmuyor."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = filteredOrders,
                                key = { order ->
                                    order.orderId
                                }
                            ) { order ->
                                OrderCard(
                                    order = order,
                                    isCancelling =
                                        uiState.cancellingOrderId ==
                                                order.orderId,
                                    onOrderClick = {
                                        onOrderClick(order.orderId)
                                    },
                                    onCancelClick = {
                                        orderIdWaitingForCancellation =
                                            order.orderId
                                    }
                                )
                            }

                            item {
                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCategoryBar(
    selectedCategory: OrderCategory,
    onCategoryClick: (OrderCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OrderCategory.entries.forEach { category ->
            val isSelected =
                selectedCategory == category

            Surface(
                modifier = Modifier.clickable {
                    onCategoryClick(category)
                },
                shape = RoundedCornerShape(50),
                color = if (isSelected) {
                    CustomerHomeColors.DeepOlive
                } else {
                    CustomerHomeColors.Cream
                },
                border = if (isSelected) {
                    null
                } else {
                    BorderStroke(
                        width = 1.dp,
                        color = CustomerHomeColors.Outline
                    )
                }
            ) {
                Text(
                    text = category.title,
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 10.dp
                    ),
                    color = if (isSelected) {
                        Color.White
                    } else {
                        CustomerHomeColors.DeepOlive
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderResponse,
    isCancelling: Boolean,
    onOrderClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val orderStatus = order.orderStatus

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOrderClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = CustomerHomeColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = CustomerHomeColors.Outline.copy(alpha = 0.75f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = order.businessName.ifBlank {
                            "İşletme"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "${formatOrderDate(order.createdAt)}  •  Sipariş #${order.orderId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomerHomeColors.TextMuted
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Text(
                    text = "›",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "${totalItemCount(order)} ürün",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomerHomeColors.TextMuted
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = order.deliveryAddressTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomerHomeColors.Olive,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = formatPrice(order.totalPrice),
                    style = MaterialTheme.typography.headlineSmall,
                    color = CustomerHomeColors.Terracotta,
                    fontWeight = FontWeight.Bold
                )
            }

            if (orderStatus == OrderStatus.PENDING) {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isCancelling,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomerHomeColors.TerracottaSoft,
                        contentColor = CustomerHomeColors.Terracotta
                    )
                ) {
                    if (isCancelling) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(22.dp),
                            strokeWidth = 2.dp,
                            color = CustomerHomeColors.Terracotta
                        )
                    } else {
                        Text(
                            text = "Siparişi İptal Et",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun orderMatchesCategory(
    orderStatus: OrderStatus,
    category: OrderCategory
): Boolean {
    return when (category) {
        OrderCategory.ALL -> true

        OrderCategory.WAITING ->
            orderStatus == OrderStatus.PENDING ||
                    orderStatus == OrderStatus.ACCEPTED

        OrderCategory.PREPARING ->
            orderStatus == OrderStatus.PREPARING ||
                    orderStatus == OrderStatus.READY

        OrderCategory.ON_THE_WAY ->
            orderStatus == OrderStatus.OUT_FOR_DELIVERY

        OrderCategory.DELIVERED ->
            orderStatus == OrderStatus.DELIVERED

        OrderCategory.CANCELLED ->
            orderStatus == OrderStatus.CANCELLED ||
                    orderStatus == OrderStatus.REJECTED
    }
}

private fun totalItemCount(
    order: OrderResponse
): Int {
    return order.items.sumOf { item ->
        item.quantity
    }
}

private fun formatOrderDate(
    createdAt: String
): String {
    val datePart = createdAt
        .substringBefore("T")
        .substringBefore(" ")

    val parts = datePart.split("-")

    return if (parts.size == 3) {
        "${parts[2]}.${parts[1]}.${parts[0]}"
    } else {
        createdAt
    }
}

private fun formatPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f ₺",
        price
    )
}
