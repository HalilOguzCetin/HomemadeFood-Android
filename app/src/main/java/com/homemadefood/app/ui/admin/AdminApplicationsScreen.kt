package com.homemadefood.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.PendingProducerResponse
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminApplicationsScreen(
    uiState: AdminApplicationsUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onApproveClick: (Int) -> Unit,
    onRejectClick: (Int, String) -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedApplicationForReject by remember {
        mutableStateOf<PendingProducerResponse?>(null)
    }

    var rejectReason by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        TextButton(
            onClick = onBackClick
        ) {
            Text("← Admin Paneline Dön")
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Üretici Başvuruları",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Onay bekleyen üretici başvurularını inceleyebilir, onaylayabilir veya reddedebilirsiniz.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        uiState.successMessage?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    TextButton(
                        onClick = onClearMessage
                    ) {
                        Text("Kapat")
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        if (
            uiState.errorMessage != null &&
            uiState.applications.isNotEmpty()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    TextButton(
                        onClick = onClearMessage
                    ) {
                        Text("Kapat")
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null &&
                    uiState.applications.isEmpty() -> {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Button(
                        onClick = onRetryClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tekrar Dene")
                    }
                }
            }

            uiState.applications.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Onay bekleyen üretici başvurusu bulunmuyor.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = uiState.applications,
                        key = { application ->
                            application.producerProfileId
                        }
                    ) { application ->

                        AdminApplicationCard(
                            application = application,

                            isUpdating =
                                uiState.updatingApplicationId ==
                                        application.producerProfileId,

                            isAnyApplicationUpdating =
                                uiState.updatingApplicationId != null,

                            onApproveClick = {
                                onApproveClick(
                                    application.producerProfileId
                                )
                            },

                            onRejectClick = {
                                rejectReason = ""

                                selectedApplicationForReject =
                                    application
                            }
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }

    selectedApplicationForReject?.let { application ->

        AlertDialog(
            onDismissRequest = {
                if (
                    uiState.updatingApplicationId == null
                ) {
                    selectedApplicationForReject = null
                    rejectReason = ""
                }
            },

            title = {
                Text("Başvuruyu Reddet")
            },

            text = {
                Column {
                    Text(
                        text =
                            "${application.businessName} başvurusunu reddetme nedeninizi yazın."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedTextField(
                        value = rejectReason,

                        onValueChange = { newValue ->
                            if (newValue.length <= 500) {
                                rejectReason = newValue
                            }
                        },

                        label = {
                            Text("Red nedeni")
                        },

                        supportingText = {
                            Text(
                                "${rejectReason.length}/500 karakter"
                            )
                        },

                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (
                        rejectReason.isNotBlank() &&
                        rejectReason.trim().length < 10
                    ) {
                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Red nedeni en az 10 karakter olmalıdır.",

                            color =
                                MaterialTheme.colorScheme.error,

                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },

            confirmButton = {
                Button(
                    onClick = {
                        onRejectClick(
                            application.producerProfileId,
                            rejectReason
                        )

                        selectedApplicationForReject = null
                        rejectReason = ""
                    },

                    enabled =
                        rejectReason.trim().length in 10..500 &&
                                uiState.updatingApplicationId == null
                ) {
                    Text("Başvuruyu Reddet")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        selectedApplicationForReject = null
                        rejectReason = ""
                    },

                    enabled =
                        uiState.updatingApplicationId == null
                ) {
                    Text("Vazgeç")
                }
            }
        )
    }
}

@Composable
private fun AdminApplicationCard(
    application: PendingProducerResponse,
    isUpdating: Boolean,
    isAnyApplicationUpdating: Boolean,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = application.businessName,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    translateApplicationStatus(
                        application.verificationStatus
                    ),

                color =
                    MaterialTheme.colorScheme.tertiary,

                style =
                    MaterialTheme.typography.titleSmall
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminApplicationInformation(
                title = "Başvuru numarası",
                value =
                    application.producerProfileId.toString()
            )

            AdminApplicationInformation(
                title = "Kullanıcı numarası",
                value = application.userId.toString()
            )

            AdminApplicationInformation(
                title = "Başvuru sahibi",
                value = application.fullName
            )

            AdminApplicationInformation(
                title = "E-posta",
                value = application.email
            )

            AdminApplicationInformation(
                title = "Günlük kapasite",
                value = "${application.dailyCapacity} adet"
            )

            AdminApplicationInformation(
                title = "Başvuru tarihi",
                value =
                    formatAdminApplicationDate(
                        application.createdAt
                    )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "İşletme Açıklaması",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = application.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "İşletme Adresi",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = application.address,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            if (isUpdating) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),

                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Button(
                    onClick = onApproveClick,

                    enabled =
                        !isAnyApplicationUpdating,

                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text("Onayla")
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedButton(
                    onClick = onRejectClick,

                    enabled =
                        !isAnyApplicationUpdating,

                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text("Reddet")
                }
            }
        }
    }
}

@Composable
private fun AdminApplicationInformation(
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
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

private fun translateApplicationStatus(
    status: String
): String {
    return when {
        status.equals(
            "Pending",
            ignoreCase = true
        ) -> "Onay Bekliyor"

        status.equals(
            "Approved",
            ignoreCase = true
        ) -> "Onaylandı"

        status.equals(
            "Rejected",
            ignoreCase = true
        ) -> "Reddedildi"

        else -> status
    }
}

private fun formatAdminApplicationDate(
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