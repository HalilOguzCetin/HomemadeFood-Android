package com.homemadefood.app.ui.admin

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
import com.homemadefood.app.data.model.AdminOrderDetailResponse
import com.homemadefood.app.data.model.AdminOrderItemResponse
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminOrderDetailScreen(
    uiState: AdminOrderDetailUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {
        TextButton(
            onClick = onBackClick
        ) {
            Text("← Sipariş Listesine Dön")
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Sipariş Detayı",
            style =
                MaterialTheme.typography
                    .headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        when {
            uiState.isLoading -> {
                AdminOrderDetailLoadingContent()
            }

            uiState.order == null -> {
                AdminOrderDetailErrorContent(
                    message =
                        uiState.errorMessage
                            ?: "Sipariş bilgileri alınamadı.",

                    onRetryClick =
                        onRetryClick
                )
            }

            else -> {
                AdminOrderDetailContent(
                    order = uiState.order
                )
            }
        }
    }
}

@Composable
private fun AdminOrderDetailContent(
    order: AdminOrderDetailResponse
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {
        item {
            AdminOrderSummaryCard(
                order = order
            )
        }

        item {
            AdminOrderCustomerCard(
                order = order
            )
        }

        item {
            AdminOrderProducerCard(
                order = order
            )
        }

        item {
            AdminOrderDeliveryCard(
                order = order
            )
        }

        item {
            AdminOrderPaymentCard(
                order = order
            )
        }

        if (
            order.recommendationSearchId != null
        ) {
            item {
                AdminOrderRecommendationCard(
                    order = order
                )
            }
        }

        item {
            Text(
                text = "Sipariş Ürünleri",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )
        }

        if (order.items.isEmpty()) {
            item {
                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text(
                        text =
                            "Bu siparişe ait ürün bulunmuyor.",

                        modifier =
                            Modifier.padding(16.dp),

                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )
                }
            }
        } else {
            items(
                items = order.items,

                key = { item ->
                    item.orderItemId
                }
            ) { item ->

                AdminOrderItemCard(
                    item = item
                )
            }
        }

        item {
            AdminOrderTotalCard(
                order = order
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun AdminOrderSummaryCard(
    order: AdminOrderDetailResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text =
                        "Sipariş #${order.orderId}",

                    style =
                        MaterialTheme.typography
                            .titleLarge
                )

                Text(
                    text =
                        translateAdminOrderDetailStatus(
                            order.status
                        ),

                    color =
                        adminOrderDetailStatusColor(
                            order.status
                        ),

                    style =
                        MaterialTheme.typography
                            .titleSmall
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            AdminOrderDetailInformation(
                title = "Sipariş Durumu",
                value =
                    translateAdminOrderDetailStatus(
                        order.status
                    )
            )

            AdminOrderDetailInformation(
                title = "Durum Versiyonu",
                value =
                    order.statusVersion.toString()
            )

            AdminOrderDetailInformation(
                title = "Sipariş Tarihi",
                value =
                    formatAdminOrderDetailDate(
                        order.createdAt
                    )
            )

            AdminOrderDetailInformation(
                title = "Son Durum Güncellemesi",
                value =
                    formatAdminOrderDetailDate(
                        order.statusUpdatedAt
                    )
            )
        }
    }
}

@Composable
private fun AdminOrderCustomerCard(
    order: AdminOrderDetailResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Müşteri Bilgileri",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminOrderDetailInformation(
                title = "Müşteri ID",
                value =
                    order.customerId.toString()
            )

            AdminOrderDetailInformation(
                title = "Ad Soyad",
                value =
                    order.customerFullName
                        .ifBlank {
                            "-"
                        }
            )

            AdminOrderDetailInformation(
                title = "E-posta",
                value =
                    order.customerEmail
                        .ifBlank {
                            "-"
                        }
            )

            AdminOrderDetailInformation(
                title = "Telefon",
                value =
                    order.customerPhone
                        .ifBlank {
                            "-"
                        }
            )
        }
    }
}

@Composable
private fun AdminOrderProducerCard(
    order: AdminOrderDetailResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Üretici Bilgileri",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminOrderDetailInformation(
                title = "Üretici Profil ID",
                value =
                    order.producerProfileId
                        .toString()
            )

            AdminOrderDetailInformation(
                title = "İşletme",
                value =
                    order.businessName
                        .ifBlank {
                            "-"
                        }
            )
        }
    }
}

@Composable
private fun AdminOrderDeliveryCard(
    order: AdminOrderDetailResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Teslimat Bilgileri",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminOrderDetailInformation(
                title = "Adres Başlığı",
                value =
                    order.deliveryAddressTitle
                        .ifBlank {
                            "-"
                        }
            )

            AdminOrderDetailInformation(
                title = "Teslimat Adresi",
                value =
                    order.deliveryAddress
                        .ifBlank {
                            "-"
                        }
            )

            AdminOrderDetailInformation(
                title = "Konum",
                value =
                    "${order.deliveryLatitude}, " +
                            order.deliveryLongitude
            )

            HorizontalDivider(
                modifier =
                    Modifier.padding(
                        vertical = 10.dp
                    )
            )

            Text(
                text = "Müşteri Notu",
                style =
                    MaterialTheme.typography
                        .titleMedium
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    order.customerNote
                        .takeIf {
                            it.isNotBlank()
                        }
                        ?: "Müşteri notu bulunmuyor.",

                style =
                    MaterialTheme.typography
                        .bodyMedium
            )
        }
    }
}

