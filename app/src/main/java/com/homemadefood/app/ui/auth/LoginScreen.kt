package com.homemadefood.app.ui.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    uiState: AuthUiState,

    onLoginClick: (
        email: String,
        password: String
    ) -> Unit,

    onNavigateToRegister: () -> Unit,

    /*
     * Şifremi unuttum ekranı hazırlandığında
     * Navigation tarafından bu callback bağlanacak.
     */
    onForgotPasswordClick: () -> Unit = {},

    /*
     * Kullanıcı formu düzenlemeye başladığında
     * önceki genel hata mesajını temizlemek için
     * AuthViewModel.clearMessage bağlanabilir.
     */
    onClearMessage: () -> Unit = {},

    modifier: Modifier = Modifier
) {
    var email by rememberSaveable {
        mutableStateOf("")
    }

    /*
     * Şifreyi rememberSaveable ile saklamıyoruz.
     * Böylece Activity yeniden oluşturulduğunda veya
     * ekran tekrar açıldığında şifre korunmaz.
     */
    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var emailError by remember {
        mutableStateOf<String?>(null)
    }

    var passwordError by remember {
        mutableStateOf<String?>(null)
    }

    val passwordFocusRequester =
        remember {
            FocusRequester()
        }

    val focusManager =
        LocalFocusManager.current

    fun submitLogin() {
        if (uiState.isLoading) {
            return
        }

        val normalizedEmail =
            email
                .trim()
                .lowercase()

        emailError =
            when {
                normalizedEmail.isBlank() ->
                    "E-posta alanı zorunludur."

                !Patterns.EMAIL_ADDRESS
                    .matcher(normalizedEmail)
                    .matches() ->
                    "Geçerli bir e-posta adresi girin."

                else ->
                    null
            }

        passwordError =
            if (password.isBlank()) {
                "Şifre alanı zorunludur."
            } else {
                null
            }

        if (
            emailError != null ||
            passwordError != null
        ) {
            return
        }

        focusManager.clearFocus()

        onLoginClick(
            normalizedEmail,
            password
        )
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    MaterialTheme
                                        .colorScheme
                                        .primaryContainer,

                                    MaterialTheme
                                        .colorScheme
                                        .background
                                )
                        )
                )
                .imePadding()
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 24.dp,
                        vertical = 32.dp
                    ),

            verticalArrangement =
                Arrangement.Center,

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Surface(
                modifier =
                    Modifier.size(82.dp),

                shape =
                    CircleShape,

                color =
                    MaterialTheme
                        .colorScheme
                        .primary,

                shadowElevation =
                    8.dp
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center
                ) {
                    Text(
                        text = "HF",

                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onPrimary,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Text(
                text = "HomemadeFood",

                style =
                    MaterialTheme
                        .typography
                        .headlineLarge,

                fontWeight =
                    FontWeight.Bold,

                color =
                    MaterialTheme
                        .colorScheme
                        .onBackground
            )

            Text(
                text =
                    "Ev yapımı lezzetlere güvenle ulaşın",

                modifier =
                    Modifier.padding(
                        top = 4.dp
                    ),

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )

            ElevatedCard(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .widthIn(
                            max = 520.dp
                        ),

                shape =
                    RoundedCornerShape(24.dp),

                elevation =
                    CardDefaults
                        .elevatedCardElevation(
                            defaultElevation =
                                8.dp
                        )
            ) {
                Column(
                    modifier =
                        Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Tekrar hoş geldiniz",

                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Text(
                        text =
                            "Devam etmek için hesabınıza giriş yapın.",

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

                    OutlinedTextField(
                        value = email,

                        onValueChange = { value ->
                            email =
                                value.trimStart()

                            emailError = null

                            if (
                                !uiState.message
                                    .isNullOrBlank()
                            ) {
                                onClearMessage()
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
                            emailError != null,

                        supportingText = {
                            emailError?.let {
                                    message ->

                                Text(message)
                            }
                        },

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
                            Modifier.height(10.dp)
                    )

                    OutlinedTextField(
                        value = password,

                        onValueChange = { value ->
                            password = value
                            passwordError = null

                            if (
                                !uiState.message
                                    .isNullOrBlank()
                            ) {
                                onClearMessage()
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

                        singleLine = true,

                        enabled =
                            !uiState.isLoading,

                        isError =
                            passwordError != null,

                        supportingText = {
                            passwordError?.let {
                                    message ->

                                Text(message)
                            }
                        },

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
                                    !uiState.isLoading,

                                contentPadding =
                                    PaddingValues(
                                        horizontal =
                                            8.dp
                                    )
                            ) {
                                Text(
                                    text =
                                        if (
                                            passwordVisible
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
                                    submitLogin()
                                }
                            )
                    )

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.End
                    ) {
                        TextButton(
                            onClick =
                                onForgotPasswordClick,

                            enabled =
                                !uiState.isLoading
                        ) {
                            Text(
                                "Şifremi Unuttum"
                            )
                        }
                    }

                    if (
                        !uiState.message
                            .isNullOrBlank()
                    ) {
                        Surface(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 4.dp
                                    ),

                            shape =
                                RoundedCornerShape(
                                    12.dp
                                ),

                            color =
                                if (
                                    uiState.isError
                                ) {
                                    MaterialTheme
                                        .colorScheme
                                        .errorContainer
                                } else {
                                    MaterialTheme
                                        .colorScheme
                                        .primaryContainer
                                }
                        ) {
                            Text(
                                text =
                                    uiState.message,

                                modifier =
                                    Modifier.padding(
                                        12.dp
                                    ),

                                color =
                                    if (
                                        uiState.isError
                                    ) {
                                        MaterialTheme
                                            .colorScheme
                                            .onErrorContainer
                                    } else {
                                        MaterialTheme
                                            .colorScheme
                                            .onPrimaryContainer
                                    },

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )

                    Button(
                        onClick = {
                            submitLogin()
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(52.dp),

                        enabled =
                            !uiState.isLoading,

                        shape =
                            RoundedCornerShape(
                                14.dp
                            )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(
                                        22.dp
                                    ),

                                strokeWidth =
                                    2.dp,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimary
                            )
                        } else {
                            Text(
                                text = "Giriş Yap",

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium,

                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    TextButton(
                        onClick =
                            onNavigateToRegister,

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            !uiState.isLoading
                    ) {
                        Text(
                            text =
                                "Hesabınız yok mu? Kayıt olun"
                        )
                    }
                }
            }
        }
    }
}