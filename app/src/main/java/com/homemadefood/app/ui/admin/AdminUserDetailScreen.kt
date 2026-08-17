package com.homemadefood.app.ui.admin

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.homemadefood.app.data.model.AdminUserDetailResponse
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.AppMessageType
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminUserDetailScreen(
    uiState: AdminUserDetailUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onUpdateStatusClick: (Boolean) -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var requestedStatus by remember {
        mutableStateOf<Boolean?>(null)
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
            enabled = !uiState.isUpdatingStatus
        ) {
            Text("← Kullanıcı Listesine Dön")
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Kullanıcı Detayı",
            style =
                MaterialTheme.typography
                    .headlineMedium
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
            uiState.user != null
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
                    message = "Kullanıcı bilgileri yükleniyor..."
                )
            }

            uiState.user == null -> {
                AppErrorState(
                    message =
                        uiState.errorMessage
                            ?: "Kullanıcı bilgileri alınamadı.",

                    onRetryClick =
                        onRetryClick
                )
            }

            else -> {
                AdminUserDetailContent(
                    user = uiState.user,
                    isUpdatingStatus =
                        uiState.isUpdatingStatus,

                    onUpdateStatusClick = {
                            isActive ->

                        requestedStatus =
                            isActive
                    }
                )
            }
        }
    }

    requestedStatus?.let { newStatus ->
        val user =
            uiState.user

        if (user != null) {
            UserStatusConfirmationDialog(
                user = user,
                newStatus = newStatus,
                isUpdating =
                    uiState.isUpdatingStatus,

                onConfirmClick = {
                    onUpdateStatusClick(
                        newStatus
                    )

                    requestedStatus = null
                },

                onDismissClick = {
                    requestedStatus = null
                }
            )
        }
    }
}

@Composable
private fun AdminUserDetailContent(
    user: AdminUserDetailResponse,
    isUpdatingStatus: Boolean,
    onUpdateStatusClick: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            ),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {
        UserIdentityCard(
            user = user
        )

        UserActivitySummaryCard(
            user = user
        )

        if (user.producerProfileId != null) {
            ProducerDetailCard(
                user = user
            )
        }

        UserStatusManagementCard(
            user = user,
            isUpdatingStatus =
                isUpdatingStatus,

            onUpdateStatusClick =
                onUpdateStatusClick
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

@Composable
private fun UserIdentityCard(
    user: AdminUserDetailResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text =
                    user.fullName.ifBlank {
                        "İsimsiz Kullanıcı"
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
                    if (user.isActive) {
                        "Aktif Hesap"
                    } else {
                        "Pasif Hesap"
                    },

                color =
                    if (user.isActive) {
                        MaterialTheme
                            .colorScheme.primary
                    } else {
                        MaterialTheme
                            .colorScheme.error
                    },

                style =
                    MaterialTheme.typography
                        .titleSmall
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            AdminUserDetailInformation(
                title = "Kullanıcı ID",
                value = user.userId.toString()
            )

            AdminUserDetailInformation(
                title = "Rol",
                value = user.role
            )

            AdminUserDetailInformation(
                title = "E-posta",
                value =
                    user.email.ifBlank {
                        "-"
                    }
            )

            AdminUserDetailInformation(
                title = "Telefon",
                value =
                    user.phone.ifBlank {
                        "-"
                    }
            )

            AdminUserDetailInformation(
                title = "Kayıt Tarihi",
                value =
                    formatAdminUserDetailDate(
                        user.createdAt
                    )
            )
        }
    }
}

@Composable
private fun UserActivitySummaryCard(
    user: AdminUserDetailResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Hesap Özeti",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                UserCountBox(
                    title = "Adres",
                    count = user.addressCount,
                    modifier = Modifier.weight(1f)
                )

                UserCountBox(
                    title = "Sipariş",
                    count = user.orderCount,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                UserCountBox(
                    title = "Yorum",
                    count = user.reviewCount,
                    modifier = Modifier.weight(1f)
                )

                UserCountBox(
                    title = "Favori",
                    count = user.favoriteCount,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun UserCountBox(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style =
                    MaterialTheme.typography
                        .headlineSmall
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = title,
                style =
                    MaterialTheme.typography
                        .bodySmall
            )
        }
    }
}

