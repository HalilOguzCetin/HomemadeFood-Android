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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.AdminOrderListItemResponse
import com.homemadefood.app.ui.components.AppEmptyState
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.AppMessageType
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminOrdersScreen(
    uiState: AdminOrdersUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,

    onStatusSelected:
        (AdminOrderStatusFilter) -> Unit,

    onSearchQueryChange:
        (String) -> Unit,

    onCustomerIdChange:
        (String) -> Unit,

    onProducerProfileIdChange:
        (String) -> Unit,

    onDateFromChange:
        (String) -> Unit,

    onDateToChange:
        (String) -> Unit,

    onApplyFiltersClick: () -> Unit,
    onClearFiltersClick: () -> Unit,

    onOrderDetailClick:
        (Int) -> Unit,

    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        item {
            TextButton(
                onClick = onBackClick,
                enabled = !uiState.isLoading
            ) {
                Text("← Admin Paneline Dön")
            }
        }

        item {
            Text(
                text = "Tüm Siparişler",
                style =
                    MaterialTheme.typography
                        .headlineMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "Sistemdeki siparişleri müşteri, üretici, tarih ve sipariş durumuna göre takip edebilirsiniz.",

                style =
                    MaterialTheme.typography
                        .bodyMedium
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Sipariş Durumu",
                style =
                    MaterialTheme.typography
                        .titleSmall
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            AdminOrderStatusTabs(
                selectedStatus =
                    uiState.selectedStatus,

                enabled =
                    !uiState.isLoading,

                onStatusSelected =
                    onStatusSelected
            )
        }

        item {
            AdminOrderFiltersCard(
                uiState = uiState,

                onSearchQueryChange =
                    onSearchQueryChange,

                onCustomerIdChange =
                    onCustomerIdChange,

                onProducerProfileIdChange =
                    onProducerProfileIdChange,

                onDateFromChange =
                    onDateFromChange,

                onDateToChange =
                    onDateToChange,

                onApplyFiltersClick =
                    onApplyFiltersClick,

                onClearFiltersClick =
                    onClearFiltersClick
            )
        }

        if (
            uiState.errorMessage != null &&
            uiState.orders.isNotEmpty()
        ) {
            item {
                AppInlineMessage(
                    message = uiState.errorMessage,
                    type = AppMessageType.Error
                )
            }
        }

        when {
            uiState.isLoading -> {
                item {
                    AppLoadingState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        message = "Siparişler yükleniyor..."
                    )
                }
            }

            uiState.errorMessage != null &&
                    uiState.orders.isEmpty() -> {
                item {
                    AppErrorState(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                }
            }

            uiState.orders.isEmpty() -> {
                item {
                    AppEmptyState(
                        title = "Sipariş bulunamadı",
                        message = uiState.emptyMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                }
            }

            else -> {
                item {
                    Text(
                        text =
                            "${uiState.orders.size} sipariş bulundu.",

                        style =
                            MaterialTheme.typography
                                .bodySmall
                    )
                }

                items(
                    items = uiState.orders,

                    key = { order ->
                        order.orderId
                    }
                ) { order ->

                    AdminOrderCard(
                        order = order,

                        onDetailClick = {
                            onOrderDetailClick(
                                order.orderId
                            )
                        }
                    )
                }
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun AdminOrderStatusTabs(
    selectedStatus:
    AdminOrderStatusFilter,

    enabled: Boolean,

    onStatusSelected:
        (AdminOrderStatusFilter) -> Unit
) {
    val statuses =
        AdminOrderStatusFilter.entries

    val selectedIndex =
        statuses.indexOf(selectedStatus)
            .coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex =
            selectedIndex,

        edgePadding = 0.dp
    ) {
        statuses.forEach { status ->
            Tab(
                selected =
                    status == selectedStatus,

                onClick = {
                    onStatusSelected(status)
                },

                enabled = enabled,

                text = {
                    Text(
                        text =
                            status.displayName
                    )
                }
            )
        }
    }
}

@Composable
private fun AdminOrderFiltersCard(
    uiState: AdminOrdersUiState,

    onSearchQueryChange:
        (String) -> Unit,

    onCustomerIdChange:
        (String) -> Unit,

    onProducerProfileIdChange:
        (String) -> Unit,

    onDateFromChange:
        (String) -> Unit,

    onDateToChange:
        (String) -> Unit,

    onApplyFiltersClick: () -> Unit,
    onClearFiltersClick: () -> Unit
) {
    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Gelişmiş Filtreler",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Text(
                text =
                    "Arama alanı müşteri adı, e-posta, işletme adı ve yemek adında arama yapar.",

                style =
                    MaterialTheme.typography
                        .bodySmall
            )

            OutlinedTextField(
                value =
                    uiState.searchQuery,

                onValueChange =
                    onSearchQueryChange,

                label = {
                    Text(
                        "Müşteri, işletme veya yemek ara"
                    )
                },

                supportingText = {
                    Text(
                        "${uiState.searchQuery.length}/100"
                    )
                },

                singleLine = true,

                enabled =
                    !uiState.isLoading,

                modifier =
                    Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value =
                    uiState.customerIdInput,

                onValueChange =
                    onCustomerIdChange,

                label = {
                    Text("Müşteri ID")
                },

                placeholder = {
                    Text("Örnek: 4")
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),

                singleLine = true,

                enabled =
                    !uiState.isLoading,

                modifier =
                    Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value =
                    uiState
                        .producerProfileIdInput,

                onValueChange =
                    onProducerProfileIdChange,

                label = {
                    Text("Üretici Profil ID")
                },

                placeholder = {
                    Text("Örnek: 2")
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),

                singleLine = true,

                enabled =
                    !uiState.isLoading,

                modifier =
                    Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            Text(
                text = "Sipariş Tarihi",
                style =
                    MaterialTheme.typography
                        .titleMedium
            )

            Text(
                text =
                    "Tarihleri yyyy-MM-dd biçiminde yazın.",

                style =
                    MaterialTheme.typography
                        .bodySmall
            )

            OutlinedTextField(
                value =
                    uiState.dateFromInput,

                onValueChange =
                    onDateFromChange,

                label = {
                    Text("Başlangıç Tarihi")
                },

                placeholder = {
                    Text("2026-08-01")
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),

                singleLine = true,

                enabled =
                    !uiState.isLoading,

                modifier =
                    Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value =
                    uiState.dateToInput,

                onValueChange =
                    onDateToChange,

                label = {
                    Text("Bitiş Tarihi")
                },

                placeholder = {
                    Text("2026-08-06")
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),

                singleLine = true,

                enabled =
                    !uiState.isLoading,

                modifier =
                    Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick =
                        onApplyFiltersClick,

                    enabled =
                        !uiState.isLoading,

                    modifier =
                        Modifier.weight(1f)
                ) {
                    Text("Filtreleri Uygula")
                }

                OutlinedButton(
                    onClick =
                        onClearFiltersClick,

                    enabled =
                        !uiState.isLoading &&
                                uiState.hasActiveFilters,

                    modifier =
                        Modifier.weight(1f)
                ) {
                    Text("Temizle")
                }
            }
        }
    }
}

@Composable
private fun AdminOrderCard(
    order: AdminOrderListItemResponse,
    onDetailClick: () -> Unit
) {
    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
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
                        translateAdminOrderStatus(
                            order.status
                        ),

                    color =
                        adminOrderStatusColor(
                            order.status
                        ),

                    style =
                        MaterialTheme.typography
                            .titleSmall
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminOrderInformation(
                title = "Müşteri",
                value =
                    order.customerFullName
                        .ifBlank {
                            "-"
                        }
            )

            AdminOrderInformation(
                title = "Müşteri E-postası",
                value =
                    order.customerEmail
                        .ifBlank {
                            "-"
                        }
            )

            AdminOrderInformation(
                title = "Müşteri ID",
                value =
                    order.customerId
                        .toString()
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            AdminOrderInformation(
                title = "İşletme",
                value =
                    order.businessName
                        .ifBlank {
                            "-"
                        }
            )

            AdminOrderInformation(
                title = "Üretici Profil ID",
                value =
                    order.producerProfileId
                        .toString()
            )

            AdminOrderInformation(
                title = "Ödeme Yöntemi",
                value =
                    translateAdminPaymentMethod(
                        order.paymentMethod
                    )
            )

            AdminOrderInformation(
                title = "Toplam Tutar",
                value =
                    formatAdminOrderPrice(
                        order.totalPrice
                    )
            )

            AdminOrderInformation(
                title = "Farklı Ürün Sayısı",
                value =
                    order.itemCount
                        .toString()
            )

            AdminOrderInformation(
                title = "Toplam Ürün Adedi",
                value =
                    order.totalQuantity
                        .toString()
            )

            if (
                order.recommendationSearchId != null
            ) {
                AdminOrderInformation(
                    title = "Öneri Araması ID",
                    value =
                        order.recommendationSearchId
                            .toString()
                )

                AdminOrderInformation(
                    title = "Uygunluk Puanı",
                    value =
                        order.suitabilityScore
                            .toString()
                )
            }

            AdminOrderInformation(
                title = "Sipariş Tarihi",
                value =
                    formatAdminOrderDate(
                        order.createdAt
                    )
            )

            AdminOrderInformation(
                title =
                    "Son Durum Güncellemesi",

                value =
                    formatAdminOrderDate(
                        order.statusUpdatedAt
                    )
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            OutlinedButton(
                onClick =
                    onDetailClick,

                modifier =
                    Modifier.fillMaxWidth()
            ) {
                Text("Sipariş Detayını Gör")
            }
        }
    }
}

@Composable
private fun AdminOrderInformation(
    title: String,
    value: String
) {
    Column(
        modifier =
            Modifier
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




private fun translateAdminOrderStatus(
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
private fun adminOrderStatusColor(
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
    AdminOrderStatusFilter.OUT_FOR_DELIVERY ->
        MaterialTheme.colorScheme.primary

    AdminOrderStatusFilter.DELIVERED ->
        MaterialTheme.colorScheme.primary

    AdminOrderStatusFilter.REJECTED,
    AdminOrderStatusFilter.CANCELLED ->
        MaterialTheme.colorScheme.error

    AdminOrderStatusFilter.ALL,
    null ->
        MaterialTheme.colorScheme.onSurface
}

private fun translateAdminPaymentMethod(
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

private fun formatAdminOrderPrice(
    value: Double
): String {
    return NumberFormat
        .getCurrencyInstance(
            Locale("tr", "TR")
        )
        .format(value)
}

private fun formatAdminOrderDate(
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