package com.homemadefood.app.ui.producer

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.ProducerOrderResponse
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.homemadefood.app.data.model.OrderStatus

@Composable
fun ProducerOrdersScreen(
    uiState: ProducerOrdersUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onAcceptClick: (Int) -> Unit,
    onRejectClick: (Int) -> Unit,
    onStartPreparingClick: (Int) -> Unit,
    onMarkReadyClick: (Int) -> Unit,
    onOutForDeliveryClick: (Int) -> Unit,
    onDeliveredClick: (Int) -> Unit,
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
            Text("← Üretici Paneline Dön")
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Gelen Siparişler",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Siparişleri görüntüleyebilir ve durumlarını yönetebilirsiniz.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (uiState.successMessage != null) {
            Text(
                text = uiState.successMessage,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        if (
            uiState.errorMessage != null &&
            uiState.orders.isNotEmpty()
        ) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

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
                    modifier = Modifier.height(14.dp)
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
                        text = "Henüz gelen bir sipariş bulunmuyor.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
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

                        ProducerOrderCard(
                            order = order,

                            isUpdating =
                                uiState.updatingOrderId ==
                                        order.orderId,

                            onAcceptClick = {
                                onAcceptClick(order.orderId)
                            },

                            onRejectClick = {
                                onRejectClick(order.orderId)
                            },

                            onStartPreparingClick = {
                                onStartPreparingClick(
                                    order.orderId
                                )
                            },

                            onMarkReadyClick = {
                                onMarkReadyClick(
                                    order.orderId
                                )
                            },

                            onOutForDeliveryClick = {
                                onOutForDeliveryClick(
                                    order.orderId
                                )
                            },

                            onDeliveredClick = {
                                onDeliveredClick(
                                    order.orderId
                                )
                            }
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProducerOrderCard(
    order: ProducerOrderResponse,
    isUpdating: Boolean,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
    onStartPreparingClick: () -> Unit,
    onMarkReadyClick: () -> Unit,
    onOutForDeliveryClick: () -> Unit,
    onDeliveredClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
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
                    style =
                        MaterialTheme.typography.titleLarge
                )

                Text(
                    text =
                        order.orderStatus.displayName,

                    color =
                        getProducerOrderStatusColor(
                            order.orderStatus
                        ),

                    style =
                        MaterialTheme.typography.titleSmall
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ProducerOrderInformationRow(
                title = "Müşteri",
                value = order.customerFullName
            )

            if (order.customerPhone.isNotBlank()) {
                ProducerOrderInformationRow(
                    title = "Telefon",
                    value = order.customerPhone
                )
            }

            ProducerOrderInformationRow(
                title = "Toplam adet",
                value = order.totalQuantity.toString()
            )

            ProducerOrderInformationRow(
                title = "Toplam tutar",
                value =
                    formatProducerOrderPrice(
                        order.totalPrice
                    )
            )

            ProducerOrderInformationRow(
                title = "Ödeme",
                value =
                    translateProducerPaymentMethod(
                        order.paymentMethod
                    )
            )

            ProducerOrderInformationRow(
                title = "Sipariş tarihi",
                value =
                    formatProducerOrderDate(
                        order.createdAt
                    )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Teslimat Adresi",
                style =
                    MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = order.deliveryAddressTitle,
                style =
                    MaterialTheme.typography.titleSmall
            )

            Text(
                text = order.deliveryAddress,
                style =
                    MaterialTheme.typography.bodyMedium
            )

            if (order.customerNote.isNotBlank()) {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Müşteri Notu",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = order.customerNote,
                    style =
                        MaterialTheme.typography.bodyMedium
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
                style =
                    MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            order.items.forEach { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Text(
                        text =
                            "${item.foodName} × ${item.quantity}",

                        style =
                            MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text =
                            formatProducerOrderPrice(
                                item.totalPrice
                            ),

                        modifier =
                            Modifier.align(
                                Alignment.End
                            ),

                        style =
                            MaterialTheme.typography.titleSmall
                    )
                }
            }

            if (order.recommendationSearchId != null) {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Akıllı üretici seçimi skoru: " +
                                String.format(
                                    Locale("tr", "TR"),
                                    "%.2f",
                                    order.suitabilityScore
                                ),

                    style =
                        MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            ProducerOrderActionSection(
                status = order.orderStatus,
                isUpdating = isUpdating,
                onAcceptClick = onAcceptClick,
                onRejectClick = onRejectClick,
                onStartPreparingClick =
                    onStartPreparingClick,
                onMarkReadyClick = onMarkReadyClick,
                onOutForDeliveryClick =
                    onOutForDeliveryClick,
                onDeliveredClick = onDeliveredClick
            )
        }
    }
}

@Composable
private fun ProducerOrderActionSection(
    status: OrderStatus,
    isUpdating: Boolean,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
    onStartPreparingClick: () -> Unit,
    onMarkReadyClick: () -> Unit,
    onOutForDeliveryClick: () -> Unit,
    onDeliveredClick: () -> Unit
) {
    if (isUpdating) {
        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.Center,

            verticalAlignment =
                Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text("Sipariş güncelleniyor...")
        }

        return
    }

    when (status) {
        OrderStatus.PENDING -> {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onAcceptClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kabul Et")
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedButton(
                    onClick = onRejectClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reddet")
                }
            }
        }

        OrderStatus.ACCEPTED -> {
            Button(
                onClick = onStartPreparingClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hazırlamaya Başla")
            }
        }

        OrderStatus.PREPARING -> {
            Button(
                onClick = onMarkReadyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hazır Olarak İşaretle")
            }
        }

        OrderStatus.READY -> {
            Button(
                onClick = onOutForDeliveryClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Teslimata Çıkar")
            }
        }

        OrderStatus.OUT_FOR_DELIVERY -> {
            Button(
                onClick = onDeliveredClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Teslim Edildi")
            }
        }

        OrderStatus.DELIVERED -> {
            Text(
                text = "Sipariş süreci tamamlandı.",
                color =
                    MaterialTheme.colorScheme.primary,
                style =
                    MaterialTheme.typography.titleSmall
            )
        }

        OrderStatus.REJECTED -> {
            Text(
                text = "Sipariş reddedildi.",
                color =
                    MaterialTheme.colorScheme.error,
                style =
                    MaterialTheme.typography.titleSmall
            )
        }

        OrderStatus.CANCELLED -> {
            Text(
                text =
                    "Sipariş müşteri tarafından iptal edildi.",
                color =
                    MaterialTheme.colorScheme.error,
                style =
                    MaterialTheme.typography.titleSmall
            )
        }

        OrderStatus.UNKNOWN -> {
            Text(
                text =
                    "Sipariş durumu tanınamadı.",
                color =
                    MaterialTheme.colorScheme.error,
                style =
                    MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun ProducerOrderInformationRow(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography.bodySmall
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun getProducerOrderStatusColor(
    status: OrderStatus
) = when (status) {

    OrderStatus.PENDING ->
        MaterialTheme.colorScheme.tertiary

    OrderStatus.ACCEPTED,
    OrderStatus.PREPARING,
    OrderStatus.READY,
    OrderStatus.OUT_FOR_DELIVERY,
    OrderStatus.DELIVERED ->
        MaterialTheme.colorScheme.primary

    OrderStatus.REJECTED,
    OrderStatus.CANCELLED ->
        MaterialTheme.colorScheme.error

    OrderStatus.UNKNOWN ->
        MaterialTheme.colorScheme.onSurface
}


private fun formatProducerOrderPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f TL",
        price
    )
}
private fun translateProducerPaymentMethod(
    paymentMethod: String
): String {
    return when (paymentMethod) {
        "CashOnDelivery" ->
            "Kapıda Nakit Ödeme"

        "CardOnDelivery" ->
            "Kapıda Kartla Ödeme"

        else ->
            paymentMethod
    }
}

private fun formatProducerOrderDate(
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