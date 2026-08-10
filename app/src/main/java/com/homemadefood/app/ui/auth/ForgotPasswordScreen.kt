package com.homemadefood.app.ui.auth

import android.util.Patterns
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ForgotPasswordScreen(
    uiState: AuthUiState,
    onRequestResetClick: (
        email: String
    ) -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearMessage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager =
        LocalFocusManager.current

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var submitAttempted by rememberSaveable {
        mutableStateOf(false)
    }

    val normalizedEmail =
        email
            .trim()
            .lowercase()

    val emailError =
        when {
            normalizedEmail.isBlank() ->
                "E-posta adresi zorunludur."

            normalizedEmail.length > 255 ->
                "E-posta adresi en fazla 255 karakter olabilir."

            !Patterns.EMAIL_ADDRESS
                .matcher(normalizedEmail)
                .matches() ->
                "Geçerli bir e-posta adresi girin."

            else ->
                null
        }

    fun clearServerMessageIfNeeded() {
        if (
            !uiState.message
                .isNullOrBlank()
        ) {
            onClearMessage()
        }
    }

    fun submit() {
        if (uiState.isLoading) {
            return
        }

        submitAttempted = true

        if (emailError != null) {
            return
        }

        focusManager.clearFocus()
        onClearMessage()

        onRequestResetClick(
            normalizedEmail
        )
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
            text = "Şifremi Unuttum",
            style =
                MaterialTheme
                    .typography
                    .headlineLarge
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "Hesabınızda kullandığınız e-posta adresini girin. Uygunsa 6 haneli şifre sıfırlama kodu oluşturulacaktır.",
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
            value = email,

            onValueChange = { value ->
                if (value.length <= 255) {
                    email =
                        value.trimStart()

                    clearServerMessageIfNeeded()
                }
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("E-posta")
            },

            placeholder = {
                Text(
                    "ornek@eposta.com"
                )
            },

            singleLine = true,

            enabled =
                !uiState.isLoading,

            isError =
                submitAttempted &&
                        emailError != null,

            supportingText = {
                if (
                    submitAttempted &&
                    emailError != null
                ) {
                    Text(emailError)
                }
            },

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Email,

                    imeAction =
                        ImeAction.Done
                ),

            keyboardActions =
                KeyboardActions(
                    onDone = {
                        submit()
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
                submit()
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
                    "Sıfırlama Kodu İste"
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