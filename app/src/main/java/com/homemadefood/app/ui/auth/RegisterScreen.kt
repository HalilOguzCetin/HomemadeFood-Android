package com.homemadefood.app.ui.auth

import android.util.Patterns
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
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
    val focusManager =
        LocalFocusManager.current

    val emailFocusRequester =
        remember {
            FocusRequester()
        }

    val passwordFocusRequester =
        remember {
            FocusRequester()
        }

    val confirmPasswordFocusRequester =
        remember {
            FocusRequester()
        }

    var fullName by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    /*
     * Şifre alanları güvenlik nedeniyle
     * rememberSaveable kullanılmadan yalnızca
     * bellekte tutulur.
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

    var submitAttempted by remember {
        mutableStateOf(false)
    }

    val normalizedFullName =
        fullName.trim()

    val normalizedEmail =
        email
            .trim()
            .lowercase()

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

            password.none {
                it.isUpperCase()
            } ->
                "Şifre en az bir büyük harf içermelidir."

            password.none {
                it.isLowerCase()
            } ->
                "Şifre en az bir küçük harf içermelidir."

            password.none {
                it.isDigit()
            } ->
                "Şifre en az bir rakam içermelidir."

            password.none {
                !it.isLetterOrDigit()
            } ->
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

    val visibleFullNameError =
        if (submitAttempted) {
            fullNameError
        } else {
            null
        }

    val visibleEmailError =
        if (submitAttempted) {
            emailError
        } else {
            null
        }

    val visiblePasswordError =
        if (submitAttempted) {
            passwordError
        } else {
            null
        }

    val visibleConfirmPasswordError =
        if (submitAttempted) {
            confirmPasswordError
        } else {
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

    Box(
        modifier =
            modifier.fillMaxSize()
    ) {
        /*
         * Login ile AYNI drawable kullanılır.
         * Ayrı register background dosyası gerekmez.
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

        /*
         * Register formu daha uzun olduğu için
         * aynı arka plan biraz sakinleştirilir.
         *
         * Daha görünür arka plan istersen:
         * alpha = 0.10f
         *
         * Daha sade istersen:
         * alpha = 0.28f
         */
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Color(0xFFFFF7ED)
                            .copy(
                                alpha = 0.18f
                            )
                    )
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
            Spacer(
                modifier =
                    Modifier.height(34.dp)
            )

            /*
             * Register'da Login kadar büyük logo
             * kullanılmıyor. Ekran bilinçli olarak
             * daha sade tutuluyor.
             */
            Text(
                text = "HomemadeFood",
                color =
                    AuthVisualColors
                        .BrandOlive,
                fontFamily =
                    FontFamily.Serif,
                fontWeight =
                    FontWeight.Medium,
                fontSize = 25.sp,
                lineHeight = 30.sp,
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = "Hesap Oluştur",
                color =
                    AuthVisualColors
                        .OliveText,
                style =
                    MaterialTheme
                        .typography
                        .headlineLarge,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Center
            )

            Text(
                text =
                    "Ev yapımı lezzetleri keşfetmeye başlayın",
                modifier =
                    Modifier.padding(
                        top = 6.dp
                    ),
                color =
                    AuthVisualColors.OliveText,
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
                    Modifier.height(22.dp)
            )

            AuthGlassCard(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .widthIn(
                            max = 520.dp
                        ),

                contentPadding =
                    PaddingValues(
                        horizontal = 22.dp,
                        vertical = 22.dp
                    )
            ) {
                OutlinedTextField(
                    value = fullName,

                    onValueChange = { value ->
                        if (
                            value.length <= 100
                        ) {
                            fullName = value
                            clearServerMessageIfNeeded()
                        }
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Ad Soyad")
                    },

                    placeholder = {
                        Text(
                            "Adınız ve soyadınız"
                        )
                    },

                    supportingText =
                        visibleFullNameError
                            ?.let { error ->
                                {
                                    Text(error)
                                }
                            },

                    isError =
                        visibleFullNameError != null,

                    singleLine = true,

                    enabled =
                        !uiState.isLoading,

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    colors =
                        authTextFieldColors(),

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
                        Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = email,

                    onValueChange = { value ->
                        if (
                            value.length <= 255
                        ) {
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

                    placeholder = {
                        Text(
                            "ornek@eposta.com"
                        )
                    },

                    supportingText =
                        visibleEmailError
                            ?.let { error ->
                                {
                                    Text(error)
                                }
                            },

                    isError =
                        visibleEmailError != null,

                    singleLine = true,

                    enabled =
                        !uiState.isLoading,

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
                        Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = password,

                    onValueChange = { value ->
                        if (
                            value.length <= 100
                        ) {
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

                    supportingText =
                        visiblePasswordError
                            ?.let { error ->
                                {
                                    Text(error)
                                }
                            },

                    isError =
                        visiblePasswordError != null,

                    singleLine = true,

                    enabled =
                        !uiState.isLoading,

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

                if (
                    visiblePasswordError == null
                ) {
                    Text(
                        text =
                            "En az 8 karakter; büyük/küçük harf, rakam ve özel karakter.",
                        modifier =
                            Modifier.padding(
                                start = 4.dp,
                                top = 6.dp
                            ),
                        color =
                            AuthVisualColors
                                .OliveText
                                .copy(
                                    alpha = 0.72f
                                ),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value =
                        confirmPassword,

                    onValueChange = { value ->
                        if (
                            value.length <= 100
                        ) {
                            confirmPassword =
                                value

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

                    supportingText =
                        visibleConfirmPasswordError
                            ?.let { error ->
                                {
                                    Text(error)
                                }
                            },

                    isError =
                        visibleConfirmPasswordError != null,

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
                                !uiState.isLoading,

                            contentPadding =
                                PaddingValues(
                                    horizontal = 8.dp
                                )
                        ) {
                            Text(
                                text =
                                    if (
                                        confirmPasswordVisible
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
                            Modifier.height(14.dp)
                    )

                    AuthMessageCard(
                        message =
                            uiState.message,
                        isError =
                            uiState.isError,
                        modifier =
                            Modifier.fillMaxWidth()
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
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Kayıt Ol",
                            fontSize = 19.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                TextButton(
                    onClick =
                        onNavigateToLogin,

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !uiState.isLoading
                ) {
                    Row(
                        horizontalArrangement =
                            Arrangement.Center,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Text(
                            text =
                                "Zaten hesabınız var mı? ",
                            color =
                                AuthVisualColors
                                    .DeepOlive,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleSmall,
                            fontWeight =
                                FontWeight.Medium
                        )

                        Text(
                            text = "Giriş yapın",
                            color =
                                AuthVisualColors
                                    .Terracotta,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleSmall,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )
        }
    }
}