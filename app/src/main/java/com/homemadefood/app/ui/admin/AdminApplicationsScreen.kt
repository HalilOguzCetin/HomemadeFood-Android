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
    onStatusChangeClick:
        (Int, ProducerApplicationStatus, String?) -> Unit,
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

    var selectedApplicationForStatusChange by
    remember {
        mutableStateOf<
                AdminProducerApplicationResponse?
                >(null)
    }

    var targetStatusForChange by remember {
        mutableStateOf<ProducerApplicationStatus?>(null)
    }

    var statusReviewNote by remember {
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

                            onStatusChangeClick = { targetStatus ->
                                statusReviewNote = ""
                                targetStatusForChange = targetStatus
                                selectedApplicationForStatusChange = application
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

    val statusTarget = targetStatusForChange

    if (
        selectedApplicationForStatusChange != null &&
        statusTarget != null
    ) {
        StatusChangeDialog(
            application =
                selectedApplicationForStatusChange!!,
            targetStatus = statusTarget,
            reviewNote = statusReviewNote,
            isUpdating =
                uiState.updatingApplicationId != null,
            onReviewNoteChange = { value ->
                if (value.length <= 1000) {
                    statusReviewNote = value
                }
            },
            onConfirmClick = {
                onStatusChangeClick(
                    selectedApplicationForStatusChange!!
                        .producerProfileId,
                    statusTarget,
                    statusReviewNote
                        .trim()
                        .takeIf { it.isNotBlank() }
                )

                selectedApplicationForStatusChange = null
                targetStatusForChange = null
                statusReviewNote = ""
            },
            onDismissClick = {
                selectedApplicationForStatusChange = null
                targetStatusForChange = null
                statusReviewNote = ""
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
            ProducerApplicationStatus.UNDER_REVIEW,
            ProducerApplicationStatus.ADDITIONAL_DOCUMENT_REQUIRED,
            ProducerApplicationStatus.APPROVED,
            ProducerApplicationStatus.SUSPENDED,
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
    onStatusChangeClick:
        (ProducerApplicationStatus) -> Unit,
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
                ProducerApplicationStatus.PENDING,
                ProducerApplicationStatus.UNDER_REVIEW,
                ProducerApplicationStatus.ADDITIONAL_DOCUMENT_REQUIRED,
                ProducerApplicationStatus.SUSPENDED -> {
                    // Bu durumlar için tarih bazlı ek alan henüz yok.
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

            application.compliance?.let { compliance ->
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                HorizontalDivider()

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Uyumluluk / Doğrulama",
                    style =
                        MaterialTheme.typography
                            .titleMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                AdminApplicationInformation(
                    title = "Vergi durumu",
                    value = translateTaxStatus(
                        compliance.taxStatus
                    )
                )

                AdminApplicationInformation(
                    title = "Vergi numarası",
                    value =
                        compliance.taxNumber
                            ?.takeIf { it.isNotBlank() }
                            ?: "-"
                )

                AdminApplicationInformation(
                    title = "Esnaf muafiyet belgesi",
                    value =
                        compliance
                            .taxExemptionCertificateNumber
                            ?.takeIf { it.isNotBlank() }
                            ?: "-"
                )

                AdminApplicationInformation(
                    title = "Gıda işletmesi kayıt no",
                    value =
                        compliance
                            .foodBusinessRegistrationNumber
                            ?.takeIf { it.isNotBlank() }
                            ?: "-"
                )

                AdminApplicationInformation(
                    title = "Gıda kayıt durumu",
                    value = translateFoodRegistrationStatus(
                        compliance.foodRegistrationStatus
                    )
                )

                AdminApplicationInformation(
                    title = "Ödeme hesabı",
                    value = translatePaymentAccountStatus(
                        compliance.paymentAccountStatus
                    )
                )

                AdminApplicationInformation(
                    title = "Uyumluluk durumu",
                    value = translateComplianceStatus(
                        compliance.complianceStatus
                    )
                )

                AdminApplicationInformation(
                    title = "Resmî doğrulama",
                    value =
                        if (compliance.isOfficiallyVerified) {
                            "Doğrulandı"
                        } else {
                            "Henüz doğrulanmadı"
                        }
                )

                compliance.reviewNote
                    ?.takeIf { it.isNotBlank() }
                    ?.let { note ->
                        AdminApplicationInformation(
                            title = "Uyumluluk inceleme notu",
                            value = note
                        )
                    }
            }

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
                selectedStatus !=
                ProducerApplicationStatus.REJECTED
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
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    when (selectedStatus) {
                        ProducerApplicationStatus.PENDING -> {
                            AdminStatusActionButton(
                                text = "İncelemeye Al",
                                enabled = !isAnyApplicationUpdating,
                                onClick = {
                                    onStatusChangeClick(
                                        ProducerApplicationStatus.UNDER_REVIEW
                                    )
                                }
                            )

                            AdminStatusActionButton(
                                text = "Ek Bilgi / Belge İste",
                                enabled = !isAnyApplicationUpdating,
                                outlined = true,
                                onClick = {
                                    onStatusChangeClick(
                                        ProducerApplicationStatus.ADDITIONAL_DOCUMENT_REQUIRED
                                    )
                                }
                            )

                            AdminStatusActionButton(
                                text = "Onayla",
                                enabled = !isAnyApplicationUpdating,
                                onClick = onApproveClick
                            )

                            AdminStatusActionButton(
                                text = "Reddet",
                                enabled = !isAnyApplicationUpdating,
                                outlined = true,
                                onClick = onRejectClick
                            )
                        }

                        ProducerApplicationStatus.UNDER_REVIEW -> {
                            AdminStatusActionButton(
                                text = "Ek Bilgi / Belge İste",
                                enabled = !isAnyApplicationUpdating,
                                outlined = true,
                                onClick = {
                                    onStatusChangeClick(
                                        ProducerApplicationStatus.ADDITIONAL_DOCUMENT_REQUIRED
                                    )
                                }
                            )

                            AdminStatusActionButton(
                                text = "Onayla",
                                enabled = !isAnyApplicationUpdating,
                                onClick = {
                                    onStatusChangeClick(
                                        ProducerApplicationStatus.APPROVED
                                    )
                                }
                            )

                            AdminStatusActionButton(
                                text = "Reddet",
                                enabled = !isAnyApplicationUpdating,
                                outlined = true,
                                onClick = onRejectClick
                            )
                        }

                        ProducerApplicationStatus.ADDITIONAL_DOCUMENT_REQUIRED -> {
                            AdminStatusActionButton(
                                text = "Yeniden İncelemeye Al",
                                enabled = !isAnyApplicationUpdating,
                                onClick = {
                                    onStatusChangeClick(
                                        ProducerApplicationStatus.UNDER_REVIEW
                                    )
                                }
                            )

                            AdminStatusActionButton(
                                text = "Onayla",
                                enabled = !isAnyApplicationUpdating,
                                onClick = {
                                    onStatusChangeClick(
                                        ProducerApplicationStatus.APPROVED
                                    )
                                }
                            )

                            AdminStatusActionButton(
                                text = "Reddet",
                                enabled = !isAnyApplicationUpdating,
                                outlined = true,
                                onClick = onRejectClick
                            )
                        }

                        ProducerApplicationStatus.APPROVED -> {
                            AdminStatusActionButton(
                                text = "Üreticiyi Askıya Al",
                                enabled = !isAnyApplicationUpdating,
                                outlined = true,
                                onClick = {
                                    onStatusChangeClick(
                                        ProducerApplicationStatus.SUSPENDED
                                    )
                                }
                            )
                        }

                        ProducerApplicationStatus.SUSPENDED -> {
                            AdminStatusActionButton(
                                text = "İncelemeye Al",
                                enabled = !isAnyApplicationUpdating,
                                onClick = {
                                    onStatusChangeClick(
                                        ProducerApplicationStatus.UNDER_REVIEW
                                    )
                                }
                            )

                            AdminStatusActionButton(
                                text = "Tekrar Onayla",
                                enabled = !isAnyApplicationUpdating,
                                onClick = {
                                    onStatusChangeClick(
                                        ProducerApplicationStatus.APPROVED
                                    )
                                }
                            )

                            AdminStatusActionButton(
                                text = "Reddet",
                                enabled = !isAnyApplicationUpdating,
                                outlined = true,
                                onClick = onRejectClick
                            )
                        }

                        ProducerApplicationStatus.REJECTED -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatusActionButton(
    text: String,
    enabled: Boolean,
    outlined: Boolean = false,
    onClick: () -> Unit
) {
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text)
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text)
        }
    }

    Spacer(
        modifier = Modifier.height(8.dp)
    )
}

@Composable
private fun StatusChangeDialog(
    application: AdminProducerApplicationResponse,
    targetStatus: ProducerApplicationStatus,
    reviewNote: String,
    isUpdating: Boolean,
    onReviewNoteChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    val noteRequired =
        targetStatus == ProducerApplicationStatus.ADDITIONAL_DOCUMENT_REQUIRED ||
                targetStatus == ProducerApplicationStatus.SUSPENDED ||
                targetStatus == ProducerApplicationStatus.REJECTED

    AlertDialog(
        onDismissRequest = {
            if (!isUpdating) {
                onDismissClick()
            }
        },
        title = {
            Text(
                text = ProducerApplicationStatus.detailDisplayNameFor(targetStatus.backendValue)
            )
        },
        text = {
            Column {
                Text(
                    text =
                        "${application.businessName.ifBlank { "İşletme" }} başvurusu için yeni durum: ${ProducerApplicationStatus.detailDisplayNameFor(targetStatus.backendValue)}"
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = reviewNote,
                    onValueChange = onReviewNoteChange,
                    label = {
                        Text(
                            if (noteRequired) {
                                "İnceleme notu (zorunlu)"
                            } else {
                                "İnceleme notu (isteğe bağlı)"
                            }
                        )
                    },
                    supportingText = {
                        Text(
                            if (noteRequired) {
                                "En az 10, en fazla 1000 karakter."
                            } else {
                                "En fazla 1000 karakter."
                            }
                        )
                    },
                    enabled = !isUpdating,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick,
                enabled =
                    !isUpdating &&
                            (!noteRequired || reviewNote.trim().length >= 10)
            ) {
                Text("Durumu Güncelle")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissClick,
                enabled = !isUpdating
            ) {
                Text("Vazgeç")
            }
        }
    )
}

private fun translateTaxStatus(value: String): String =
    when (value.trim()) {
        "Taxpayer" -> "Vergi mükellefi"
        "TradesmanExemption" -> "Esnaf vergi muafiyeti"
        "PendingSetup" -> "İşlemler hazırlanıyor"
        else -> "Beyan edilmedi"
    }

private fun translateFoodRegistrationStatus(value: String): String =
    when (value.trim()) {
        "ManualReview" -> "Manuel incelemede"
        "Verified" -> "Doğrulandı"
        "Rejected" -> "Reddedildi"
        "Expired" -> "Süresi doldu"
        else -> "Beyan edilmedi"
    }

private fun translatePaymentAccountStatus(value: String): String =
    when (value.trim()) {
        "ManualReview" -> "Manuel incelemede"
        "Verified" -> "Doğrulandı"
        "Rejected" -> "Reddedildi"
        else -> "Henüz yapılandırılmadı"
    }

private fun translateComplianceStatus(value: String): String =
    when (value.trim()) {
        "ManualReview" -> "Manuel incelemede"
        "Compliant" -> "Uygun"
        "AdditionalActionRequired" -> "Ek işlem gerekli"
        "Suspended" -> "Askıya alındı"
        else -> "Altyapı hazır / resmî doğrulama bağlı değil"
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
            "Bekleyen üretici başvurularını görüntüleyebilirsiniz."

        ProducerApplicationStatus.UNDER_REVIEW ->
            "Manuel incelemeye alınmış üretici başvurularını görüntüleyebilirsiniz."

        ProducerApplicationStatus.ADDITIONAL_DOCUMENT_REQUIRED ->
            "Ek bilgi veya belge beklenen üretici başvurularını görüntüleyebilirsiniz."

        ProducerApplicationStatus.APPROVED ->
            "Onaylanmış üretici başvurularını ve hesap bilgilerini görüntüleyebilirsiniz."

        ProducerApplicationStatus.SUSPENDED ->
            "Geçici olarak askıya alınmış üretici başvurularını görüntüleyebilirsiniz."

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

        ProducerApplicationStatus.UNDER_REVIEW ->
            "İnceleniyor"

        ProducerApplicationStatus.ADDITIONAL_DOCUMENT_REQUIRED ->
            "Ek Bilgi / Belge Gerekli"

        ProducerApplicationStatus.APPROVED ->
            "Onaylandı"

        ProducerApplicationStatus.SUSPENDED ->
            "Askıya Alındı"

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
    ProducerApplicationStatus.PENDING,
    ProducerApplicationStatus.UNDER_REVIEW ->
        MaterialTheme.colorScheme.tertiary

    ProducerApplicationStatus.ADDITIONAL_DOCUMENT_REQUIRED,
    ProducerApplicationStatus.SUSPENDED,
    ProducerApplicationStatus.REJECTED ->
        MaterialTheme.colorScheme.error

    ProducerApplicationStatus.APPROVED ->
        MaterialTheme.colorScheme.primary

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