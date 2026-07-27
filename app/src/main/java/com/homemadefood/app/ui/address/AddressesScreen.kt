package com.homemadefood.app.ui.address

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.AddressResponse

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
            text = "Adreslerim",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = onAddAddressClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Yeni Adres Ekle")
        }

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

            uiState.errorMessage != null -> {
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

            uiState.addresses.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz kayıtlı adresiniz bulunmuyor.",
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

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.addresses,
                        key = { address ->
                            address.id
                        }
                    ) { address ->
                        AddressCard(
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

@Composable
private fun AddressCard(
    address: AddressResponse,
    isDeleting: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
){
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
                    text = address.title,
                    style = MaterialTheme.typography.titleLarge
                )

                if (address.isDefault) {
                    Text(
                        text = "Varsayılan",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = address.fullAddress,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AddressInformationRow(
                title = "Enlem",
                value = address.latitude.toString()
            )

            AddressInformationRow(
                title = "Boylam",
                value = address.longitude.toString()
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isDeleting
            ) {
                Text("Adresi Düzenle")
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = onDeleteClick,
                enabled = !isDeleting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Adresi Sil")
                }
            }
        }
    }
}

@Composable
private fun AddressInformationRow(
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