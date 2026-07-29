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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ProducerApplicationScreen(
    uiState: ProducerApplicationUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
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

        uiState.application == null -> {
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
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text =
                        uiState.errorMessage
                            ?: "Üretici başvuru bilgisi bulunamadı.",
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
            val application =
                uiState.application

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp),

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {
                TextButton(
                    onClick = onBackClick
                ) {
                    Text("← Üretici Paneline Dön")
                }

                Text(
                    text = "Üretici Başvurum",
                    style =
                        MaterialTheme.typography.headlineMedium
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = application.businessName,
                            style =
                                MaterialTheme.typography.headlineSmall
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                translateVerificationStatus(
                                    application.verificationStatus
                                ),

                            color =
                                getVerificationStatusColor(
                                    application.verificationStatus
                                ),

                            style =
                                MaterialTheme.typography.titleLarge
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        ProducerApplicationInformationRow(
                            title = "Profil numarası",
                            value =
                                application.producerProfileId
                                    .toString()
                        )

                        ProducerApplicationInformationRow(
                            title = "Onaylı",
                            value =
                                if (application.isApproved) {
                                    "Evet"
                                } else {
                                    "Hayır"
                                }
                        )

                        ProducerApplicationInformationRow(
                            title = "Satış durumu",
                            value =
                                if (application.isAvailable) {
                                    "Aktif"
                                } else {
                                    "Pasif"
                                }
                        )

                        ProducerApplicationInformationRow(
                            title = "Başvuru tarihi",
                            value =
                                formatProducerDate(
                                    application.createdAt
                                )
                        )

                        if (
                            !application.approvedAt
                                .isNullOrBlank()
                        ) {
                            ProducerApplicationInformationRow(
                                title = "Onay tarihi",
                                value =
                                    formatProducerDate(
                                        application.approvedAt
                                    )
                            )
                        }

                        if (
                            !application.rejectedAt
                                .isNullOrBlank()
                        ) {
                            ProducerApplicationInformationRow(
                                title = "Red tarihi",
                                value =
                                    formatProducerDate(
                                        application.rejectedAt
                                    )
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "İşletme Bilgileri",
                            style =
                                MaterialTheme.typography.titleLarge
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        if (
                            application.description
                                .isNotBlank()
                        ) {
                            Text(
                                text = application.description,
                                style =
                                    MaterialTheme.typography.bodyLarge
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            HorizontalDivider()

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )
                        }

                        Text(
                            text = "Adres",
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text = application.address,
                            style =
                                MaterialTheme.typography.bodyLarge
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text =
                                "Konum: " +
                                        "${application.latitude}, " +
                                        application.longitude,

                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Kapasite Bilgileri",
                            style =
                                MaterialTheme.typography.titleLarge
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        ProducerApplicationInformationRow(
                            title = "Günlük kapasite",
                            value =
                                "${application.dailyCapacity} adet"
                        )

                        ProducerApplicationInformationRow(
                            title = "Kalan kapasite",
                            value =
                                "${application.remainingCapacity} adet"
                        )
                    }
                }

                if (
                    !application.rejectionReason
                        .isNullOrBlank()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Başvuru Red Nedeni",
                                color =
                                    MaterialTheme.colorScheme.error,
                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    application.rejectionReason,

                                style =
                                    MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ProducerApplicationInformationRow(
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
private fun getVerificationStatusColor(
    status: String
) = when (status) {
    "Pending" ->
        MaterialTheme.colorScheme.tertiary

    "Approved" ->
        MaterialTheme.colorScheme.primary

    "Rejected" ->
        MaterialTheme.colorScheme.error

    else ->
        MaterialTheme.colorScheme.onSurface
}

private fun translateVerificationStatus(
    status: String
): String {
    return when (status) {
        "Pending" -> "Onay Bekliyor"
        "Approved" -> "Onaylandı"
        "Rejected" -> "Reddedildi"
        else -> status
    }
}

private fun formatProducerDate(
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