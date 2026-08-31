package com.homemadefood.app.ui.address

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.ui.components.AppEmptyState
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.AppMessageType
import com.homemadefood.app.ui.customer.CustomerHomeColors
import com.homemadefood.app.ui.customer.CustomerHomeTheme

@Composable
fun AddressesScreen(
    uiState: AddressesUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onDeleteAddressClick: (Int) -> Unit,
    onEditAddressClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CustomerHomeColors.Cream)
        ) {
            AddressesHeader(
                addressCount = uiState.addresses.size,
                onBackClick = onBackClick,
                onAddAddressClick = onAddAddressClick
            )

            when {
                uiState.isLoading -> {
                    AppLoadingState(
                        message = "Adresleriniz yükleniyor...",
                        modifier = Modifier.weight(1f)
                    )
                }

                uiState.errorMessage != null &&
                        uiState.addresses.isEmpty() -> {
                    AppErrorState(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                uiState.addresses.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        AppEmptyState(
                            title = "Henüz kayıtlı adresiniz yok",
                            message = "Siparişleriniz için ilk teslimat adresinizi ekleyebilirsiniz.",
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = onAddAddressClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 18.dp
                                )
                                .height(54.dp),
                            shape = RoundedCornerShape(17.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CustomerHomeColors.DeepOlive,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "+  Yeni Adres Ekle",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 4.dp,
                            bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!uiState.actionMessage.isNullOrBlank()) {
                            item(
                                key = "address_action_message"
                            ) {
                                AppInlineMessage(
                                    message = uiState.actionMessage,
                                    type = AppMessageType.Success
                                )
                            }
                        }

                        if (!uiState.errorMessage.isNullOrBlank()) {
                            item(
                                key = "address_inline_error"
                            ) {
                                AppInlineMessage(
                                    message = uiState.errorMessage,
                                    type = AppMessageType.Error
                                )
                            }
                        }

                        item(
                            key = "address_list_title"
                        ) {
                            Column {
                                Text(
                                    text = "Kayıtlı Adresler",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = CustomerHomeColors.DeepOlive,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Sipariş verirken kullanacağınız adresleri buradan yönetebilirsiniz.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CustomerHomeColors.TextMuted
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )
                            }
                        }

                        items(
                            items = uiState.addresses,
                            key = { address ->
                                address.id
                            }
                        ) { address ->
                            CustomerAddressCard(
                                address = address,
                                isDeleting =
                                    uiState.deletingAddressId ==
                                            address.id,
                                onEditClick = {
                                    onEditAddressClick(
                                        address.id
                                    )
                                },
                                onDeleteClick = {
                                    onDeleteAddressClick(
                                        address.id
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressesHeader(
    addressCount: Int,
    onBackClick: () -> Unit,
    onAddAddressClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 14.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            contentPadding = PaddingValues(
                horizontal = 0.dp,
                vertical = 4.dp
            )
        ) {
            Text(
                text = "←  Hesabıma Dön",
                color = CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Adreslerim",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        if (addressCount == 0) {
                            "Teslimat adreslerinizi yönetin"
                        } else {
                            "$addressCount kayıtlı teslimat adresi"
                        },
                    style = MaterialTheme.typography.bodyMedium,
                    color = CustomerHomeColors.TextMuted
                )
            }

            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = CustomerHomeColors.OliveSoft
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = addressCount.toString(),
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = onAddAddressClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomerHomeColors.DeepOlive,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "+  Yeni Adres Ekle",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CustomerAddressCard(
    address: AddressResponse,
    isDeleting: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border = BorderStroke(
            width = 1.dp,
            color = CustomerHomeColors.Outline
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color =
                        if (address.isDefault) {
                            CustomerHomeColors.OliveSoft
                        } else {
                            CustomerHomeColors.SurfaceSoft
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = addressIconText(
                                address.title
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = CustomerHomeColors.DeepOlive,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = address.title,
                            modifier = Modifier.weight(
                                weight = 1f,
                                fill = false
                            ),
                            style =
                                MaterialTheme.typography.titleMedium,
                            color = CustomerHomeColors.Text,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (address.isDefault) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color =
                                    CustomerHomeColors.OliveSoft
                            ) {
                                Text(
                                    text = "Varsayılan",
                                    modifier = Modifier.padding(
                                        horizontal = 9.dp,
                                        vertical = 4.dp
                                    ),
                                    style =
                                        MaterialTheme.typography.labelSmall,
                                    color =
                                        CustomerHomeColors.DeepOlive,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    val locationText =
                        listOf(
                            address.neighborhood,
                            address.district,
                            address.city
                        )
                            .filter {
                                it.isNotBlank()
                            }
                            .distinct()
                            .joinToString(" • ")

                    if (locationText.isNotBlank()) {
                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = locationText,
                            style =
                                MaterialTheme.typography.bodySmall,
                            color =
                                CustomerHomeColors.Terracotta,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = address.fullAddress,
                style = MaterialTheme.typography.bodyMedium,
                color = CustomerHomeColors.Text,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            if (!address.addressNote.isNullOrBlank()) {
                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    color = CustomerHomeColors.SurfaceSoft
                ) {
                    Text(
                        text = "Not: ${address.addressNote}",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 9.dp
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = CustomerHomeColors.TextMuted
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    enabled = !isDeleting,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color =
                            CustomerHomeColors.DeepOlive
                                .copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text = "Düzenle",
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onDeleteClick,
                    enabled = !isDeleting,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color =
                            CustomerHomeColors.Error
                                .copy(alpha = 0.35f)
                    )
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(19.dp),
                            strokeWidth = 2.dp,
                            color = CustomerHomeColors.Error
                        )
                    } else {
                        Text(
                            text = "Sil",
                            color = CustomerHomeColors.Error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun addressIconText(
    title: String
): String {
    val normalized =
        title.trim().lowercase()

    return when {
        "ev" in normalized -> "⌂"
        "iş" in normalized ||
                "ofis" in normalized -> "▣"
        else -> "⌖"
    }
}
