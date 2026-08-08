package com.homemadefood.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationScreen(
    email: String,
    uiState: AuthUiState,
    onVerifyClick: (
        code: String
    ) -> Unit,
    onResendClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager =
        LocalFocusManager.current

    /*
     * Doğrulama kodu one-time secret olduğu için
     * rememberSaveable ile kalıcılaştırılmaz.
     */
    var code by remember {
        mutableStateOf("")
    }

    var localError by remember {
        mutableStateOf<String?>(null)
    }

    var resendSeconds by rememberSaveable(
        email
    ) {
        mutableIntStateOf(60)
    }

    /*
     * Kayıt ekranından gelindiğinde ilk resend
     * için 60 saniye bekletilir.
     */
    LaunchedEffect(
        resendSeconds
    ) {
        if (resendSeconds > 0) {
            delay(1_000)
            resendSeconds--
        }
    }

    /*
     * Backend resend isteğini HTTP açısından
     * başarılı kabul ettiğinde ViewModel bu
     * sayacı artırır ve UI cooldown'ı yeniler.
     */
    LaunchedEffect(
        uiState.resendRequestVersion
    ) {
        if (
            uiState.resendRequestVersion > 0
        ) {
            resendSeconds = 60
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
                .padding(
                    horizontal = 24.dp,
                    vertical = 32.dp
                ),
        verticalArrangement =
            Arrangement.Center,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text =
                "E-posta Doğrulama",
            style =
                MaterialTheme
                    .typography
                    .headlineLarge
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Text(
            text =
                "$email adresine gönderilen 6 haneli kodu girin.",
            style =
                MaterialTheme
                    .typography
                    .bodyLarge
        )

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        OutlinedTextField(
            value = code,

            onValueChange = { value ->
                val filtered =
                    value.filter {
                        it.isDigit()
                    }

                if (filtered.length <= 6) {
                    code = filtered
                    localError = null

                    if (
                        !uiState.message
                            .isNullOrBlank()
                    ) {
                        onClearMessage()
                    }
                }
            },

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
                !uiState.isLoading,

            isError =
                localError != null,

            supportingText = {
                if (localError != null) {
                    Text(localError!!)
                }
            },

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.NumberPassword,
                    imeAction =
                        ImeAction.Done
                ),

            keyboardActions =
                KeyboardActions(
                    onDone = {
                        if (
                            !uiState.isLoading
                        ) {
                            if (
                                code.length == 6
                            ) {
                                focusManager
                                    .clearFocus()

                                onVerifyClick(
                                    code
                                )
                            } else {
                                localError =
                                    "Doğrulama kodu 6 haneli olmalıdır."
                            }
                        }
                    }
                )
        )

        if (
            !uiState.message
                .isNullOrBlank()
        ) {
            Spacer(
                modifier =
                    Modifier.height(12.dp)
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

        Button(
            onClick = {
                if (code.length != 6) {
                    localError =
                        "Doğrulama kodu 6 haneli olmalıdır."
                } else {
                    localError = null

                    focusManager
                        .clearFocus()

                    onVerifyClick(
                        code
                    )
                }
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                !uiState.isLoading
        ) {
            if (uiState.isLoading) {
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
                    "E-postayı Doğrula"
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        TextButton(
            onClick = {
                onClearMessage()
                onResendClick()
            },

            enabled =
                !uiState.isLoading &&
                        resendSeconds == 0
        ) {
            Text(
                if (resendSeconds > 0) {
                    "Kodu Tekrar Gönder ($resendSeconds sn)"
                } else {
                    "Kodu Tekrar Gönder"
                }
            )
        }

        TextButton(
            onClick = {
                onClearMessage()
                onNavigateToLogin()
            },

            enabled =
                !uiState.isLoading
        ) {
            Text(
                "Giriş ekranına dön"
            )
        }
    }
}