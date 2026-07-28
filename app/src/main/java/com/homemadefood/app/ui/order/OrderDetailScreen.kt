package com.homemadefood.app.ui.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember {
        mutableStateOf(false)
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = {
                showCancelDialog = false
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
                        showCancelDialog = false
                        onCancelOrderClick()
                    }
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
                    Text("Vazgeç")
                }
            }
        )
    }

    when {
        uiState.isLoading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.order == null -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                TextButton(
                    onClick = onBackClick
                ) {
                    Text("← Siparişlerime Dön")
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text =
                        uiState.errorMessage
                            ?: "Sipariş bilgisi bulunamadı.",
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
        }

        else -> {
            val order = uiState.order

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                item {
                    TextButton(
                        onClick = onBackClick,
                        enabled = !uiState.isCancelling
                    ) {
                        Text("← Siparişlerime Dön")
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sipariş #${order.orderId}",
                            style =
                                MaterialTheme.typography.headlineMedium
                        )

                        Text(
                            text =
                                translateOrderStatus(
                                    order.status
                                ),
                            color =
                                getOrderStatusColor(
                                    order.status
                                ),
                            style =
                                MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    if (!uiState.actionMessage.isNullOrBlank()) {
                        Text(
                            text = uiState.actionMessage,
                            color =
                                MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )
                    }

                    if (!uiState.errorMessage.isNullOrBlank()) {
                        Text(
                            text = uiState.errorMessage,
                            color =
                                MaterialTheme.colorScheme.error
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Sipariş Bilgileri",
                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            OrderDetailInformationRow(
                                title = "Üretici",
                                value = order.businessName
                            )

                            OrderDetailInformationRow(
                                title = "Toplam tutar",
                                value =
                                    formatOrderPrice(
                                        order.totalPrice
                                    )
                            )

                            OrderDetailInformationRow(
                                title = "Ödeme",
                                value =
                                    translatePaymentMethod(
                                        order.paymentMethod
                                    )
                            )

                            OrderDetailInformationRow(
                                title = "Sipariş tarihi",
                                value =
                                    formatOrderDate(
                                        order.createdAt
                                    )
                            )

                            OrderDetailInformationRow(
                                title = "Son güncelleme",
                                value =
                                    formatOrderDate(
                                        order.statusUpdatedAt
                                    )
                            )

                            if (
                                order.recommendationSearchId != null
                            ) {
                                OrderDetailInformationRow(
                                    title = "Uygunluk puanı",
                                    value =
                                        String.format(
                                            Locale("tr", "TR"),
                                            "%.2f",
                                            order.suitabilityScore
                                        )
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Teslimat Adresi",
                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Text(
                                text =
                                    order.deliveryAddressTitle,
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Spacer(
                                modifier = Modifier.height(5.dp)
                            )

                            Text(
                                text = order.deliveryAddress,
                                style =
                                    MaterialTheme.typography.bodyLarge
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Text(
                                text =
                                    "Konum: " +
                                            "${order.deliveryLatitude}, " +
                                            order.deliveryLongitude,
                                style =
                                    MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    if (order.customerNote.isNotBlank()) {
                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Sipariş Notu",
                                    style =
                                        MaterialTheme.typography.titleLarge
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text = order.customerNote,
                                    style =
                                        MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Sipariş İçeriği",
                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            order.items.forEachIndexed {
                                    index,
                                    item ->

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier =
                                            Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = item.foodName,
                                            style =
                                                MaterialTheme.typography
                                                    .titleMedium
                                        )

                                        Text(
                                            text =
                                                "${item.quantity} adet × " +
                                                        formatOrderPrice(
                                                            item.unitPrice
                                                        ),
                                            style =
                                                MaterialTheme.typography
                                                    .bodyMedium
                                        )
                                    }

                                    Text(
                                        text =
                                            formatOrderPrice(
                                                item.totalPrice
                                            ),
                                        style =
                                            MaterialTheme.typography
                                                .titleMedium
                                    )
                                }

                                if (
                                    index <
                                    order.items.lastIndex
                                ) {
                                    HorizontalDivider(
                                        modifier =
                                            Modifier.padding(
                                                vertical = 12.dp
                                            )
                                    )
                                }
                            }
                        }
                    }

                    if (order.status == "Pending") {
                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        Button(
                            onClick = {
                                showCancelDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isCancelling
                        ) {
                            if (uiState.isCancelling) {
                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.height(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Siparişi İptal Et")
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderDetailInformationRow(
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
            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun getOrderStatusColor(
    status: String
) = when (status) {
    "Pending" ->
        MaterialTheme.colorScheme.tertiary

    "Accepted",
    "Preparing",
    "Ready",
    "OutForDelivery",
    "Delivered" ->
        MaterialTheme.colorScheme.primary

    "Rejected",
    "Cancelled" ->
        MaterialTheme.colorScheme.error

    else ->
        MaterialTheme.colorScheme.onSurface
}

private fun translateOrderStatus(
    status: String
): String {
    return when (status) {
        "Pending" -> "Onay Bekliyor"
        "Accepted" -> "Kabul Edildi"
        "Preparing" -> "Hazırlanıyor"
        "Ready" -> "Hazır"
        "OutForDelivery" -> "Teslimatta"
        "Delivered" -> "Teslim Edildi"
        "Rejected" -> "Reddedildi"
        "Cancelled" -> "İptal Edildi"
        else -> status
    }
}

private fun translatePaymentMethod(
    paymentMethod: String
): String {
    return when (paymentMethod) {
        "CashOnDelivery" ->
            "Kapıda Nakit"

        "CardOnDelivery" ->
            "Kapıda Kart"

        else ->
            paymentMethod
    }
}

private fun formatOrderPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f TL",
        price
    )
}

private fun formatOrderDate(
    value: String
): String {
    val formatter =
        DateTimeFormatter.ofPattern(
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