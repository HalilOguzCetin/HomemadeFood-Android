package com.homemadefood.app.ui.customer

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CustomerProducerApplicationScreen(
    uiState: CustomerProducerApplicationUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onBusinessNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onDailyCapacityChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onShowReapplicationFormClick: () -> Unit,
    onHideReapplicationFormClick: () -> Unit,
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

        uiState.application == null &&
                !uiState.isFormVisible -> {

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
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text =
                        uiState.errorMessage
                            ?: "Başvuru bilgisi alınamadı.",

                    color =
                        MaterialTheme.colorScheme.error
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
                    enabled = !uiState.isSubmitting
                ) {
                    Text("← Ana Sayfaya Dön")
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Üretici Ol",
                    style =
                        MaterialTheme.typography
                            .headlineMedium
                )

                Text(
                    text =
                        "Ev yapımı yemeklerinizi satışa sunmak için üretici başvurunuzu yönetin.",

                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )

                if (
                    !uiState.successMessage
                        .isNullOrBlank()
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    Text(
                        text =
                            uiState.successMessage,

                        color =
                            MaterialTheme.colorScheme
                                .primary
                    )
                }

                if (
                    !uiState.errorMessage
                        .isNullOrBlank()
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    Text(
                        text =
                            uiState.errorMessage,

                        color =
                            MaterialTheme.colorScheme
                                .error
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                if (uiState.isFormVisible) {
                    ProducerApplicationForm(
                        uiState = uiState,

                        onBusinessNameChange =
                            onBusinessNameChange,

                        onDescriptionChange =
                            onDescriptionChange,

                        onAddressChange =
                            onAddressChange,

                        onLatitudeChange =
                            onLatitudeChange,

                        onLongitudeChange =
                            onLongitudeChange,

                        onDailyCapacityChange =
                            onDailyCapacityChange,

                        onSubmitClick =
                            onSubmitClick,

                        onCancelClick =
                            onHideReapplicationFormClick,

                        showCancelButton =
                            uiState.application != null
                    )
                } else {
                    val application =
                        uiState.application

                    if (application != null) {
                        ProducerApplicationStatusContent(
                            application =
                                application,

                            onShowReapplicationFormClick =
                                onShowReapplicationFormClick
                        )
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
private fun ProducerApplicationForm(
    uiState: CustomerProducerApplicationUiState,
    onBusinessNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onDailyCapacityChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onCancelClick: () -> Unit,
    showCancelButton: Boolean
) {
    Text(
        text =
            if (showCancelButton) {
                "Yeniden Başvuru Formu"
            } else {
                "Üretici Başvuru Formu"
            },

        style =
            MaterialTheme.typography
                .titleLarge
    )

    Spacer(
        modifier = Modifier.height(14.dp)
    )

    OutlinedTextField(
        value = uiState.businessName,
        onValueChange = onBusinessNameChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("İşletme Adı")
        },
        supportingText = {
            Text(
                "${uiState.businessName.length}/150"
            )
        },
        singleLine = true,
        enabled = !uiState.isSubmitting
    )

    Spacer(
        modifier = Modifier.height(12.dp)
    )

    OutlinedTextField(
        value = uiState.description,
        onValueChange = onDescriptionChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("İşletme Açıklaması")
        },
        placeholder = {
            Text(
                "Hazırladığınız yemekleri ve işletmenizi tanıtın."
            )
        },
        supportingText = {
            Text(
                "${uiState.description.length}/1000"
            )
        },
        minLines = 4,
        maxLines = 7,
        enabled = !uiState.isSubmitting
    )

    Spacer(
        modifier = Modifier.height(12.dp)
    )

    OutlinedTextField(
        value = uiState.address,
        onValueChange = onAddressChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("İşletme Adresi")
        },
        supportingText = {
            Text(
                "${uiState.address.length}/500"
            )
        },
        minLines = 3,
        maxLines = 5,
        enabled = !uiState.isSubmitting
    )

    Spacer(
        modifier = Modifier.height(12.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = uiState.latitudeText,
            onValueChange = onLatitudeChange,
            modifier = Modifier.weight(1f),
            label = {
                Text("Enlem")
            },
            placeholder = {
                Text("39.93")
            },
            singleLine = true,
            enabled = !uiState.isSubmitting,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Decimal
                )
        )

        OutlinedTextField(
            value = uiState.longitudeText,
            onValueChange = onLongitudeChange,
            modifier = Modifier.weight(1f),
            label = {
                Text("Boylam")
            },
            placeholder = {
                Text("26.40")
            },
            singleLine = true,
            enabled = !uiState.isSubmitting,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Decimal
                )
        )
    }

    Spacer(
        modifier = Modifier.height(12.dp)
    )

    OutlinedTextField(
        value = uiState.dailyCapacityText,
        onValueChange = onDailyCapacityChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("Günlük Kapasite")
        },
        placeholder = {
            Text("Örnek: 50")
        },
        supportingText = {
            Text(
                "Günde hazırlayabileceğiniz toplam ürün sayısı"
            )
        },
        singleLine = true,
        enabled = !uiState.isSubmitting,
        keyboardOptions =
            KeyboardOptions(
                keyboardType =
                    KeyboardType.Number
            )
    )

    Spacer(
        modifier = Modifier.height(22.dp)
    )

    Button(
        onClick = onSubmitClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !uiState.isSubmitting
    ) {
        if (uiState.isSubmitting) {
            CircularProgressIndicator(
                modifier =
                    Modifier.height(22.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                if (showCancelButton) {
                    "Yeniden Başvur"
                } else {
                    "Başvuruyu Gönder"
                }
            )
        }
    }

    if (showCancelButton) {
        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedButton(
            onClick = onCancelClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSubmitting
        ) {
            Text("Formu Kapat")
        }
    }
}

