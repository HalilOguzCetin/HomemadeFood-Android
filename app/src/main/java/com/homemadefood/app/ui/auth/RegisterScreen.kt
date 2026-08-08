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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onRegisterClick: (
        fullName: String,
        email: String,
        password: String
    ) -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearMessage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }

    var fullName by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    /*
     * Şifreler rememberSaveable kullanılmadan
     * yalnızca bellekte tutulur.
     */
    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    /*
     * Alan hataları yalnızca kullanıcı kayıt
     * işlemini denedikten sonra gösterilir.
     */
    var submitAttempted by remember {
        mutableStateOf(false)
    }

    val normalizedFullName = fullName.trim()

    val normalizedEmail =
        email.trim().lowercase()

    val fullNameError =
        when {
            normalizedFullName.isBlank() ->
                "Ad soyad zorunludur."

            normalizedFullName.length < 2 ->
                "Ad soyad en az 2 karakter olmalıdır."

            normalizedFullName.length > 100 ->
                "Ad soyad en fazla 100 karakter olabilir."

            else ->
                null
        }

    val isEmailFormatValid =
        Patterns.EMAIL_ADDRESS
            .matcher(normalizedEmail)
            .matches() &&
                normalizedEmail
                    .substringAfter(
                        "@",
                        ""
                    )
                    .contains(".") &&
                !normalizedEmail.endsWith(".")

    val emailError =
        when {
            normalizedEmail.isBlank() ->
                "E-posta adresi zorunludur."

            normalizedEmail.length > 255 ->
                "E-posta adresi en fazla 255 karakter olabilir."

            !isEmailFormatValid ->
                "Geçerli bir e-posta adresi girin. Örnek: ad@ornek.com"

            else ->
                null
        }

    val passwordError =
        when {
            password.isBlank() ->
                "Şifre zorunludur."

            password.length < 8 ->
                "Şifre en az 8 karakter olmalıdır."

            password.length > 100 ->
                "Şifre en fazla 100 karakter olabilir."

            password.none { it.isUpperCase() } ->
                "Şifre en az bir büyük harf içermelidir."

            password.none { it.isLowerCase() } ->
                "Şifre en az bir küçük harf içermelidir."

            password.none { it.isDigit() } ->
                "Şifre en az bir rakam içermelidir."

            password.none { !it.isLetterOrDigit() } ->
                "Şifre en az bir özel karakter içermelidir."

            else ->
                null
        }

    val confirmPasswordError =
        when {
            confirmPassword.isBlank() ->
                "Şifre tekrar alanı zorunludur."

            password != confirmPassword ->
                "Şifreler birbiriyle eşleşmiyor."

            else ->
                null
        }

    fun clearServerMessageIfNeeded() {
        if (!uiState.message.isNullOrBlank()) {
            onClearMessage()
        }
    }

    fun submit() {
        submitAttempted = true

        /*
         * Formdaki tek bir alan bile geçersizse
         * backend'e kayıt isteği gönderilmez.
         */
        if (
            fullNameError != null ||
            emailError != null ||
            passwordError != null ||
            confirmPasswordError != null
        ) {
            return
        }

        focusManager.clearFocus()
        onClearMessage()

        onRegisterClick(
            normalizedFullName,
            normalizedEmail,
            password
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
            text = "Hesap Oluştur",
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
                "Ev yapımı lezzetleri keşfetmeye başlayın",
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
            value = fullName,
            onValueChange = { value ->
                if (value.length <= 100) {
                    fullName = value
                    clearServerMessageIfNeeded()
                }
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("Ad Soyad")
            },
            supportingText = {
                if (
                    submitAttempted &&
                    fullNameError != null
                ) {
                    Text(fullNameError)
                }
            },
            isError =
                submitAttempted &&
                        fullNameError != null,
            singleLine = true,
            enabled = !uiState.isLoading,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Text,
                    imeAction =
                        ImeAction.Next
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        emailFocusRequester
                            .requestFocus()
                    }
                )
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { value ->
                if (value.length <= 255) {
                    email = value
                    clearServerMessageIfNeeded()
                }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(
                        emailFocusRequester
                    ),
            label = {
                Text("E-posta")
            },
            supportingText = {
                if (
                    submitAttempted &&
                    emailError != null
                ) {
                    Text(emailError)
                }
            },
            isError =
                submitAttempted &&
                        emailError != null,
            singleLine = true,
            enabled = !uiState.isLoading,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Email,
                    imeAction =
                        ImeAction.Next
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        passwordFocusRequester
                            .requestFocus()
                    }
                )
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { value ->
                if (value.length <= 100) {
                    password = value
                    clearServerMessageIfNeeded()
                }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(
                        passwordFocusRequester
                    ),
            label = {
                Text("Şifre")
            },
            supportingText = {
                if (
                    submitAttempted &&
                    passwordError != null
                ) {
                    Text(passwordError)
                } else {
                    Text(
                        "En az 8 karakter; büyük/küçük harf, rakam ve özel karakter."
                    )
                }
            },
            isError =
                submitAttempted &&
                        passwordError != null,
            singleLine = true,
            enabled = !uiState.isLoading,
            visualTransformation =
                if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            trailingIcon = {
                TextButton(
                    onClick = {
                        passwordVisible =
                            !passwordVisible
                    },
                    enabled =
                        !uiState.isLoading
                ) {
                    Text(
                        if (passwordVisible) {
                            "Gizle"
                        } else {
                            "Göster"
                        }
                    )
                }
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Password,
                    imeAction =
                        ImeAction.Next
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        confirmPasswordFocusRequester
                            .requestFocus()
                    }
                )
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { value ->
                if (value.length <= 100) {
                    confirmPassword = value
                    clearServerMessageIfNeeded()
                }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(
                        confirmPasswordFocusRequester
                    ),
            label = {
                Text("Şifre Tekrar")
            },
            supportingText = {
                if (
                    submitAttempted &&
                    confirmPasswordError != null
                ) {
                    Text(
                        confirmPasswordError
                    )
                }
            },
            isError =
                submitAttempted &&
                        confirmPasswordError != null,
            singleLine = true,
            enabled = !uiState.isLoading,
            visualTransformation =
                if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            trailingIcon = {
                TextButton(
                    onClick = {
                        confirmPasswordVisible =
                            !confirmPasswordVisible
                    },
                    enabled =
                        !uiState.isLoading
                ) {
                    Text(
                        if (confirmPasswordVisible) {
                            "Gizle"
                        } else {
                            "Göster"
                        }
                    )
                }
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Password,
                    imeAction =
                        ImeAction.Done
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        if (!uiState.isLoading) {
                            submit()
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
                Text("Kayıt Ol")
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        TextButton(
            onClick =
                onNavigateToLogin,
            enabled =
                !uiState.isLoading
        ) {
            Text(
                "Zaten hesabınız var mı? Giriş yapın"
            )
        }
    }
}