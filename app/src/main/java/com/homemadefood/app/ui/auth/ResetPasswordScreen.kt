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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ResetPasswordScreen(
    email: String,
    uiState: AuthUiState,
    onResetPasswordClick: (
        code: String,
        newPassword: String
    ) -> Unit,
    onResendCodeClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearMessage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager =
        LocalFocusManager.current

    val newPasswordFocusRequester =
        remember {
            FocusRequester()
        }

    val confirmPasswordFocusRequester =
        remember {
            FocusRequester()
        }

    /*
     * Kod ve şifreler hassas olduğu için
     * rememberSaveable kullanılmaz.
     */
    var code by remember {
        mutableStateOf("")
    }

    var newPassword by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var newPasswordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    var submitAttempted by remember {
        mutableStateOf(false)
    }

    /*
     * İlk forgot-password çağrısından sonra
     * backend tarafındaki 60 saniyelik cooldown
     * ile UI aynı süreyi bekler.
     */
    var resendSeconds by rememberSaveable(
        email
    ) {
        mutableIntStateOf(60)
    }

    LaunchedEffect(
        resendSeconds
    ) {
        if (resendSeconds > 0) {
            delay(1_000)
            resendSeconds--
        }
    }

    LaunchedEffect(
        uiState
            .passwordResetResendRequestVersion
    ) {
        if (
            uiState
                .passwordResetResendRequestVersion > 0
        ) {
            resendSeconds = 60
        }
    }

    val codeError =
        when {
            code.isBlank() ->
                "Şifre sıfırlama kodu zorunludur."

            code.length != 6 ->
                "Şifre sıfırlama kodu 6 haneli olmalıdır."

            code.any {
                !it.isDigit()
            } ->
                "Şifre sıfırlama kodu yalnızca rakamlardan oluşmalıdır."

            else ->
                null
        }

    val newPasswordError =
        when {
            newPassword.isBlank() ->
                "Yeni şifre zorunludur."

            newPassword.length < 8 ->
                "Şifre en az 8 karakter olmalıdır."

            newPassword.length > 100 ->
                "Şifre en fazla 100 karakter olabilir."

            newPassword.none {
                it.isUpperCase()
            } ->
                "Şifre en az bir büyük harf içermelidir."

            newPassword.none {
                it.isLowerCase()
            } ->
                "Şifre en az bir küçük harf içermelidir."

            newPassword.none {
                it.isDigit()
            } ->
                "Şifre en az bir rakam içermelidir."

            newPassword.none {
                !it.isLetterOrDigit()
            } ->
                "Şifre en az bir özel karakter içermelidir."

            else ->
                null
        }

    val confirmPasswordError =
        when {
            confirmPassword.isBlank() ->
                "Yeni şifre tekrar alanı zorunludur."

            newPassword !=
                    confirmPassword ->
                "Şifreler birbiriyle eşleşmiyor."

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

        if (
            codeError != null ||
            newPasswordError != null ||
            confirmPasswordError != null
        ) {
            return
        }

        focusManager.clearFocus()
        onClearMessage()

        onResetPasswordClick(
            code,
            newPassword
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
            text = "Yeni Şifre Belirle",
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
                "$email adresi için oluşturulan 6 haneli kodu ve yeni şifrenizi girin.",
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
                    clearServerMessageIfNeeded()
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
                submitAttempted &&
                        codeError != null,

            supportingText = {
                if (
                    submitAttempted &&
                    codeError != null
                ) {
                    Text(codeError)
                }
            },

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.NumberPassword,

                    imeAction =
                        ImeAction.Next
                ),

            keyboardActions =
                KeyboardActions(
                    onNext = {
                        newPasswordFocusRequester
                            .requestFocus()
                    }
                )
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = newPassword,

            onValueChange = { value ->
                if (value.length <= 100) {
                    newPassword = value
                    clearServerMessageIfNeeded()
                }
            },

            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(
                        newPasswordFocusRequester
                    ),

            label = {
                Text("Yeni Şifre")
            },

            supportingText = {
                if (
                    submitAttempted &&
                    newPasswordError != null
                ) {
                    Text(
                        newPasswordError
                    )
                } else {
                    Text(
                        "En az 8 karakter; büyük/küçük harf, rakam ve özel karakter."
                    )
                }
            },

            isError =
                submitAttempted &&
                        newPasswordError != null,

            singleLine = true,

            enabled =
                !uiState.isLoading,

            visualTransformation =
                if (
                    newPasswordVisible
                ) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

            trailingIcon = {
                TextButton(
                    onClick = {
                        newPasswordVisible =
                            !newPasswordVisible
                    },

                    enabled =
                        !uiState.isLoading
                ) {
                    Text(
                        if (
                            newPasswordVisible
                        ) {
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
                Text(
                    "Yeni Şifre Tekrar"
                )
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

            enabled =
                !uiState.isLoading,

            visualTransformation =
                if (
                    confirmPasswordVisible
                ) {
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
                        if (
                            confirmPasswordVisible
                        ) {
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
                    "Şifreyi Güncelle"
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
                onResendCodeClick()
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