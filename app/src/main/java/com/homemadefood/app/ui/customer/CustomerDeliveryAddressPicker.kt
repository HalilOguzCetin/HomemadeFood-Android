package com.homemadefood.app.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.homemadefood.app.R
import com.homemadefood.app.data.model.AddressResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDeliveryAddressPicker(
    isLoading: Boolean,
    addresses: List<AddressResponse>,
    selectedAddress: AddressResponse?,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    onAddressSelected: (Int) -> Unit,
    onAddAddressClick: () -> Unit,
    onManageAddressesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddressSheet by
    rememberSaveable {
        mutableStateOf(false)
    }

    when {
        isLoading &&
                selectedAddress == null -> {

            AddressShell(
                modifier = modifier
            ) {
                CircularProgressIndicator(
                    modifier =
                        Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color =
                        CustomerHomeColors
                            .DeepOlive
                )

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(
                                start = 12.dp
                            )
                ) {
                    Text(
                        text = "Teslimat Adresi",
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        color =
                            CustomerHomeColors
                                .TextMuted
                    )

                    Text(
                        text =
                            "Adresleriniz yükleniyor...",
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            CustomerHomeColors
                                .Text
                    )
                }
            }
        }

        selectedAddress != null -> {
            Card(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = !isLoading,
                            onClick = {
                                showAddressSheet = true
                            }
                        ),

                shape =
                    RoundedCornerShape(
                        20.dp
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            CustomerHomeColors
                                .Surface
                    ),

                border =
                    BorderStroke(
                        width = 1.dp,
                        color =
                            CustomerHomeColors
                                .Outline
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 14.dp,
                                vertical = 11.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Surface(
                        modifier =
                            Modifier.size(38.dp),
                        shape = CircleShape,
                        color =
                            CustomerHomeColors
                                .OliveSoft
                    ) {
                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Icon(
                                painter =
                                    painterResource(
                                        id =
                                            R.drawable
                                                .ic_customer_home_location
                                    ),
                                contentDescription = null,
                                tint =
                                    CustomerHomeColors
                                        .DeepOlive,
                                modifier =
                                    Modifier.size(
                                        21.dp
                                    )
                            )
                        }
                    }

                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    start = 11.dp,
                                    end = 8.dp
                                )
                    ) {
                        Text(
                            text = "Teslimat Adresi",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall,
                            color =
                                CustomerHomeColors
                                    .TextMuted
                        )

                        Spacer(
                            modifier =
                                Modifier.size(2.dp)
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Text(
                                text =
                                    selectedAddress.title,
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleSmall,
                                fontWeight =
                                    FontWeight
                                        .SemiBold,
                                color =
                                    CustomerHomeColors
                                        .Text,
                                maxLines = 1,
                                overflow =
                                    TextOverflow
                                        .Ellipsis
                            )

                            Text(
                                text = " • ",
                                color =
                                    CustomerHomeColors
                                        .TextMuted
                            )

                            Text(
                                text =
                                    deliveryAddressSummary(
                                        selectedAddress
                                    ),
                                modifier =
                                    Modifier.weight(1f),
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,
                                color =
                                    CustomerHomeColors
                                        .TextMuted,
                                maxLines = 1,
                                overflow =
                                    TextOverflow
                                        .Ellipsis
                            )
                        }
                    }

                    if (
                        selectedAddress.isDefault
                    ) {
                        Surface(
                            shape =
                                RoundedCornerShape(
                                    50.dp
                                ),
                            color =
                                CustomerHomeColors
                                    .OliveSoft
                        ) {
                            Text(
                                text = "✓ Varsayılan",
                                modifier =
                                    Modifier.padding(
                                        horizontal =
                                            8.dp,
                                        vertical =
                                            5.dp
                                    ),
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,
                                color =
                                    CustomerHomeColors
                                        .DeepOlive,
                                fontWeight =
                                    FontWeight
                                        .SemiBold
                            )
                        }
                    }

                    Icon(
                        painter =
                            painterResource(
                                id =
                                    R.drawable
                                        .ic_customer_home_chevron
                            ),
                        contentDescription =
                            "Teslimat adresini değiştir",
                        tint =
                            CustomerHomeColors
                                .DeepOlive,
                        modifier =
                            Modifier
                                .padding(
                                    start = 5.dp
                                )
                                .size(20.dp)
                    )
                }
            }

            if (
                !errorMessage.isNullOrBlank()
            ) {
                TextButton(
                    onClick = onRetryClick,
                    enabled = !isLoading
                ) {
                    Text(
                        "Adresleri Tekrar Yükle"
                    )
                }
            }
        }

        !errorMessage.isNullOrBlank() -> {
            Card(
                modifier =
                    modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(
                        20.dp
                    ),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            CustomerHomeColors
                                .Surface
                    ),
                border =
                    BorderStroke(
                        1.dp,
                        CustomerHomeColors
                            .Outline
                    )
            ) {
                Column(
                    modifier =
                        Modifier.padding(
                            14.dp
                        )
                ) {
                    Text(
                        text = "Teslimat Adresi",
                        style =
                            MaterialTheme
                                .typography
                                .titleSmall,
                        fontWeight =
                            FontWeight.SemiBold,
                        color =
                            CustomerHomeColors
                                .Text
                    )

                    Spacer(
                        modifier =
                            Modifier.size(5.dp)
                    )

                    Text(
                        text = errorMessage,
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )

                    Spacer(
                        modifier =
                            Modifier.size(10.dp)
                    )

                    Button(
                        onClick = onRetryClick,
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Tekrar Dene"
                        )
                    }
                }
            }
        }

        addresses.isEmpty() -> {
            Card(
                modifier =
                    modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(
                        20.dp
                    ),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            CustomerHomeColors
                                .Surface
                    ),
                border =
                    BorderStroke(
                        1.dp,
                        CustomerHomeColors
                            .Outline
                    )
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 14.dp,
                                vertical = 12.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Icon(
                        painter =
                            painterResource(
                                id =
                                    R.drawable
                                        .ic_customer_home_location
                            ),
                        contentDescription = null,
                        tint =
                            CustomerHomeColors
                                .DeepOlive,
                        modifier =
                            Modifier.size(22.dp)
                    )

                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    horizontal =
                                        10.dp
                                )
                    ) {
                        Text(
                            text = "Teslimat Adresi",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall,
                            color =
                                CustomerHomeColors
                                    .TextMuted
                        )

                        Text(
                            text =
                                "Henüz kayıtlı adresiniz yok.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color =
                                CustomerHomeColors
                                    .Text
                        )
                    }

                    TextButton(
                        onClick =
                            onAddAddressClick
                    ) {
                        Text(
                            "Ekle",
                            color =
                                CustomerHomeColors
                                    .Terracotta,
                            fontWeight =
                                FontWeight
                                    .SemiBold
                        )
                    }
                }
            }
        }
    }

    if (showAddressSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showAddressSheet = false
            },
            containerColor =
                CustomerHomeColors
                    .Surface
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 24.dp
                        )
            ) {
                Text(
                    text =
                        "Teslimat adresini seç",
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.size(6.dp)
                )

                Text(
                    text =
                        "Bu seçim yalnız şu anki teslimat adresinizi değiştirir. Varsayılan adres ayarınız değişmez.",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted
                )

                Spacer(
                    modifier =
                        Modifier.size(18.dp)
                )

                addresses.forEach {
                        address ->

                    DeliveryAddressOption(
                        address =
                            address,
                        selected =
                            selectedAddress
                                ?.id ==
                                    address.id,
                        onClick = {
                            onAddressSelected(
                                address.id
                            )

                            showAddressSheet =
                                false
                        }
                    )
                }

                HorizontalDivider(
                    modifier =
                        Modifier.padding(
                            vertical = 10.dp
                        )
                )

                TextButton(
                    onClick = {
                        showAddressSheet =
                            false
                        onAddAddressClick()
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text(
                        "+ Yeni adres ekle",
                        color =
                            CustomerHomeColors
                                .Terracotta
                    )
                }

                TextButton(
                    onClick = {
                        showAddressSheet =
                            false
                        onManageAddressesClick()
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Adreslerimi yönet",
                        color =
                            CustomerHomeColors
                                .DeepOlive
                    )
                }

                Spacer(
                    modifier =
                        Modifier.size(8.dp)
                )
            }
        }
    }
}

