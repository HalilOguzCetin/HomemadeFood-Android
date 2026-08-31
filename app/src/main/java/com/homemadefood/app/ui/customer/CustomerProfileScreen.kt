package com.homemadefood.app.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.AppMessageType

private object CustomerProfileDp {
    val Horizontal = 20.dp
    val Top = 16.dp
    val Bottom = 28.dp
    val SectionGap = 18.dp
    val CardRadius = 22.dp
    val CardPadding = 18.dp
    val AvatarSize = 72.dp
}

@Composable
fun CustomerProfileScreen(
    uiState: CustomerProfileUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onStartEditingClick: () -> Unit,
    onCancelEditingClick: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onPhoneVerificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(CustomerHomeColors.Cream)
                ) {
                    AppLoadingState(
                        message = "Profil bilgileriniz yükleniyor..."
                    )

                    TextButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                horizontal = CustomerProfileDp.Horizontal,
                                vertical = 8.dp
                            )
                    ) {
                        Text(
                            text = "←  Geri",
                            color = CustomerHomeColors.DeepOlive,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            uiState.profile == null -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(CustomerHomeColors.Cream)
                ) {
                    AppErrorState(
                        message = uiState.errorMessage
                            ?: "Profil bilgileri görüntülenemedi.",
                        onRetryClick = onRetryClick
                    )

                    TextButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                horizontal = CustomerProfileDp.Horizontal,
                                vertical = 8.dp
                            )
                    ) {
                        Text(
                            text = "←  Geri",
                            color = CustomerHomeColors.DeepOlive,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            else -> {
                val profile = uiState.profile

                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(CustomerHomeColors.Cream)
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = CustomerProfileDp.Horizontal,
                            end = CustomerProfileDp.Horizontal,
                            top = CustomerProfileDp.Top,
                            bottom = CustomerProfileDp.Bottom
                        )
                ) {
                    TextButton(
                        onClick = onBackClick,
                        enabled = !uiState.isSaving,
                        contentPadding =
                            androidx.compose.foundation.layout.PaddingValues(
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

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Profilim",
                        style = MaterialTheme.typography.headlineMedium,
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Kişisel bilgilerinizi görüntüleyin ve yönetin",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomerHomeColors.TextMuted
                    )

                    Spacer(
                        modifier = Modifier.height(CustomerProfileDp.SectionGap)
                    )

                    ProfileHeaderCard(
                        fullName = profile.fullName,
                        email = profile.email
                    )

                    Spacer(
                        modifier = Modifier.height(CustomerProfileDp.SectionGap)
                    )

                    uiState.successMessage?.let { message ->
                        AppInlineMessage(
                            message = message,
                            type = AppMessageType.Success
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    uiState.errorMessage?.let { message ->
                        AppInlineMessage(
                            message = message,
                            type = AppMessageType.Error
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    ProfileInformationCard(
                        uiState = uiState,
                        email = profile.email,
                        isEmailVerified = profile.isEmailVerified,
                        phone = profile.phone,
                        isPhoneVerified = profile.isPhoneVerified,
                        onFullNameChange = onFullNameChange,
                        onPhoneVerificationClick = onPhoneVerificationClick
                    )

                    Spacer(
                        modifier = Modifier.height(CustomerProfileDp.SectionGap)
                    )

                    if (uiState.isEditing) {
                        EditingActions(
                            isSaving = uiState.isSaving,
                            canSave = uiState.canSave,
                            onCancelClick = onCancelEditingClick,
                            onSaveClick = onSaveClick
                        )
                    } else {
                        Button(
                            onClick = onStartEditingClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(17.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CustomerHomeColors.DeepOlive,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Profili Düzenle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    fullName: String,
    email: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CustomerProfileDp.CardRadius),
        color = CustomerHomeColors.Surface,
        shadowElevation = 3.dp,
        border = BorderStroke(
            width = 1.dp,
            color = CustomerHomeColors.Outline.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CustomerProfileDp.CardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(CustomerProfileDp.AvatarSize)
                    .background(
                        color = CustomerHomeColors.OliveSoft,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fullName
                        .trim()
                        .firstOrNull()
                        ?.uppercase()
                        ?: "M",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = fullName.ifBlank { "Müşteri Hesabı" },
                    style = MaterialTheme.typography.titleLarge,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CustomerHomeColors.TextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = CustomerHomeColors.TerracottaSoft
                ) {
                    Text(
                        text = "Müşteri Hesabı",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = CustomerHomeColors.Terracotta,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInformationCard(
    uiState: CustomerProfileUiState,
    email: String,
    isEmailVerified: Boolean,
    phone: String,
    isPhoneVerified: Boolean,
    onFullNameChange: (String) -> Unit,
    onPhoneVerificationClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CustomerProfileDp.CardRadius),
        color = CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border = BorderStroke(
            width = 1.dp,
            color = CustomerHomeColors.Outline.copy(alpha = 0.72f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CustomerProfileDp.CardPadding)
        ) {
            Text(
                text = "Kişisel Bilgiler",
                style = MaterialTheme.typography.titleLarge,
                color = CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (uiState.isEditing) {
                Text(
                    text = "Ad Soyad",
                    style = MaterialTheme.typography.labelLarge,
                    color = CustomerHomeColors.TextMuted,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(7.dp))

                OutlinedTextField(
                    value = uiState.fullName,
                    onValueChange = onFullNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isSaving,
                    placeholder = {
                        Text("Ad Soyad")
                    },
                    shape = RoundedCornerShape(15.dp)
                )
            } else {
                ProfileField(
                    label = "Ad Soyad",
                    value = uiState.profile?.fullName.orEmpty()
                )
            }

            ProfileDivider()

            ProfileFieldWithStatus(
                label = "E-posta",
                value = email,
                isVerified = isEmailVerified,
                verifiedText = "Doğrulandı",
                unverifiedText = "Doğrulanmadı"
            )

            Text(
                text = "E-posta adresi bu ekrandan değiştirilemez.",
                modifier = Modifier.padding(top = 7.dp),
                style = MaterialTheme.typography.bodySmall,
                color = CustomerHomeColors.TextMuted
            )

            ProfileDivider()

            ProfileFieldWithStatus(
                label = "Telefon",
                value = phone.takeIf { it.isNotBlank() }
                    ?: "Kayıtlı telefon numarası yok",
                isVerified = isPhoneVerified,
                verifiedText = "Doğrulandı",
                unverifiedText = "Doğrulanmadı"
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onPhoneVerificationClick,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(
                    1.dp,
                    CustomerHomeColors.DeepOlive.copy(alpha = 0.35f)
                )
            ) {
                Text(
                    text = when {
                        isPhoneVerified -> "Telefon Numarasını Değiştir"
                        phone.isNotBlank() -> "Telefonu Doğrula"
                        else -> "Telefon Ekle ve Doğrula"
                    },
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = CustomerHomeColors.TextMuted,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = CustomerHomeColors.Text,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ProfileFieldWithStatus(
    label: String,
    value: String,
    isVerified: Boolean,
    verifiedText: String,
    unverifiedText: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = CustomerHomeColors.TextMuted,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(7.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = CustomerHomeColors.Text,
                fontWeight = FontWeight.Medium
            )

            VerificationBadge(
                text = if (isVerified) {
                    verifiedText
                } else {
                    unverifiedText
                },
                isVerified = isVerified
            )
        }
    }
}

@Composable
private fun VerificationBadge(
    text: String,
    isVerified: Boolean
) {
    val containerColor =
        if (isVerified) {
            CustomerHomeColors.OliveSoft
        } else {
            CustomerHomeColors.TerracottaSoft
        }

    val contentColor =
        if (isVerified) {
            CustomerHomeColors.DeepOlive
        } else {
            CustomerHomeColors.Terracotta
        }

    Surface(
        shape = RoundedCornerShape(50),
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProfileDivider() {
    Spacer(modifier = Modifier.height(18.dp))

    HorizontalDivider(
        color = CustomerHomeColors.Outline.copy(alpha = 0.75f)
    )

    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
private fun EditingActions(
    isSaving: Boolean,
    canSave: Boolean,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancelClick,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            enabled = !isSaving,
            shape = RoundedCornerShape(17.dp),
            border = BorderStroke(
                width = 1.dp,
                color = CustomerHomeColors.Outline
            )
        ) {
            Text(
                text = "Vazgeç",
                color = CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            enabled = canSave,
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomerHomeColors.Terracotta,
                contentColor = Color.White,
                disabledContainerColor =
                    CustomerHomeColors.Terracotta.copy(alpha = 0.38f),
                disabledContentColor = Color.White.copy(alpha = 0.85f)
            )
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text = "Kaydet",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
