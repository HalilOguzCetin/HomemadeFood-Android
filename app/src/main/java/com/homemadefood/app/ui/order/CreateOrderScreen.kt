package com.homemadefood.app.ui.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.CartItemResponse
import com.homemadefood.app.data.model.OrderStatus
import com.homemadefood.app.data.model.PaymentMethods
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.FoodImage
import com.homemadefood.app.ui.customer.CustomerHomeColors
import com.homemadefood.app.ui.customer.CustomerHomeTheme
import java.util.Locale

@Composable
fun CreateOrderScreen(
    uiState: CreateOrderUiState,
    isPhoneVerificationLoading: Boolean,
    isPhoneVerifiedForOrder: Boolean,
    phoneVerificationErrorMessage: String?,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onRetryPhoneVerificationStatusClick: () -> Unit,
    onPhoneVerificationClick: () -> Unit,
    onAddressSelected: (Int) -> Unit,
    onPaymentMethodSelected: (String) -> Unit,
    onCustomerNoteChange: (String) -> Unit,
    onCreateOrderClick: () -> Unit,
    onReturnHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        when {
            uiState.createdOrder != null -> {
                OrderSuccessContent(
                    orderId = uiState.createdOrder.orderId,
                    totalPrice = uiState.createdOrder.totalPrice,
                    status = uiState.createdOrder.status,
                    onReturnHomeClick = onReturnHomeClick,
                    modifier = modifier
                )
            }

            uiState.isLoading -> {
                AppLoadingState(
                    modifier = modifier
                        .fillMaxSize()
                        .background(CustomerHomeColors.Cream),
                    message = "Sipariş bilgileri yükleniyor..."
                )
            }

            uiState.errorMessage != null && uiState.cart == null -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(CustomerHomeColors.Cream)
                ) {
                    CheckoutHeader(
                        onBackClick = onBackClick,
                        enabled = true
                    )

                    AppErrorState(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            else -> {
                val cart = uiState.cart

                val serverRequiresPhoneVerification =
                    uiState.errorMessage
                        ?.contains(
                            "telefon numaranızı doğrulamanız gerekir",
                            ignoreCase = true
                        ) == true

                val phoneVerificationRequired =
                    !isPhoneVerificationLoading &&
                            (!isPhoneVerifiedForOrder || serverRequiresPhoneVerification)

                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(CustomerHomeColors.Cream)
                ) {
                    CheckoutHeader(
                        onBackClick = onBackClick,
                        enabled = !uiState.isCreatingOrder
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                bottom = 18.dp
                            )
                    ) {
                        Text(
                            text = "Siparişinizi gözden geçirin ve onaylayın.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CustomerHomeColors.TextMuted
                        )

                        Spacer(Modifier.height(18.dp))

                        AddressSection(
                            addresses = uiState.addresses,
                            selectedAddressId = uiState.selectedAddressId,
                            enabled = !uiState.isCreatingOrder,
                            onAddressSelected = onAddressSelected
                        )

                        Spacer(Modifier.height(14.dp))

                        if (cart != null && cart.businessName.isNotBlank()) {
                            BusinessSummaryCard(
                                businessName = cart.businessName,
                                isCurrentlyOpen = cart.isCurrentlyOpen
                            )

                            Spacer(Modifier.height(14.dp))
                        }

                        if (cart != null && !cart.isCurrentlyOpen) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = CustomerHomeColors.TerracottaSoft
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp)
                                ) {
                                    Text(
                                        text = "İşletme şu anda kapalı",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = CustomerHomeColors.Terracotta,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = "Sepetiniz korunur ancak işletme yeniden açılana kadar bu siparişi oluşturamazsınız.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CustomerHomeColors.TextMuted
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))
                        }

                        if (cart != null) {
                            ProductsSummaryCard(
                                items = cart.items,
                                totalQuantity = cart.totalQuantity
                            )

                            Spacer(Modifier.height(14.dp))
                        }

                        PaymentSection(
                            selectedPaymentMethod = uiState.paymentMethod,
                            enabled = !uiState.isCreatingOrder,
                            onPaymentMethodSelected = onPaymentMethodSelected
                        )

                        Spacer(Modifier.height(14.dp))

                        OrderNoteCard(
                            note = uiState.customerNote,
                            enabled = !uiState.isCreatingOrder,
                            onValueChange = onCustomerNoteChange
                        )

                        if (
                            !uiState.errorMessage.isNullOrBlank() &&
                            !serverRequiresPhoneVerification
                        ) {
                            Spacer(Modifier.height(14.dp))

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = CustomerHomeColors.TerracottaSoft
                            ) {
                                Text(
                                    text = uiState.errorMessage,
                                    modifier = Modifier.padding(14.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CustomerHomeColors.Error
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        PhoneVerificationSection(
                            isLoading = isPhoneVerificationLoading,
                            isRequired = phoneVerificationRequired,
                            errorMessage = phoneVerificationErrorMessage,
                            enabled = !uiState.isCreatingOrder,
                            onRetryClick = onRetryPhoneVerificationStatusClick,
                            onVerificationClick = onPhoneVerificationClick
                        )
                    }

                    CheckoutBottomBar(
                        totalQuantity = cart?.totalQuantity ?: 0,
                        totalPrice = cart?.totalPrice ?: 0.0,
                        isCurrentlyOpen = cart?.isCurrentlyOpen == true,
                        isCreatingOrder = uiState.isCreatingOrder,
                        phoneVerificationRequired = phoneVerificationRequired,
                        phoneVerificationLoading = isPhoneVerificationLoading,
                        enabled =
                            cart != null &&
                                    cart.isCurrentlyOpen &&
                                    cart.items.isNotEmpty() &&
                                    uiState.selectedAddressId != null,
                        onCreateOrderClick = onCreateOrderClick
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckoutHeader(
    onBackClick: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    enabled = enabled,
                    onClick = onBackClick
                ),
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

        Spacer(Modifier.width(14.dp))

        Text(
            text = "Siparişi Tamamla",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineSmall,
            color = CustomerHomeColors.DeepOlive,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AddressSection(
    addresses: List<AddressResponse>,
    selectedAddressId: Int?,
    enabled: Boolean,
    onAddressSelected: (Int) -> Unit
) {
    SectionCard {
        SectionTitle(
            title = "Teslimat Adresi",
            leadingText = "⌖"
        )

        Spacer(Modifier.height(10.dp))

        if (addresses.isEmpty()) {
            Text(
                text =
                    "Kayıtlı adresiniz bulunmuyor. Sipariş oluşturmadan önce Adreslerim bölümünden adres ekleyin.",
                style = MaterialTheme.typography.bodyMedium,
                color = CustomerHomeColors.Error
            )
        } else {
            addresses.forEachIndexed { index, address ->
                AddressSelectionRow(
                    address = address,
                    isSelected = selectedAddressId == address.id,
                    enabled = enabled,
                    onClick = {
                        onAddressSelected(address.id)
                    }
                )

                if (index != addresses.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        color = CustomerHomeColors.Outline
                    )
                }
            }
        }

        if (addresses.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CustomerHomeColors.OliveSoft.copy(alpha = 0.55f)
            ) {
                Text(
                    text = "Siparişiniz seçili adrese gönderilecektir.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = CustomerHomeColors.DeepOlive
                )
            }
        }
    }
}

@Composable
private fun AddressSelectionRow(
    address: AddressResponse,
    isSelected: Boolean,
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
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            enabled = enabled
        )

        Spacer(Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = address.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )

                if (address.isDefault) {
                    Text(
                        text = "  • Varsayılan",
                        style = MaterialTheme.typography.labelMedium,
                        color = CustomerHomeColors.Terracotta,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(3.dp))

            Text(
                text = address.fullAddress,
                style = MaterialTheme.typography.bodyMedium,
                color = CustomerHomeColors.TextMuted
            )
        }
    }
}

@Composable
private fun BusinessSummaryCard(
    businessName: String,
    isCurrentlyOpen: Boolean
) {
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = CustomerHomeColors.OliveSoft
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⌂",
                        style = MaterialTheme.typography.titleLarge,
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Hazırlayan İşletme",
                    style = MaterialTheme.typography.labelMedium,
                    color = CustomerHomeColors.TextMuted
                )

                Text(
                    text = businessName,
                    style = MaterialTheme.typography.titleMedium,
                    color =
                        if (isCurrentlyOpen) {
                            CustomerHomeColors.DeepOlive
                        } else {
                            CustomerHomeColors.TextMuted
                        },
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color =
                        if (isCurrentlyOpen) {
                            CustomerHomeColors.OliveSoft
                        } else {
                            CustomerHomeColors.TerracottaSoft
                        }
                ) {
                    Text(
                        text = if (isCurrentlyOpen) "Açık" else "Kapalı",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color =
                            if (isCurrentlyOpen) {
                                CustomerHomeColors.DeepOlive
                            } else {
                                CustomerHomeColors.Terracotta
                            },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductsSummaryCard(
    items: List<CartItemResponse>,
    totalQuantity: Int
) {
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sepetinizdeki Ürünler",
                style = MaterialTheme.typography.titleLarge,
                color = CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CustomerHomeColors.OliveSoft
            ) {
                Text(
                    text = "$totalQuantity ürün",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        items.forEachIndexed { index, item ->
            CheckoutProductRow(item = item)

            if (index != items.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = CustomerHomeColors.Outline
                )
            }
        }
    }
}

@Composable
private fun CheckoutProductRow(
    item: CartItemResponse
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FoodImage(
            imageUrl = item.imageUrl,
            contentDescription = item.foodName,
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(14.dp))
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.foodName,
                style = MaterialTheme.typography.titleSmall,
                color = CustomerHomeColors.Text,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${formatPrice(item.unitPrice)}  •  ${item.quantity} adet",
                style = MaterialTheme.typography.bodySmall,
                color = CustomerHomeColors.TextMuted
            )
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = formatPrice(item.lineTotal),
            style = MaterialTheme.typography.titleMedium,
            color = CustomerHomeColors.Terracotta,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PaymentSection(
    selectedPaymentMethod: String,
    enabled: Boolean,
    onPaymentMethodSelected: (String) -> Unit
) {
    SectionCard {
        SectionTitle(
            title = "Ödeme Yöntemi",
            leadingText = "₺"
        )

        Spacer(Modifier.height(8.dp))

        PaymentMethodRow(
            title = "Kapıda Nakit Ödeme",
            selected =
                selectedPaymentMethod == PaymentMethods.CASH_ON_DELIVERY,
            enabled = enabled,
            onClick = {
                onPaymentMethodSelected(
                    PaymentMethods.CASH_ON_DELIVERY
                )
            }
        )

        PaymentMethodRow(
            title = "Kapıda Kartla Ödeme",
            selected =
                selectedPaymentMethod == PaymentMethods.CARD_ON_DELIVERY,
            enabled = enabled,
            onClick = {
                onPaymentMethodSelected(
                    PaymentMethods.CARD_ON_DELIVERY
                )
            }
        )
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
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = CustomerHomeColors.Text
        )
    }
}

@Composable
private fun OrderNoteCard(
    note: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    SectionCard {
        SectionTitle(
            title = "Sipariş Notu",
            leadingText = "✎",
            trailingText = "İsteğe bağlı"
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = note,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Örn: Zile basmayın, telefonla arayın."
                )
            },
            supportingText = {
                Text(
                    text = "${note.length}/500",
                    modifier = Modifier.fillMaxWidth()
                )
            },
            minLines = 3,
            maxLines = 5,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun PhoneVerificationSection(
    isLoading: Boolean,
    isRequired: Boolean,
    errorMessage: String?,
    enabled: Boolean,
    onRetryClick: () -> Unit,
    onVerificationClick: () -> Unit
) {
    when {
        isLoading -> {
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
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = CustomerHomeColors.DeepOlive
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = "Telefon doğrulama durumu kontrol ediliyor...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomerHomeColors.TextMuted
                    )
                }
            }
        }

        !errorMessage.isNullOrBlank() -> {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = CustomerHomeColors.TerracottaSoft
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Telefon doğrulama durumu alınamadı",
                        style = MaterialTheme.typography.titleMedium,
                        color = CustomerHomeColors.Text,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomerHomeColors.Error
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onRetryClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomerHomeColors.DeepOlive
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Tekrar Kontrol Et")
                    }
                }
            }
        }

        isRequired -> {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = CustomerHomeColors.TerracottaSoft
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = CustomerHomeColors.Surface
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.titleMedium,
                                color = CustomerHomeColors.Terracotta,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Telefon Doğrulaması",
                            style = MaterialTheme.typography.titleMedium,
                            color = CustomerHomeColors.Terracotta,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Sipariş oluşturmak için telefon numaranızın doğrulanmış olması gerekir.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomerHomeColors.TextMuted
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Button(
                        onClick = onVerificationClick,
                        enabled = enabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomerHomeColors.Terracotta
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Doğrula")
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    totalQuantity: Int,
    totalPrice: Double,
    isCurrentlyOpen: Boolean,
    isCreatingOrder: Boolean,
    phoneVerificationRequired: Boolean,
    phoneVerificationLoading: Boolean,
    enabled: Boolean,
    onCreateOrderClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CustomerHomeColors.Surface,
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 14.dp,
                bottom = 16.dp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ürün Toplamı ($totalQuantity ürün)",
                        style = MaterialTheme.typography.bodySmall,
                        color = CustomerHomeColors.TextMuted
                    )

                    Text(
                        text = "Toplam Tutar",
                        style = MaterialTheme.typography.titleMedium,
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = formatPrice(totalPrice),
                    style = MaterialTheme.typography.headlineSmall,
                    color = CustomerHomeColors.Terracotta,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onCreateOrderClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled =
                    enabled &&
                            isCurrentlyOpen &&
                            !isCreatingOrder &&
                            !phoneVerificationRequired &&
                            !phoneVerificationLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomerHomeColors.Terracotta,
                    contentColor = CustomerHomeColors.Surface,
                    disabledContainerColor =
                        CustomerHomeColors.Terracotta.copy(alpha = 0.45f),
                    disabledContentColor =
                        CustomerHomeColors.Surface.copy(alpha = 0.85f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isCreatingOrder) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = CustomerHomeColors.Surface
                    )
                } else {
                    Text(
                        text =
                            if (isCurrentlyOpen) {
                                "Siparişi Oluştur"
                            } else {
                                "İşletme Kapalı"
                            },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    content: @Composable () -> Unit
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
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    leadingText: String,
    trailingText: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = CircleShape,
            color = CustomerHomeColors.OliveSoft
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = leadingText,
                    style = MaterialTheme.typography.titleMedium,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = CustomerHomeColors.DeepOlive,
            fontWeight = FontWeight.Bold
        )

        if (!trailingText.isNullOrBlank()) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.labelMedium,
                color = CustomerHomeColors.TextMuted
            )
        }
    }
}

@Composable
private fun OrderSuccessContent(
    orderId: Int,
    totalPrice: Double,
    status: String,
    onReturnHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomerHomeColors.Cream)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(84.dp),
            shape = CircleShape,
            color = CustomerHomeColors.OliveSoft
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.headlineLarge,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Siparişiniz Oluşturuldu",
            style = MaterialTheme.typography.headlineSmall,
            color = CustomerHomeColors.DeepOlive,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Sipariş No: #$orderId",
            style = MaterialTheme.typography.titleMedium,
            color = CustomerHomeColors.Text
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = "Tutar: ${formatPrice(totalPrice)}",
            style = MaterialTheme.typography.bodyLarge,
            color = CustomerHomeColors.Terracotta,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Durum: ${translateOrderStatus(status)}",
            style = MaterialTheme.typography.bodyMedium,
            color = CustomerHomeColors.TextMuted
        )

        Spacer(Modifier.height(26.dp))

        Button(
            onClick = onReturnHomeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomerHomeColors.DeepOlive
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Ana Sayfaya Dön",
                fontWeight = FontWeight.Bold
            )
        }
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

private fun translateOrderStatus(
    status: String
): String =
    OrderStatus.displayNameFor(status)
