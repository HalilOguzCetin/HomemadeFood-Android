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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onRegisterClick: (
        fullName: String,
        email: String,
        password: String,
        phone: String
    ) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var phone by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    var localError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = modifier
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
            text = "Hesap Oluştur",
            style =
                MaterialTheme.typography
                    .headlineLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "Ev yapımı lezzetleri keşfetmeye başlayın",
            style =
                MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                localError = null
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("Ad Soyad")
            },
            singleLine = true,
            enabled = !uiState.isLoading,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Text,
                    imeAction =
                        ImeAction.Next
                )
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                localError = null
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("E-posta")
            },
            singleLine = true,
            enabled = !uiState.isLoading,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Email,
                    imeAction =
                        ImeAction.Next
                )
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                localError = null
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("Telefon")
            },
            placeholder = {
                Text("05551234567")
            },
            singleLine = true,
            enabled = !uiState.isLoading,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Phone,
                    imeAction =
                        ImeAction.Next
                )
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                localError = null
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("Şifre")
            },
            singleLine = true,
            enabled = !uiState.isLoading,
            visualTransformation =
                PasswordVisualTransformation(),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Password,
                    imeAction =
                        ImeAction.Next
                )
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                localError = null
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("Şifre Tekrar")
            },
            singleLine = true,
            enabled = !uiState.isLoading,
            visualTransformation =
                PasswordVisualTransformation(),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Password,
                    imeAction =
                        ImeAction.Done
                )
        )

        val screenMessage =
            localError ?: uiState.message

        if (!screenMessage.isNullOrBlank()) {
            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Text(
                text = screenMessage,
                color =
                    if (
                        localError != null ||
                        uiState.isError
                    ) {
                        MaterialTheme
                            .colorScheme.error
                    } else {
                        MaterialTheme
                            .colorScheme.primary
                    },
                style =
                    MaterialTheme.typography
                        .bodyMedium
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {
                when {
                    password != confirmPassword -> {
                        localError =
                            "Şifreler birbiriyle eşleşmiyor."
                    }

                    password.length < 6 -> {
                        localError =
                            "Şifre en az 6 karakter olmalıdır."
                    }

                    else -> {
                        localError = null

                        onRegisterClick(
                            fullName,
                            email,
                            password,
                            phone
                        )
                    }
                }
            },
            modifier =
                Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier =
                        Modifier.height(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Kayıt Ol")
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        TextButton(
            onClick = onNavigateToLogin,
            enabled = !uiState.isLoading
        ) {
            Text(
                text =
                    "Zaten hesabınız var mı? Giriş yapın"
            )
        }
    }
}