@Composable
private fun ProducerApplicationStatusContent(
    application:
    ProducerApplicationStatusResponse,

    onShowReapplicationFormClick:
        () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = application.businessName,
                style =
                    MaterialTheme.typography
                        .headlineSmall
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    translateVerificationStatus(
                        application
                            .verificationStatus
                    ),

                color =
                    getVerificationStatusColor(
                        application
                            .verificationStatus
                    ),

                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            ApplicationInformationRow(
                title = "Başvuru tarihi",
                value =
                    formatApplicationDate(
                        application.createdAt
                    )
            )

            ApplicationInformationRow(
                title = "Günlük kapasite",
                value =
                    "${application.dailyCapacity} adet"
            )

            ApplicationInformationRow(
                title = "İşletme adresi",
                value = application.address
            )
        }
    }

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    when {
        application.verificationStatus.equals(
            "Pending",
            ignoreCase = true
        ) -> {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text =
                        "Başvurunuz yönetici incelemesindedir. Sonuçlandığında başvuru durumunuz güncellenecektir.",

                    modifier =
                        Modifier.padding(16.dp),

                    style =
                        MaterialTheme.typography
                            .bodyLarge
                )
            }
        }

        application.verificationStatus.equals(
            "Approved",
            ignoreCase = true
        ) -> {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text =
                        "Başvurunuz onaylandı. Artık üretici işlemlerine erişebilirsiniz.",

                    modifier =
                        Modifier.padding(16.dp),

                    color =
                        MaterialTheme.colorScheme
                            .primary,

                    style =
                        MaterialTheme.typography
                            .bodyLarge
                )
            }
        }

        application.verificationStatus.equals(
            "Rejected",
            ignoreCase = true
        ) -> {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Başvuru Red Nedeni",
                        color =
                            MaterialTheme.colorScheme
                                .error,

                        style =
                            MaterialTheme.typography
                                .titleLarge
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            application.rejectionReason
                                ?: "Red nedeni belirtilmedi.",

                        style =
                            MaterialTheme.typography
                                .bodyLarge
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Button(
                onClick =
                    onShowReapplicationFormClick,

                modifier =
                    Modifier.fillMaxWidth()
            ) {
                Text("Bilgileri Düzenleyerek Yeniden Başvur")
            }
        }
    }
}

@Composable
private fun ApplicationInformationRow(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
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
private fun getVerificationStatusColor(
    status: String
) = when {
    status.equals(
        "Pending",
        ignoreCase = true
    ) ->
        MaterialTheme.colorScheme.tertiary

    status.equals(
        "Approved",
        ignoreCase = true
    ) ->
        MaterialTheme.colorScheme.primary

    status.equals(
        "Rejected",
        ignoreCase = true
    ) ->
        MaterialTheme.colorScheme.error

    else ->
        MaterialTheme.colorScheme.onSurface
}

private fun translateVerificationStatus(
    status: String
): String {
    return when {
        status.equals(
            "Pending",
            ignoreCase = true
        ) ->
            "Onay Bekliyor"

        status.equals(
            "Approved",
            ignoreCase = true
        ) ->
            "Onaylandı"

        status.equals(
            "Rejected",
            ignoreCase = true
        ) ->
            "Reddedildi"

        else ->
            status
    }
}

private fun formatApplicationDate(
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