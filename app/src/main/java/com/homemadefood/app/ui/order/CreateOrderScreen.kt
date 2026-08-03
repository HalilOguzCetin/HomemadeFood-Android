package com.homemadefood.app.ui.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.PaymentMethods
import java.util.Locale
import com.homemadefood.app.data.model.OrderStatus

@Composable
fun CreateOrderScreen(
    uiState: CreateOrderUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onAddressSelected: (Int) -> Unit,
    onPaymentMethodSelected: (String) -> Unit,
    onCustomerNoteChange: (String) -> Unit,
    onCreateOrderClick: () -> Unit,
    onReturnHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.createdOrder != null -> {
            OrderSuccessContent(
                orderId = uiState.createdOrder.orderId,
                totalPrice = uiState.createdOrder.totalPrice,
                status =
                    OrderStatus.fromBackendValue(
                        uiState.createdOrder.status
                    ),
                onReturnHomeClick = onReturnHomeClick,
                modifier = modifier
            )
        }

        uiState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null &&
                uiState.cart == null -> {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                TextButton(
                    onClick = onBackClick
                ) {
                    Text("← Sepetime Dön")
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

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
        }

        else -> {
            val cart = uiState.cart

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp)
            ) {
                TextButton(
                    onClick = onBackClick,
                    enabled = !uiState.isCreatingOrder
                ) {
                    Text("← Sepetime Dön")
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Siparişi Tamamla",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(
                    modifier = Modifier.height(22.dp)
                )

                Text(
                    text = "Sepet Özeti",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (
                            cart != null &&
                            cart.businessName.isNotBlank()
                        ) {
                            Text(
                                text = cart.businessName,
                                style =
                                    MaterialTheme.typography.titleMedium,
                                color =
                                    MaterialTheme.colorScheme.primary
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )
                        }

                        cart?.items?.forEach { item ->
                            Row(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text =
                                        "${item.foodName} × ${item.quantity}"
                                )

                                Text(
                                    text =
                                        formatPrice(item.lineTotal)
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )
                        }

                        HorizontalDivider(
                            modifier =
                                Modifier.padding(vertical = 10.dp)
                        )

                        OrderInformationRow(
                            title = "Toplam ürün",
                            value =
                                "${cart?.totalQuantity ?: 0} adet"
                        )

                        OrderInformationRow(
                            title = "Toplam tutar",
                            value =
                                formatPrice(
                                    cart?.totalPrice ?: 0.0
                                )
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(26.dp)
                )

                Text(
                    text = "Teslimat Adresi",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                if (uiState.addresses.isEmpty()) {
                    Text(
                        text =
                            "Kayıtlı adresiniz bulunmuyor. " +
                                    "Sipariş oluşturmadan önce " +
                                    "Adreslerim bölümünden adres ekleyin.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    uiState.addresses.forEach { address ->
                        AddressSelectionCard(
                            address = address,

                            isSelected =
                                uiState.selectedAddressId ==
                                        address.id,

                            enabled =
                                !uiState.isCreatingOrder,

                            onClick = {
                                onAddressSelected(
                                    address.id
                                )
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = "Ödeme Yöntemi",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                PaymentMethodRow(
                    title = "Kapıda Nakit Ödeme",

                    selected =
                        uiState.paymentMethod ==
                                PaymentMethods.CASH_ON_DELIVERY,

                    enabled =
                        !uiState.isCreatingOrder,

                    onClick = {
                        onPaymentMethodSelected(
                            PaymentMethods.CASH_ON_DELIVERY
                        )
                    }
                )

                PaymentMethodRow(
                    title = "Kapıda Kartla Ödeme",

                    selected =
                        uiState.paymentMethod ==
                                PaymentMethods.CARD_ON_DELIVERY,

                    enabled =
                        !uiState.isCreatingOrder,

                    onClick = {
                        onPaymentMethodSelected(
                            PaymentMethods.CARD_ON_DELIVERY
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                OutlinedTextField(
                    value = uiState.customerNote,

                    onValueChange =
                        onCustomerNoteChange,

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Sipariş Notu")
                    },

                    placeholder = {
                        Text(
                            "Örnek: Zili çalmayın, " +
                                    "telefonla arayın."
                        )
                    },

                    supportingText = {
                        Text(
                            "${uiState.customerNote.length}/500"
                        )
                    },

                    minLines = 3,
                    maxLines = 5,
                    enabled = !uiState.isCreatingOrder
                )

                if (!uiState.errorMessage.isNullOrBlank()) {
                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Button(
                    onClick = onCreateOrderClick,

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !uiState.isCreatingOrder &&
                                cart != null &&
                                cart.items.isNotEmpty() &&
                                uiState.selectedAddressId != null
                ) {
                    if (uiState.isCreatingOrder) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.height(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Siparişi Onayla")
                    }
                }

                Spacer(
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}

@Composable
private fun AddressSelectionCard(
    address: AddressResponse,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                enabled = enabled
            )

            Column(
                modifier =
                    Modifier.padding(start = 8.dp)
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = address.title,
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    if (address.isDefault) {
                        Text(
                            text = "  • Varsayılan",
                            color =
                                MaterialTheme.colorScheme.primary,
                            style =
                                MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = address.fullAddress,
                    style =
                        MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodRow(
    title: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(vertical = 5.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled
        )

        Text(
            text = title,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun OrderInformationRow(
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
private fun OrderSuccessContent(
    orderId: Int,
    totalPrice: Double,
    status: OrderStatus,
    onReturnHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {
        Text(
            text = "Siparişiniz Oluşturuldu",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Sipariş No: #$orderId",
            style =
                MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Tutar: ${formatPrice(totalPrice)}"
        )

        Text(
            text = "Durum: ${status.displayName}"
        )

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        Button(
            onClick = onReturnHomeClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ana Sayfaya Dön")
        }
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

