package com.homemadefood.app.ui.order

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
import com.homemadefood.app.data.model.OrderResponse
import java.util.Locale
import androidx.compose.foundation.clickable

@Composable
fun OrdersScreen(
    uiState: OrdersUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onCancelOrderClick: (Int) -> Unit,
    onOrderClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var orderIdWaitingForCancellation by
    remember {
        mutableStateOf<Int?>(null)
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
                    }
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
            text = "Siparişlerim",
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

            uiState.errorMessage != null &&
                    uiState.orders.isEmpty() -> {

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

            uiState.orders.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz oluşturulmuş bir siparişiniz yok.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
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

                if (!uiState.errorMessage.isNullOrBlank()) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = uiState.orders,
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
                                onOrderClick(
                                    order.orderId
                                )
                            },

                            onCancelClick = {
                                orderIdWaitingForCancellation =
                                    order.orderId
                            }
                        )
                    }
                }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onOrderClick
            )
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = "Sipariş #${order.orderId}",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = translateOrderStatus(order.status),
                    color = getStatusColor(order.status),
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            if (order.businessName.isNotBlank()) {
                OrderDetailRow(
                    title = "Üretici",
                    value = order.businessName
                )
            }

            OrderDetailRow(
                title = "Toplam tutar",
                value = formatPrice(order.totalPrice)
            )

            OrderDetailRow(
                title = "Ödeme",
                value =
                    translatePaymentMethod(
                        order.paymentMethod
                    )
            )

            OrderDetailRow(
                title = "Adres başlığı",
                value = order.deliveryAddressTitle
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = order.deliveryAddress,
                style = MaterialTheme.typography.bodyMedium
            )

            if (order.customerNote.isNotBlank()) {
                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Sipariş notu:",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = order.customerNote,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Sipariş İçeriği",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text(
                        text =
                            "${item.foodName} × ${item.quantity}"
                    )

                    Text(
                        text =
                            formatPrice(item.totalPrice)
                    )
                }
            }

            if (order.status == "Pending") {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isCancelling
                ) {
                    if (isCancelling) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Siparişi İptal Et")
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderDetailRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),

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

@Composable
private fun getStatusColor(
    status: String
) = when (status) {
    "Pending" ->
        MaterialTheme.colorScheme.tertiary

    "Accepted",
    "Preparing",
    "Ready",
    "OutForDelivery" ->
        MaterialTheme.colorScheme.primary

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

private fun formatPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f TL",
        price
    )
}