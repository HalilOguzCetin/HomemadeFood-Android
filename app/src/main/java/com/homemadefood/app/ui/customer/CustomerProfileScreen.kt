package com.homemadefood.app.ui.customer

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp)
    ) {
        TextButton(
            onClick = onBackClick
        ) {
            Text("← Hesabıma Dön")
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text = "Profil Bilgilerim",

            style =
                MaterialTheme
                    .typography
                    .headlineMedium,

            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                "Hesap bilgilerinizi görüntüleyin ve düzenleyin.",

            modifier =
                Modifier.padding(
                    top = 6.dp
                ),

            style =
                MaterialTheme
                    .typography
                    .bodyMedium,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier =
                        Modifier.align(
                            Alignment
                                .CenterHorizontally
                        )
                )
            }

            uiState.profile == null -> {
                Text(
                    text =
                        uiState.errorMessage
                            ?: "Profil bilgileri görüntülenemedi.",

                    color =
                        MaterialTheme
                            .colorScheme
                            .error
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Button(
                    onClick =
                        onRetryClick,

                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text("Tekrar Dene")
                }
            }

            else -> {
                val profile =
                    uiState.profile

                if (
                    uiState.successMessage !=
                    null
                ) {
                    ProfileMessage(
                        message =
                            uiState
                                .successMessage,

                        isError = false
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )
                }

                if (
                    uiState.errorMessage !=
                    null
                ) {
                    ProfileMessage(
                        message =
                            uiState
                                .errorMessage,

                        isError = true
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )
                }

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),

                    tonalElevation = 1.dp,

                    shape =
                        MaterialTheme.shapes
                            .large
                ) {
                    Column(
                        modifier =
                            Modifier.padding(
                                18.dp
                            )
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement
                                    .SpaceBetween,

                            verticalAlignment =
                                Alignment
                                    .CenterVertically
                        ) {
                            Text(
                                text =
                                    "Temel Bilgiler",

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge,

                                fontWeight =
                                    FontWeight
                                        .SemiBold
                            )

                            if (
                                !uiState.isEditing
                            ) {
                                TextButton(
                                    onClick =
                                        onStartEditingClick
                                ) {
                                    Text(
                                        "Düzenle"
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(
                                    14.dp
                                )
                        )

                        if (
                            uiState.isEditing
                        ) {
                            OutlinedTextField(
                                value =
                                    uiState.fullName,

                                onValueChange =
                                    onFullNameChange,

                                modifier =
                                    Modifier
                                        .fillMaxWidth(),

                                label = {
                                    Text(
                                        "Ad Soyad"
                                    )
                                },

                                singleLine = true,

                                enabled =
                                    !uiState
                                        .isSaving
                            )
                        } else {
                            ProfileValue(
                                label =
                                    "Ad Soyad",

                                value =
                                    profile.fullName
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(
                                    18.dp
                                )
                        )

                        HorizontalDivider()

                        Spacer(
                            modifier =
                                Modifier.height(
                                    18.dp
                                )
                        )

                        ProfileValue(
                            label =
                                "E-posta",

                            value =
                                profile.email
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    6.dp
                                )
                        )

                        Text(
                            text =
                                if (
                                    profile
                                        .isEmailVerified
                                ) {
                                    "E-posta doğrulandı"
                                } else {
                                    "E-posta doğrulanmadı"
                                },

                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium,

                            color =
                                if (
                                    profile
                                        .isEmailVerified
                                ) {
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                                } else {
                                    MaterialTheme
                                        .colorScheme
                                        .error
                                }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    18.dp
                                )
                        )

                        HorizontalDivider()

                        Spacer(
                            modifier =
                                Modifier.height(
                                    18.dp
                                )
                        )

                        ProfileValue(
                            label =
                                "Telefon",

                            value =
                                profile.phone
                                    .takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "Kayıtlı telefon numarası yok"
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    6.dp
                                )
                        )

                        Text(
                            text =
                                if (
                                    profile.isPhoneVerified
                                ) {
                                    "Telefon doğrulandı"
                                } else {
                                    "Telefon doğrulanmadı"
                                },

                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium,

                            color =
                                if (
                                    profile.isPhoneVerified
                                ) {
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                                } else {
                                    MaterialTheme
                                        .colorScheme
                                        .error
                                }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        TextButton(
                            onClick =
                                onPhoneVerificationClick,

                            enabled =
                                !uiState.isSaving
                        ) {
                            Text(
                                text =
                                    when {
                                        profile
                                            .isPhoneVerified ->
                                            "Telefonu Değiştir"

                                        profile.phone
                                            .isNotBlank() ->
                                            "Telefonu Doğrula"

                                        else ->
                                            "Telefon Ekle ve Doğrula"
                                    }
                            )
                        }
                    }
                }

                if (
                    uiState.isEditing
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(
                                16.dp
                            )
                    )

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {
                        TextButton(
                            onClick =
                                onCancelEditingClick,

                            modifier =
                                Modifier.weight(
                                    1f
                                ),

                            enabled =
                                !uiState.isSaving
                        ) {
                            Text("Vazgeç")
                        }

                        Button(
                            onClick =
                                onSaveClick,

                            modifier =
                                Modifier.weight(
                                    1f
                                ),

                            enabled =
                                uiState.canSave
                        ) {
                            if (
                                uiState.isSaving
                            ) {
                                CircularProgressIndicator()
                            } else {
                                Text(
                                    "Kaydet"
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )
    }
}

@Composable
private fun ProfileValue(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,

            style =
                MaterialTheme
                    .typography
                    .labelMedium,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        Text(
            text = value,

            modifier =
                Modifier.padding(
                    top = 4.dp
                ),

            style =
                MaterialTheme
                    .typography
                    .bodyLarge
        )
    }
}

@Composable
private fun ProfileMessage(
    message: String,
    isError: Boolean
) {
    Text(
        text = message,

        modifier =
            Modifier.fillMaxWidth(),

        color =
            if (isError) {
                MaterialTheme
                    .colorScheme
                    .error
            } else {
                MaterialTheme
                    .colorScheme
                    .primary
            },

        style =
            MaterialTheme
                .typography
                .bodyMedium
    )
}