@Composable
private fun AdminOrderPaymentCard(
    order: AdminOrderDetailResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ödeme Bilgileri",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminOrderDetailInformation(
                title = "Ödeme Yöntemi",
                value =
                    translateAdminOrderDetailPaymentMethod(
                        order.paymentMethod
                    )
            )

            AdminOrderDetailInformation(
                title = "Sipariş Toplamı",
                value =
                    formatAdminOrderDetailPrice(
                        order.totalPrice
                    )
            )
        }
    }
}

@Composable
private fun AdminOrderRecommendationCard(
    order: AdminOrderDetailResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Öneri Sistemi Bilgileri",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminOrderDetailInformation(
                title = "Öneri Araması ID",
                value =
                    order.recommendationSearchId
                        ?.toString()
                        ?: "-"
            )

            AdminOrderDetailInformation(
                title = "Uygunluk Puanı",
                value =
                    order.suitabilityScore
                        .toString()
            )
        }
    }
}

@Composable
private fun AdminOrderItemCard(
    item: AdminOrderItemResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text =
                    item.foodName.ifBlank {
                        "Yemek"
                    },

                style =
                    MaterialTheme.typography
                        .titleMedium
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            AdminOrderDetailInformation(
                title = "Sipariş Kalem ID",
                value =
                    item.orderItemId.toString()
            )

            AdminOrderDetailInformation(
                title = "Yemek ID",
                value =
                    item.foodId.toString()
            )

            AdminOrderDetailInformation(
                title = "Adet",
                value =
                    item.quantity.toString()
            )

            AdminOrderDetailInformation(
                title = "Birim Fiyat",
                value =
                    formatAdminOrderDetailPrice(
                        item.unitPrice
                    )
            )

            AdminOrderDetailInformation(
                title = "Kalem Toplamı",
                value =
                    formatAdminOrderDetailPrice(
                        item.totalPrice
                    )
            )
        }
    }
}

@Composable
private fun AdminOrderTotalCard(
    order: AdminOrderDetailResponse
) {
    val itemsTotal =
        order.items.sumOf { item ->
            item.totalPrice
        }

    val totalQuantity =
        order.items.sumOf { item ->
            item.quantity
        }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Sipariş Özeti",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminOrderDetailInformation(
                title = "Farklı Ürün Sayısı",
                value =
                    order.items.size.toString()
            )

            AdminOrderDetailInformation(
                title = "Toplam Ürün Adedi",
                value =
                    totalQuantity.toString()
            )

            AdminOrderDetailInformation(
                title = "Ürünler Toplamı",
                value =
                    formatAdminOrderDetailPrice(
                        itemsTotal
                    )
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = "Genel Toplam",
                    style =
                        MaterialTheme.typography
                            .titleLarge
                )

                Text(
                    text =
                        formatAdminOrderDetailPrice(
                            order.totalPrice
                        ),

                    style =
                        MaterialTheme.typography
                            .titleLarge,

                    color =
                        MaterialTheme.colorScheme
                            .primary
                )
            }
        }
    }
}

@Composable
private fun AdminOrderDetailInformation(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography
                    .bodySmall
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography
                    .titleSmall
        )
    }
}

@Composable
private fun AdminOrderDetailLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    "Sipariş bilgileri yükleniyor..."
            )
        }
    }
}

@Composable
private fun AdminOrderDetailErrorContent(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = message,

            color =
                MaterialTheme.colorScheme.error,

            style =
                MaterialTheme.typography
                    .bodyLarge
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Button(
            onClick = onRetryClick,
            modifier =
                Modifier.fillMaxWidth()
        ) {
            Text("Tekrar Dene")
        }
    }
}

private fun translateAdminOrderDetailStatus(
    status: String
): String {
    return AdminOrderStatusFilter
        .fromBackendValue(status)
        ?.displayName
        ?: status.ifBlank {
            "Bilinmeyen Durum"
        }
}

@Composable
private fun adminOrderDetailStatusColor(
    status: String
) = when (
    AdminOrderStatusFilter
        .fromBackendValue(status)
) {
    AdminOrderStatusFilter.PENDING ->
        MaterialTheme.colorScheme.tertiary

    AdminOrderStatusFilter.ACCEPTED,
    AdminOrderStatusFilter.PREPARING,
    AdminOrderStatusFilter.READY,
    AdminOrderStatusFilter.OUT_FOR_DELIVERY,
    AdminOrderStatusFilter.DELIVERED ->
        MaterialTheme.colorScheme.primary

    AdminOrderStatusFilter.REJECTED,
    AdminOrderStatusFilter.CANCELLED ->
        MaterialTheme.colorScheme.error

    AdminOrderStatusFilter.ALL,
    null ->
        MaterialTheme.colorScheme.onSurface
}

private fun translateAdminOrderDetailPaymentMethod(
    paymentMethod: String
): String {
    return when {
        paymentMethod.equals(
            "CashOnDelivery",
            ignoreCase = true
        ) ->
            "Kapıda Nakit"

        paymentMethod.equals(
            "CardOnDelivery",
            ignoreCase = true
        ) ->
            "Kapıda Kart"

        else ->
            paymentMethod.ifBlank {
                "-"
            }
    }
}

private fun formatAdminOrderDetailPrice(
    value: Double
): String {
    return NumberFormat
        .getCurrencyInstance(
            Locale("tr", "TR")
        )
        .format(value)
}

private fun formatAdminOrderDetailDate(
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