@Composable
private fun AddressShell(
    modifier: Modifier,
    content:
    @Composable
    androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    Card(
        modifier =
            modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                20.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    CustomerHomeColors
                        .Surface
            ),
        border =
            BorderStroke(
                1.dp,
                CustomerHomeColors
                    .Outline
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
private fun DeliveryAddressOption(
    address: AddressResponse,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                )
                .padding(
                    vertical = 10.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(
                        start = 8.dp
                    )
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = address.title,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    color =
                        CustomerHomeColors
                            .Text
                )

                if (address.isDefault) {
                    Text(
                        text =
                            "  • Varsayılan",
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
                        color =
                            CustomerHomeColors
                                .DeepOlive
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.size(3.dp)
            )

            Text(
                text =
                    deliveryAddressSummary(
                        address
                    ),
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    CustomerHomeColors
                        .TextMuted,
                maxLines = 2,
                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}

private fun deliveryAddressSummary(
    address: AddressResponse
): String {
    val locationSummary =
        listOf(
            address.neighborhood,
            address.district,
            address.city
        )
            .map {
                it.trim()
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()
            .joinToString(
                " / "
            )

    if (locationSummary.isNotBlank()) {
        return locationSummary
    }

    return address.fullAddress
        .trim()
        .ifBlank {
            "Adres ayrıntısı bulunmuyor"
        }
}