@Composable
private fun ProducerDetailCard(
    user: AdminUserDetailResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Üretici Bilgileri",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminUserDetailInformation(
                title = "Üretici Profil ID",
                value =
                    user.producerProfileId
                        ?.toString()
                        ?: "-"
            )

            AdminUserDetailInformation(
                title = "İşletme",
                value =
                    user.businessName
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "-"
            )

            AdminUserDetailInformation(
                title = "Başvuru Durumu",
                value =
                    translateAdminProducerStatus(
                        user.producerVerificationStatus
                    )
            )

            AdminUserDetailInformation(
                title = "Üretici Onayı",
                value =
                    when (user.isProducerApproved) {
                        true -> "Onaylı"
                        false -> "Onaylı Değil"
                        null -> "-"
                    }
            )

            AdminUserDetailInformation(
                title = "Sipariş Alma Durumu",
                value =
                    when (user.isProducerAvailable) {
                        true -> "Açık"
                        false -> "Kapalı"
                        null -> "-"
                    }
            )

            AdminUserDetailInformation(
                title = "Günlük Kapasite",
                value =
                    user.dailyCapacity
                        ?.let {
                            "$it adet"
                        }
                        ?: "-"
            )

            AdminUserDetailInformation(
                title = "Kalan Kapasite",
                value =
                    user.remainingCapacity
                        ?.let {
                            "$it adet"
                        }
                        ?: "-"
            )
        }
    }
}

@Composable
private fun UserStatusManagementCard(
    user: AdminUserDetailResponse,
    isUpdatingStatus: Boolean,
    onUpdateStatusClick: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Hesap Yönetimi",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    if (user.isActive) {
                        "Bu kullanıcı şu anda sisteme giriş yapabilir."
                    } else {
                        "Bu kullanıcı şu anda sisteme giriş yapamaz."
                    },

                style =
                    MaterialTheme.typography
                        .bodyMedium
            )

            if (
                user.role.equals(
                    "Producer",
                    ignoreCase = true
                ) &&
                user.isActive
            ) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Hesap pasifleştirildiğinde üreticinin sipariş alma durumu da kapatılır.",

                    style =
                        MaterialTheme.typography
                            .bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (isUpdatingStatus) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),

                    contentAlignment =
                        Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (user.isActive) {
                OutlinedButton(
                    onClick = {
                        onUpdateStatusClick(false)
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text("Hesabı Pasifleştir")
                }
            } else {
                Button(
                    onClick = {
                        onUpdateStatusClick(true)
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text("Hesabı Aktifleştir")
                }
            }
        }
    }
}

@Composable
private fun AdminUserDetailInformation(
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
private fun UserStatusConfirmationDialog(
    user: AdminUserDetailResponse,
    newStatus: Boolean,
    isUpdating: Boolean,
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
            Text(
                text =
                    if (newStatus) {
                        "Hesabı Aktifleştir"
                    } else {
                        "Hesabı Pasifleştir"
                    }
            )
        },

        text = {
            Text(
                text =
                    if (newStatus) {
                        "${user.fullName} adlı kullanıcının hesabı yeniden aktifleştirilecek. Kullanıcı tekrar giriş yapabilecek."
                    } else {
                        "${user.fullName} adlı kullanıcının hesabı pasifleştirilecek. Kullanıcının mevcut oturumu geçersiz olacak ve yeniden giriş yapamayacak."
                    }
            )
        },

        confirmButton = {
            Button(
                onClick = onConfirmClick,
                enabled = !isUpdating
            ) {
                Text(
                    text =
                        if (newStatus) {
                            "Aktifleştir"
                        } else {
                            "Pasifleştir"
                        }
                )
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

private fun translateAdminProducerStatus(
    status: String?
): String {
    return when {
        status.isNullOrBlank() ->
            "-"

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

private fun formatAdminUserDetailDate(
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