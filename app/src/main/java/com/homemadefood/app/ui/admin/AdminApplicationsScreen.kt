package com.homemadefood.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.homemadefood.app.data.model.AdminProducerApplicationResponse
import com.homemadefood.app.data.remote.ApiConfig
import com.homemadefood.app.data.model.ProducerApplicationStatus
import com.homemadefood.app.ui.components.AppEmptyState
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.AppMessageType
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminApplicationsScreen(
    uiState: AdminApplicationsUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onStatusSelected:
        (ProducerApplicationStatus) -> Unit,
    onApproveClick: (Int) -> Unit,
    onRejectClick: (Int, String) -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedApplicationForReject by
    remember {
        mutableStateOf<
                AdminProducerApplicationResponse?
                >(null)
    }

    var rejectReason by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            enabled =
                uiState.updatingApplicationId == null
        ) {
            Text("← Admin Paneline Dön")
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Üretici Başvuruları",
            style =
                MaterialTheme.typography
                    .headlineMedium
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text =
                applicationScreenDescription(
                    status = uiState.selectedStatus
                ),
            style =
                MaterialTheme.typography
                    .bodyMedium
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        ApplicationStatusTabs(
            selectedStatus =
                uiState.selectedStatus,

            isEnabled =
                !uiState.isLoading &&
                        uiState.updatingApplicationId ==
                        null,

            onStatusSelected =
                onStatusSelected
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        uiState.successMessage?.let { message ->
            AppInlineMessage(
                message = message,
                type = AppMessageType.Success
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        if (
            uiState.errorMessage != null &&
            uiState.applications.isNotEmpty()
        ) {
            AppInlineMessage(
                message = uiState.errorMessage,
                type = AppMessageType.Error
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        when {
            uiState.isLoading -> {
                AppLoadingState(
                    message = "Başvurular yükleniyor..."
                )
            }

            uiState.errorMessage != null &&
                    uiState.applications.isEmpty() -> {

                AppErrorState(
                    message = uiState.errorMessage,
                    onRetryClick = onRetryClick
                )
            }

            uiState.applications.isEmpty() -> {
                AppEmptyState(
                    title = "Başvuru bulunamadı",
                    message = uiState.emptyMessage
                )
            }

            else -> {
                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize(),

                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items =
                            uiState.applications,

                        key = { application ->
                            application
                                .producerProfileId
                        }
                    ) { application ->

                        AdminApplicationCard(
                            application =
                                application,

                            selectedStatus =
                                uiState.selectedStatus,

                            isUpdating =
                                uiState
                                    .updatingApplicationId ==
                                        application
                                            .producerProfileId,

                            isAnyApplicationUpdating =
                                uiState
                                    .updatingApplicationId !=
                                        null,

                            onApproveClick = {
                                onApproveClick(
                                    application
                                        .producerProfileId
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
                            modifier =
                                Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }

    selectedApplicationForReject
        ?.let { application ->

            RejectApplicationDialog(
                application =
                    application,

                rejectReason =
                    rejectReason,

                isUpdating =
                    uiState
                        .updatingApplicationId !=
                            null,

                onReasonChange = { newValue ->
                    if (newValue.length <= 500) {
                        rejectReason =
                            newValue
                    }
                },

                onConfirmClick = {
                    onRejectClick(
                        application
                            .producerProfileId,

                        rejectReason
                    )

                    selectedApplicationForReject =
                        null

                    rejectReason = ""
                },

                onDismissClick = {
                    selectedApplicationForReject =
                        null

                    rejectReason = ""
                }
            )
        }
}

@Composable
private fun ApplicationStatusTabs(
    selectedStatus:
    ProducerApplicationStatus,
    isEnabled: Boolean,
    onStatusSelected:
        (ProducerApplicationStatus) -> Unit
) {
    val statuses =
        listOf(
            ProducerApplicationStatus.PENDING,
            ProducerApplicationStatus.APPROVED,
            ProducerApplicationStatus.REJECTED
        )

    val selectedIndex =
        statuses.indexOf(selectedStatus)
            .coerceAtLeast(0)

    TabRow(
        selectedTabIndex =
            selectedIndex
    ) {
        statuses.forEach { status ->
            Tab(
                selected =
                    selectedStatus == status,

                onClick = {
                    onStatusSelected(status)
                },

                enabled =
                    isEnabled,

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
private fun AdminApplicationCard(
    application:
    AdminProducerApplicationResponse,

    selectedStatus:
    ProducerApplicationStatus,

    isUpdating: Boolean,
    isAnyApplicationUpdating: Boolean,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            AdminBusinessImage(
                businessImageUrl =
                    application.businessImageUrl,

                businessName =
                    application.businessName
            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Text(
                text =
                    application.businessName
                        .ifBlank {
                            "İşletme"
                        },

                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    translateApplicationStatus(
                        application
                            .verificationStatus
                    ),

                color =
                    applicationStatusColor(
                        application
                            .verificationStatus
                    ),

                style =
                    MaterialTheme.typography
                        .titleSmall
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            AdminApplicationInformation(
                title = "Başvuru numarası",
                value =
                    application
                        .producerProfileId
                        .toString()
            )

            AdminApplicationInformation(
                title = "Başvuru sahibi",
                value =
                    application.fullName
                        .ifBlank {
                            "-"
                        }
            )

            AdminApplicationInformation(
                title = "E-posta",
                value =
                    application.email
                        .ifBlank {
                            "-"
                        }
            )

            AdminApplicationInformation(
                title = "Kullanıcı rolü",
                value =
                    application.userRole
                        .ifBlank {
                            "-"
                        }
            )

            AdminApplicationInformation(
                title = "Günlük kapasite",
                value =
                    "${application.dailyCapacity} adet"
            )

            AdminApplicationInformation(
                title = "Kalan kapasite",
                value =
                    "${application.remainingCapacity} adet"
            )

            AdminApplicationInformation(
                title = "Sipariş alma durumu",
                value =
                    if (application.isAvailable) {
                        "Açık"
                    } else {
                        "Kapalı"
                    }
            )

            AdminApplicationInformation(
                title = "Başvuru tarihi",
                value =
                    formatAdminApplicationDate(
                        application.createdAt
                    )
            )

            when (selectedStatus) {
                ProducerApplicationStatus.PENDING -> {
                    // Ek durum bilgisi yok.
                }

                ProducerApplicationStatus.APPROVED -> {
                    AdminApplicationInformation(
                        title = "Onay tarihi",
                        value =
                            formatAdminApplicationDate(
                                application.approvedAt
                            )
                    )

                    AdminApplicationInformation(
                        title =
                            "Onaylayan Admin ID",

                        value =
                            application
                                .approvedByAdminId
                                ?.toString()
                                ?: "-"
                    )
                }

                ProducerApplicationStatus.REJECTED -> {
                    AdminApplicationInformation(
                        title = "Red tarihi",
                        value =
                            formatAdminApplicationDate(
                                application.rejectedAt
                            )
                    )

                    AdminApplicationInformation(
                        title =
                            "Reddeden Admin ID",

                        value =
                            application
                                .rejectedByAdminId
                                ?.toString()
                                ?: "-"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "İşletme Açıklaması",
                style =
                    MaterialTheme.typography
                        .titleMedium
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    application.description
                        .ifBlank {
                            "-"
                        },

                style =
                    MaterialTheme.typography
                        .bodyMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "İşletme Adresi",
                style =
                    MaterialTheme.typography
                        .titleMedium
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    application.address
                        .ifBlank {
                            "-"
                        },

                style =
                    MaterialTheme.typography
                        .bodyMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminApplicationInformation(
                title = "Konum",
                value =
                    "${application.latitude}, " +
                            application.longitude
            )

            if (
                selectedStatus ==
                ProducerApplicationStatus.REJECTED
            ) {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                HorizontalDivider()

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Red Nedeni",
                    style =
                        MaterialTheme.typography
                            .titleMedium,

                    color =
                        MaterialTheme.colorScheme
                            .error
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text =
                        application
                            .rejectionReason
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?: "Red nedeni bulunmuyor.",

                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }

            if (
                selectedStatus ==
                ProducerApplicationStatus.PENDING
            ) {
                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                if (isUpdating) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Button(
                        onClick =
                            onApproveClick,

                        enabled =
                            !isAnyApplicationUpdating,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text("Onayla")
                    }

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    OutlinedButton(
                        onClick =
                            onRejectClick,

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
}

@Composable
private fun AdminBusinessImage(
    businessImageUrl: String?,
    businessName: String
) {
    val resolvedImageUrl =
        ApiConfig.resolveMediaUrl(
            businessImageUrl
        )

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(190.dp),

        shape =
            RoundedCornerShape(16.dp),

        tonalElevation = 1.dp
    ) {
        if (resolvedImageUrl == null) {
            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text =
                        "İşletme görseli bulunmuyor",

                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }
        } else {
            AsyncImage(
                model =
                    resolvedImageUrl,

                contentDescription =
                    "${businessName.ifBlank { "İşletme" }} vitrin görseli",

                modifier =
                    Modifier.fillMaxSize(),

                contentScale =
                    ContentScale.Crop
            )
        }
    }
}

@Composable
private fun AdminApplicationInformation(
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

@Composable
private fun RejectApplicationDialog(
    application:
    AdminProducerApplicationResponse,

    rejectReason: String,
    isUpdating: Boolean,
    onReasonChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isUpdating) {
                onDismissClick()
            }
        },

        title = {
            Text("Başvuruyu Reddet")
        },

        text = {
            Column {
                Text(
                    text =
                        "${application.businessName} " +
                                "başvurusunu reddetme nedeninizi yazın."
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = rejectReason,

                    onValueChange =
                        onReasonChange,

                    label = {
                        Text("Red nedeni")
                    },

                    supportingText = {
                        Text(
                            "${rejectReason.length}/500 karakter"
                        )
                    },

                    minLines = 3,

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled = !isUpdating
                )

                if (
                    rejectReason.isNotBlank() &&
                    rejectReason.trim().length < 10
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "Red nedeni en az 10 karakter olmalıdır.",

                        color =
                            MaterialTheme.colorScheme
                                .error,

                        style =
                            MaterialTheme.typography
                                .bodySmall
                    )
                }
            }
        },

        confirmButton = {
            Button(
                onClick =
                    onConfirmClick,

                enabled =
                    rejectReason.trim()
                        .length in 10..500 &&
                            !isUpdating
            ) {
                Text("Başvuruyu Reddet")
            }
        },

        dismissButton = {
            TextButton(
                onClick =
                    onDismissClick,

                enabled =
                    !isUpdating
            ) {
                Text("Vazgeç")
            }
        }
    )
}

private fun applicationScreenDescription(
    status: ProducerApplicationStatus
): String {
    return when (status) {
        ProducerApplicationStatus.PENDING ->
            "Bekleyen başvuruları inceleyebilir, onaylayabilir veya reddedebilirsiniz."

        ProducerApplicationStatus.APPROVED ->
            "Onaylanmış üretici başvurularını ve hesap bilgilerini görüntüleyebilirsiniz."

        ProducerApplicationStatus.REJECTED ->
            "Reddedilmiş başvuruları ve red nedenlerini görüntüleyebilirsiniz."
    }
}

private fun translateApplicationStatus(
    status: String
): String {
    return when (
        ProducerApplicationStatus
            .fromBackendValue(status)
    ) {
        ProducerApplicationStatus.PENDING ->
            "Onay Bekliyor"

        ProducerApplicationStatus.APPROVED ->
            "Onaylandı"

        ProducerApplicationStatus.REJECTED ->
            "Reddedildi"

        null ->
            status.ifBlank {
                "Bilinmeyen Durum"
            }
    }
}

@Composable
private fun applicationStatusColor(
    status: String
) = when (
    ProducerApplicationStatus
        .fromBackendValue(status)
) {
    ProducerApplicationStatus.PENDING ->
        MaterialTheme.colorScheme.tertiary

    ProducerApplicationStatus.APPROVED ->
        MaterialTheme.colorScheme.primary

    ProducerApplicationStatus.REJECTED ->
        MaterialTheme.colorScheme.error

    null ->
        MaterialTheme.colorScheme.onSurface
}

private fun formatAdminApplicationDate(
    value: String?
): String {
    if (value.isNullOrBlank()) {
        return "-"
    }

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