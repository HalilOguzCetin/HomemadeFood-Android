package com.homemadefood.app.ui.customer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CustomerPhoneVerificationScreen(
    uiState: CustomerPhoneVerificationUiState,
    onBackClick: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onRequestCodeClick: () -> Unit,
    onVerifyClick: () -> Unit,
    onEditPhoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager =
        LocalFocusManager.current

    var resendSeconds by rememberSaveable {
        mutableIntStateOf(0)
    }

    /*
     * İlk başarılı request ve her başarılı resend
     * sonrası 60 saniyelik UI cooldown başlar.
     */
    LaunchedEffect(
        uiState.codeRequestVersion
    ) {
        if (
            uiState.codeRequestVersion > 0
        ) {
            resendSeconds = 60
        }
    }

    LaunchedEffect(
        resendSeconds
    ) {
        if (resendSeconds > 0) {
            delay(1_000)
            resendSeconds--
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .imePadding()
                .padding(20.dp)
    ) {
        TextButton(
            onClick =
                onBackClick,

            enabled =
                !uiState.isBusy
        ) {
            Text(
                "← Geri Dön"
            )
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "Telefon Doğrulama",

            style =
                MaterialTheme
                    .typography
                    .headlineMedium,

            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                if (
                    uiState.isCodeSent
                ) {
                    "Telefonunuza gönderilen 6 haneli kodu girin."
                } else {
                    "Türkiye mobil telefon numaranızı girin. Numara yalnız doğru OTP kodundan sonra hesabınıza kaydedilir."
                },

            modifier =
                Modifier.padding(
                    top = 8.dp
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

        OutlinedTextField(
            value =
                uiState.phone,

            onValueChange =
                onPhoneChange,

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text(
                    "Telefon Numarası"
                )
            },

            placeholder = {
                Text(
                    "0555 123 45 67"
                )
            },

            singleLine = true,

            enabled =
                !uiState.isBusy &&
                        !uiState.isCodeSent,

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Phone,

                    imeAction =
                        if (
                            uiState.isCodeSent
                        ) {
                            ImeAction.Next
                        } else {
                            ImeAction.Done
                        }
                ),

            keyboardActions =
                KeyboardActions(
                    onDone = {
                        if (
                            uiState.canRequestCode
                        ) {
                            focusManager
                                .clearFocus()

                            onRequestCodeClick()
                        }
                    }
                )
        )

        if (uiState.isCodeSent) {
            TextButton(
                onClick =
                    onEditPhoneClick,

                enabled =
                    !uiState.isBusy
            ) {
                Text(
                    "Numarayı Değiştir"
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            OutlinedTextField(
                value =
                    uiState.code,

                onValueChange =
                    onCodeChange,

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text(
                        "6 Haneli Kod"
                    )
                },

                placeholder = {
                    Text("123456")
                },

                singleLine = true,

                enabled =
                    !uiState.isBusy,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType
                                .NumberPassword,

                        imeAction =
                            ImeAction.Done
                    ),

                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            if (
                                uiState.canVerify
                            ) {
                                focusManager
                                    .clearFocus()

                                onVerifyClick()
                            }
                        }
                    )
            )
        }

        if (
            !uiState.message
                .isNullOrBlank()
        ) {
            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Text(
                text =
                    uiState.message,

                color =
                    if (uiState.isError) {
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

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        if (!uiState.isCodeSent) {
            Button(
                onClick = {
                    focusManager
                        .clearFocus()

                    onRequestCodeClick()
                },

                modifier =
                    Modifier.fillMaxWidth(),

                enabled =
                    uiState.canRequestCode
            ) {
                if (
                    uiState.isRequestingCode
                ) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.height(
                                22.dp
                            ),
                        strokeWidth =
                            2.dp
                    )
                } else {
                    Text(
                        "Doğrulama Kodu Gönder"
                    )
                }
            }
        } else {
            Button(
                onClick = {
                    focusManager
                        .clearFocus()

                    onVerifyClick()
                },

                modifier =
                    Modifier.fillMaxWidth(),

                enabled =
                    uiState.canVerify
            ) {
                if (uiState.isVerifying) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.height(
                                22.dp
                            ),
                        strokeWidth =
                            2.dp
                    )
                } else {
                    Text(
                        "Telefonu Doğrula"
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            TextButton(
                onClick =
                    onRequestCodeClick,

                modifier =
                    Modifier.fillMaxWidth(),

                enabled =
                    !uiState.isBusy &&
                            resendSeconds == 0
            ) {
                Text(
                    text =
                        if (
                            resendSeconds > 0
                        ) {
                            "Kodu Tekrar Gönder ($resendSeconds sn)"
                        } else {
                            "Kodu Tekrar Gönder"
                        }
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )
    }
}