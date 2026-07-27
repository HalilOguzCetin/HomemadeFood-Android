package com.homemadefood.app.ui.address

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EditAddressScreen(
    uiState: EditAddressUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onFullAddressChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onIsDefaultChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null &&
                uiState.title.isBlank() -> {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                TextButton(
                    onClick = onBackClick
                ) {
                    Text("← Adreslerime Dön")
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
                    enabled = !uiState.isSaving
                ) {
                    Text("← Adreslerime Dön")
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Adresi Düzenle",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Adres Başlığı")
                    },
                    placeholder = {
                        Text("Ev, İş, Okul...")
                    },
                    singleLine = true,
                    enabled = !uiState.isSaving
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                OutlinedTextField(
                    value = uiState.fullAddress,
                    onValueChange = onFullAddressChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Açık Adres")
                    },
                    placeholder = {
                        Text(
                            "Mahalle, sokak, bina ve daire bilgilerini girin."
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    enabled = !uiState.isSaving
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                OutlinedTextField(
                    value = uiState.latitude,
                    onValueChange = onLatitudeChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Enlem")
                    },
                    singleLine = true,
                    enabled = !uiState.isSaving,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                OutlinedTextField(
                    value = uiState.longitude,
                    onValueChange = onLongitudeChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Boylam")
                    },
                    singleLine = true,
                    enabled = !uiState.isSaving,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.isDefault,
                        onCheckedChange = onIsDefaultChange,
                        enabled = !uiState.isSaving
                    )

                    Text(
                        text = "Bu adresi varsayılan adresim yap"
                    )
                }

                if (!uiState.errorMessage.isNullOrBlank()) {
                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Değişiklikleri Kaydet")
                    }
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}