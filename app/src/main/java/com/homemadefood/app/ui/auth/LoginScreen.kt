package com.homemadefood.app.ui.auth

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homemadefood.app.R

@Composable
fun LoginScreen(
    uiState: AuthUiState,

    onLoginClick: (
        email: String,
        password: String
    ) -> Unit,

    onNavigateToRegister: () -> Unit,

    onForgotPasswordClick: () -> Unit = {},

    onClearMessage: () -> Unit = {},

    modifier: Modifier = Modifier
) {
    var email by rememberSaveable {
        mutableStateOf("")
    }

    /*
     * Şifre güvenlik nedeniyle rememberSaveable
     * kullanılmadan yalnız bellekte tutulur.
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
            modifier.fillMaxSize()
    ) {
        /*
         * Tek parça auth background.
         * Altındaki yapay navigation bar temizlenmiş
         * drawable kullanılır.
         */
        Image(
            painter =
                painterResource(
                    id =
                        R.drawable
                            .auth_login_background
                ),
            contentDescription = null,
            modifier =
                Modifier.fillMaxSize(),
            contentScale =
                ContentScale.Crop,
            alignment =
                Alignment.TopCenter
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 20.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            /*
             * Mockup'a göre header biraz yukarıda
             * tutuluyor. System status bar'dan sonra
             * kontrollü bir nefes alanı bırakılır.
             */
            Spacer(
                modifier =
                    Modifier.height(44.dp)
            )

            AuthBrandLogo()

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Text(
                text = "HomemadeFood",
                color =
                    AuthVisualColors.BrandOlive,
                fontFamily =
                    FontFamily.Serif,
                fontWeight =
                    FontWeight.Medium,
                fontSize = 39.sp,
                lineHeight = 44.sp,
                textAlign =
                    TextAlign.Center
            )

            Text(
                text =
                    "Ev yapımı lezzetlere güvenle ulaşın",
                modifier =
                    Modifier.padding(
                        top = 6.dp
                    ),
                color =
                    AuthVisualColors
                        .BrandOlive
                        .copy(
                            alpha = 0.86f
                        ),
                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,
                fontWeight =
                    FontWeight.Medium,
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )

            AuthGlassCard(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .widthIn(
                            max = 520.dp
                        )
            ) {
                Text(
                    text = "Tekrar hoş geldiniz",
                    color =
                        AuthVisualColors
                            .OliveText,
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "Devam etmek için hesabınıza giriş yapın.",
                    modifier =
                        Modifier.padding(
                            top = 8.dp
                        ),
                    color =
                        AuthVisualColors
                            .OliveText
                            .copy(
                                alpha = 0.90f
                            ),
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge
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

                    supportingText =
                        emailError?.let { message ->
                            {
                                Text(message)
                            }
                        },

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    colors =
                        authTextFieldColors(),

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
                        Modifier.height(14.dp)
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

                    supportingText =
                        passwordError?.let { message ->
                            {
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
                                    horizontal = 8.dp
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
                                    },

                                color =
                                    AuthVisualColors
                                        .DeepOlive,

                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }
                    },

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    colors =
                        authTextFieldColors(),

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
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 6.dp
                            ),

                    horizontalArrangement =
                        Arrangement.End
                ) {
                    TextButton(
                        onClick =
                            onForgotPasswordClick,

                        enabled =
                            !uiState.isLoading,

                        contentPadding =
                            PaddingValues(
                                horizontal = 4.dp,
                                vertical = 6.dp
                            )
                    ) {
                        Text(
                            text =
                                "Şifremi Unuttum",

                            color =
                                AuthVisualColors
                                    .DeepOlive,

                            style =
                                MaterialTheme
                                    .typography
                                    .titleSmall,

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }

                if (
                    !uiState.message
                        .isNullOrBlank()
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    AuthMessageCard(
                        message =
                            uiState.message,
                        isError =
                            uiState.isError,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            if (
                                uiState.message
                                    .isNullOrBlank()
                            ) {
                                18.dp
                            } else {
                                16.dp
                            }
                        )
                )

                Button(
                    onClick = {
                        submitLogin()
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(58.dp),

                    enabled =
                        !uiState.isLoading,

                    shape =
                        RoundedCornerShape(
                            18.dp
                        ),

                    colors =
                        authPrimaryButtonColors(),

                    contentPadding =
                        PaddingValues(
                            horizontal = 20.dp
                        )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.height(
                                    24.dp
                                ),
                            strokeWidth = 2.dp,
                            color =
                                androidx.compose
                                    .ui
                                    .graphics
                                    .Color
                                    .White
                        )
                    } else {
                        Text(
                            text = "Giriş Yap",
                            fontSize = 20.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
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
                            "Hesabınız yok mu? Kayıt olun",

                        color =
                            AuthVisualColors
                                .DeepOlive,

                        style =
                            MaterialTheme
                                .typography
                                .titleSmall,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )
        }
    }
}