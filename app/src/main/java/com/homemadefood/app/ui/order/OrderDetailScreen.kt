package com.homemadefood.app.ui.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.OrderStatus
import com.homemadefood.app.data.model.PaymentMethods
import com.homemadefood.app.data.model.ReviewResponse
import com.homemadefood.app.ui.customer.CustomerHomeColors
import com.homemadefood.app.ui.customer.CustomerHomeTheme
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun OrderDetailScreen(
    uiState: OrderDetailUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onCancelOrderClick: () -> Unit,
    onShowReviewFormClick: () -> Unit,
    onHideReviewFormClick: () -> Unit,
    onRatingSelected: (Int) -> Unit,
    onReviewCommentChange: (String) -> Unit,
    onSubmitReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        var showCancelDialog by remember {
            mutableStateOf(false)
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCancelDialog = false
                },
                title = {
                    Text(
                        text = "Siparişi İptal Et",
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Bu siparişi iptal etmek istediğinizden emin misiniz?",
                        color = CustomerHomeColors.Text
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showCancelDialog = false
                            onCancelOrderClick()
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
                            showCancelDialog = false
                        }
                    ) {
                        Text(
                            text = "Vazgeç",
                            color = CustomerHomeColors.DeepOlive
                        )
                    }
                }
            )
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(CustomerHomeColors.Cream),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = CustomerHomeColors.DeepOlive
                    )
                }
            }

            uiState.order == null -> {
                OrderDetailErrorState(
                    message = uiState.errorMessage
                        ?: "Sipariş bilgisi bulunamadı.",
                    onBackClick = onBackClick,
                    onRetryClick = onRetryClick,
                    modifier = modifier
                )
            }

            else -> {
                val order = uiState.order
                val orderStatus = order.orderStatus

                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(CustomerHomeColors.Cream)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 20.dp,
                                    end = 20.dp,
                                    top = 16.dp,
                                    bottom = 30.dp
                                )
                        ) {
                            OrderDetailTopBar(
                                onBackClick = onBackClick,
                                enabled = !uiState.isCancelling
                            )

                            Spacer(
                                modifier = Modifier.height(22.dp)
                            )

                            OrderIdentityCard(
                                orderId = order.orderId,
                                createdAt = order.createdAt,
                                statusUpdatedAt = order.statusUpdatedAt
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            OrderStatusBanner(
                                status = orderStatus
                            )

                            if (!uiState.actionMessage.isNullOrBlank()) {
                                Spacer(
                                    modifier = Modifier.height(14.dp)
                                )

                                MessageCard(
                                    text = uiState.actionMessage,
                                    isError = false
                                )
                            }

                            if (!uiState.errorMessage.isNullOrBlank()) {
                                Spacer(
                                    modifier = Modifier.height(14.dp)
                                )

                                MessageCard(
                                    text = uiState.errorMessage,
                                    isError = true
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            BusinessSummaryCard(
                                businessName = order.businessName,
                                suitabilityScore = if (
                                    order.recommendationSearchId != null
                                ) {
                                    order.suitabilityScore
                                } else {
                                    null
                                }
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            OrderItemsCard(
                                items = order.items,
                                totalPrice = order.totalPrice
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            AddressCard(
                                title = order.deliveryAddressTitle,
                                address = order.deliveryAddress,
                                latitude = order.deliveryLatitude,
                                longitude = order.deliveryLongitude
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            OrderExtraInformationCard(
                                customerNote = order.customerNote,
                                paymentMethod = order.paymentMethod
                            )

                            if (orderStatus == OrderStatus.DELIVERED) {
                                Spacer(
                                    modifier = Modifier.height(18.dp)
                                )

                                ReviewSection(
                                    uiState = uiState,
                                    onRetryClick = onRetryClick,
                                    onShowReviewFormClick = onShowReviewFormClick,
                                    onHideReviewFormClick = onHideReviewFormClick,
                                    onRatingSelected = onRatingSelected,
                                    onReviewCommentChange = onReviewCommentChange,
                                    onSubmitReviewClick = onSubmitReviewClick
                                )
                            }

                            if (orderStatus == OrderStatus.PENDING) {
                                Spacer(
                                    modifier = Modifier.height(18.dp)
                                )

                                OutlinedButton(
                                    onClick = {
                                        showCancelDialog = true
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    enabled = !uiState.isCancelling,
                                    border = BorderStroke(
                                        1.dp,
                                        CustomerHomeColors.Error
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = CustomerHomeColors.Error
                                    )
                                ) {
                                    if (uiState.isCancelling) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            strokeWidth = 2.dp,
                                            color = CustomerHomeColors.Error
                                        )
                                    } else {
                                        Text(
                                            text = "Siparişi İptal Et",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text = "Yalnızca onay bekleyen siparişler iptal edilebilir.",
                                    modifier = Modifier.fillMaxWidth(),
                                    color = CustomerHomeColors.TextMuted,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
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
private fun OrderDetailTopBar(
    onBackClick: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            onClick = onBackClick,
            enabled = enabled,
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = CustomerHomeColors.Surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Box(
                modifier = Modifier.size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sipariş Detayı",
                color = CustomerHomeColors.DeepOlive,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Siparişinizle ilgili tüm bilgiler",
                color = CustomerHomeColors.TextMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
private fun OrderIdentityCard(
    orderId: Int,
    createdAt: String,
    statusUpdatedAt: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = CustomerHomeColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Sipariş #$orderId",
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Oluşturulma: ${formatOrderDate(createdAt)}",
                    color = CustomerHomeColors.TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Son güncelleme: ${formatOrderDate(statusUpdatedAt)}",
                    color = CustomerHomeColors.TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = CustomerHomeColors.OliveSoft,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#",
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OrderStatusBanner(
    status: OrderStatus
) {
    val style = statusVisualStyle(status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = style.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = style.accent.copy(alpha = 0.14f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = style.symbol,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = status.displayName,
                    color = style.accent,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = style.description,
                    color = CustomerHomeColors.Text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MessageCard(
    text: String,
    isError: Boolean
) {
    val background = if (isError) {
        CustomerHomeColors.TerracottaSoft
    } else {
        CustomerHomeColors.OliveSoft
    }

    val foreground = if (isError) {
        CustomerHomeColors.Error
    } else {
        CustomerHomeColors.DeepOlive
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(14.dp),
            color = foreground,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun BusinessSummaryCard(
    businessName: String,
    suitabilityScore: Double?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = CustomerHomeColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        CustomerHomeColors.OliveSoft,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "İ",
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = "Hazırlayan İşletme",
                    color = CustomerHomeColors.TextMuted,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = businessName,
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (suitabilityScore != null) {
                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Uygunluk puanı: ${String.format(Locale("tr", "TR"), "%.2f", suitabilityScore)}",
                        color = CustomerHomeColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItemsCard(
    items: List<com.homemadefood.app.data.model.OrderItemResponse>,
    totalPrice: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = CustomerHomeColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sipariş Edilen Ürünler",
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${items.sumOf { it.quantity }} ürün",
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .background(
                            CustomerHomeColors.OliveSoft,
                            RoundedCornerShape(50)
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                CustomerHomeColors.SurfaceSoft,
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${item.quantity}x",
                            color = CustomerHomeColors.DeepOlive,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = item.foodName,
                            color = CustomerHomeColors.Text,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = "${formatOrderPrice(item.unitPrice)} × ${item.quantity} adet",
                            color = CustomerHomeColors.TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Text(
                        text = formatOrderPrice(item.totalPrice),
                        color = CustomerHomeColors.Terracotta,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (index < items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = CustomerHomeColors.Outline
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 14.dp),
                color = CustomerHomeColors.Outline
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Toplam Tutar",
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatOrderPrice(totalPrice),
                    color = CustomerHomeColors.Terracotta,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AddressCard(
    title: String,
    address: String,
    latitude: Double,
    longitude: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = CustomerHomeColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        CustomerHomeColors.OliveSoft,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌖",
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = "Teslimat Adresi",
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = title,
                    color = CustomerHomeColors.Text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = address,
                    color = CustomerHomeColors.TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Konum: $latitude, $longitude",
                    color = CustomerHomeColors.TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun OrderExtraInformationCard(
    customerNote: String,
    paymentMethod: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = CustomerHomeColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Ödeme Yöntemi",
                color = CustomerHomeColors.DeepOlive,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = translatePaymentMethod(paymentMethod),
                color = CustomerHomeColors.Text,
                style = MaterialTheme.typography.bodyLarge
            )

            if (customerNote.isNotBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    color = CustomerHomeColors.Outline
                )

                Text(
                    text = "Sipariş Notu",
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = customerNote,
                    color = CustomerHomeColors.TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun OrderDetailErrorState(
    message: String,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomerHomeColors.Cream)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sipariş bilgisine ulaşılamadı",
            color = CustomerHomeColors.DeepOlive,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = message,
            color = CustomerHomeColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Button(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomerHomeColors.DeepOlive
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Tekrar Dene")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Siparişlerime Dön",
                color = CustomerHomeColors.DeepOlive
            )
        }
    }
}

private data class OrderStatusVisualStyle(
    val accent: Color,
    val background: Color,
    val symbol: String,
    val description: String
)

private fun statusVisualStyle(
    status: OrderStatus
): OrderStatusVisualStyle = when (status) {
    OrderStatus.PENDING -> OrderStatusVisualStyle(
        accent = CustomerHomeColors.Gold,
        background = Color(0xFFFFF7E4),
        symbol = "◷",
        description = "Siparişiniz üreticinin onayını bekliyor."
    )

    OrderStatus.ACCEPTED -> OrderStatusVisualStyle(
        accent = CustomerHomeColors.DeepOlive,
        background = CustomerHomeColors.OliveSoft,
        symbol = "✓",
        description = "Siparişiniz üretici tarafından kabul edildi."
    )

    OrderStatus.PREPARING -> OrderStatusVisualStyle(
        accent = CustomerHomeColors.Terracotta,
        background = CustomerHomeColors.TerracottaSoft,
        symbol = "◷",
        description = "Siparişiniz hazırlanıyor."
    )

    OrderStatus.READY -> OrderStatusVisualStyle(
        accent = CustomerHomeColors.DeepOlive,
        background = CustomerHomeColors.OliveSoft,
        symbol = "✓",
        description = "Siparişiniz hazırlandı."
    )

    OrderStatus.OUT_FOR_DELIVERY -> OrderStatusVisualStyle(
        accent = CustomerHomeColors.Gold,
        background = Color(0xFFFFF7E4),
        symbol = "→",
        description = "Siparişiniz teslimat için yola çıktı."
    )

    OrderStatus.DELIVERED -> OrderStatusVisualStyle(
        accent = CustomerHomeColors.DeepOlive,
        background = CustomerHomeColors.OliveSoft,
        symbol = "✓",
        description = "Siparişiniz teslim edildi."
    )

    OrderStatus.REJECTED -> OrderStatusVisualStyle(
        accent = CustomerHomeColors.Error,
        background = Color(0xFFFFECE8),
        symbol = "×",
        description = "Siparişiniz üretici tarafından reddedildi."
    )

    OrderStatus.CANCELLED -> OrderStatusVisualStyle(
        accent = CustomerHomeColors.Error,
        background = Color(0xFFFFECE8),
        symbol = "×",
        description = "Siparişiniz iptal edildi."
    )

    OrderStatus.UNKNOWN -> OrderStatusVisualStyle(
        accent = CustomerHomeColors.TextMuted,
        background = CustomerHomeColors.SurfaceSoft,
        symbol = "?",
        description = "Sipariş durumu güncelleniyor."
    )
}

private fun translatePaymentMethod(
    paymentMethod: String
): String = PaymentMethods.displayName(
    paymentMethod
)

private fun formatOrderPrice(
    price: Double
): String = String.format(
    Locale("tr", "TR"),
    "%.2f ₺",
    price
)

private fun formatOrderDate(
    value: String
): String {
    val formatter = DateTimeFormatter.ofPattern(
        "dd.MM.yyyy HH:mm",
        Locale("tr", "TR")
    )

    return runCatching {
        OffsetDateTime
            .parse(value)
            .format(formatter)
    }.recoverCatching {
        LocalDateTime
            .parse(value)
            .format(formatter)
    }.getOrElse {
        value
    }
}

@Composable
private fun ReviewSection(
    uiState: OrderDetailUiState,
    onRetryClick: () -> Unit,
    onShowReviewFormClick: () -> Unit,
    onHideReviewFormClick: () -> Unit,
    onRatingSelected: (Int) -> Unit,
    onReviewCommentChange: (String) -> Unit,
    onSubmitReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = CustomerHomeColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Sipariş Değerlendirmesi",
                color = CustomerHomeColors.DeepOlive,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            when {
                uiState.isReviewStatusLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        ),
                        color = CustomerHomeColors.DeepOlive
                    )
                }

                !uiState.hasCheckedReview -> {
                    Text(
                        text = "Değerlendirme durumu kontrol edilemedi.",
                        color = CustomerHomeColors.Error
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Button(
                        onClick = onRetryClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomerHomeColors.DeepOlive
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Tekrar Kontrol Et")
                    }
                }

                uiState.existingReview != null -> {
                    ExistingReviewContent(
                        review = uiState.existingReview
                    )
                }

                uiState.isReviewFormVisible -> {
                    ReviewFormContent(
                        selectedRating = uiState.selectedRating,
                        comment = uiState.reviewComment,
                        isSubmitting = uiState.isSubmittingReview,
                        onRatingSelected = onRatingSelected,
                        onCommentChange = onReviewCommentChange,
                        onSubmitClick = onSubmitReviewClick,
                        onCancelClick = onHideReviewFormClick
                    )
                }

                else -> {
                    Text(
                        text = "Siparişiniz teslim edildi. Deneyiminizi paylaşarak üreticiyi değerlendirebilirsiniz.",
                        color = CustomerHomeColors.TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Button(
                        onClick = onShowReviewFormClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomerHomeColors.Terracotta
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "Değerlendir / Yorum Yap",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExistingReviewContent(
    review: ReviewResponse
) {
    Text(
        text = buildRatingStars(
            review.rating
        ),
        style = MaterialTheme.typography.headlineSmall,
        color = CustomerHomeColors.Gold
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    Text(
        text = if (review.comment.isBlank()) {
            "Yorum yazılmadı."
        } else {
            review.comment
        },
        color = CustomerHomeColors.Text,
        style = MaterialTheme.typography.bodyLarge
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    Text(
        text = "Değerlendirme tarihi: ${formatOrderDate(review.createdAt)}",
        color = CustomerHomeColors.TextMuted,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun ReviewFormContent(
    selectedRating: Int,
    comment: String,
    isSubmitting: Boolean,
    onRatingSelected: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Text(
        text = "Puanınız",
        color = CustomerHomeColors.DeepOlive,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (1..5).forEach { rating ->
            TextButton(
                onClick = {
                    onRatingSelected(rating)
                },
                enabled = !isSubmitting
            ) {
                Text(
                    text = if (rating <= selectedRating) {
                        "★"
                    } else {
                        "☆"
                    },
                    color = CustomerHomeColors.Gold,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }

    Text(
        text = if (selectedRating == 0) {
            "Henüz puan seçilmedi."
        } else {
            "$selectedRating / 5 puan"
        },
        color = CustomerHomeColors.TextMuted,
        style = MaterialTheme.typography.bodySmall
    )

    Spacer(
        modifier = Modifier.height(12.dp)
    )

    OutlinedTextField(
        value = comment,
        onValueChange = onCommentChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("Yorumunuz")
        },
        placeholder = {
            Text(
                "Sipariş ve üretici hakkındaki düşüncelerinizi yazabilirsiniz."
            )
        },
        supportingText = {
            Text("${comment.length}/1000")
        },
        minLines = 3,
        maxLines = 6,
        enabled = !isSubmitting,
        shape = RoundedCornerShape(16.dp)
    )

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    Button(
        onClick = onSubmitClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = !isSubmitting && selectedRating in 1..5,
        colors = ButtonDefaults.buttonColors(
            containerColor = CustomerHomeColors.Terracotta
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else {
            Text(
                text = "Değerlendirmeyi Gönder",
                fontWeight = FontWeight.Bold
            )
        }
    }

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    TextButton(
        onClick = onCancelClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isSubmitting
    ) {
        Text(
            text = "Vazgeç",
            color = CustomerHomeColors.DeepOlive
        )
    }
}

private fun buildRatingStars(
    rating: Int
): String {
    val safeRating = rating.coerceIn(
        minimumValue = 0,
        maximumValue = 5
    )

    return "★".repeat(safeRating) +
            "☆".repeat(5 - safeRating)
}
