package com.homemadefood.app.ui.auth

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homemadefood.app.R

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

    val visibleEmailError =
        if (submitAttempted) {
            emailError
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

        if (emailError != null) {
            return
        }

        focusManager.clearFocus()
        onClearMessage()

        onRequestResetClick(
            normalizedEmail
        )
    }

    Box(
        modifier =
            modifier.fillMaxSize()
    ) {
        Image(
            painter =
                painterResource(
                    id =
                        R.drawable
                            .auth_forgot_password_background
                ),
            contentDescription = null,
            modifier =
                Modifier.fillMaxSize(),
            contentScale =
                ContentScale.Crop,
            alignment =
                Alignment.Center
        )

        /*
         * Arka planı Login kadar güçlü bırakıyoruz fakat
         * yazı/kart okunabilirliği için çok hafif sıcak
         * koyu bir overlay kullanıyoruz.
         */
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Color(0xFF2C2117)
                            .copy(
                                alpha = 0.10f
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
            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.Start
            ) {
                Surface(
                    onClick = {
                        if (!uiState.isLoading) {
                            onClearMessage()
                            onNavigateToLogin()
                        }
                    },

                    modifier =
                        Modifier
                            .padding(
                                top = 8.dp
                            )
                            .size(42.dp),

                    shape = CircleShape,

                    color =
                        Color(0xFF231A13)
                            .copy(
                                alpha = 0.58f
                            ),

                    border =
                        BorderStroke(
                            width = 1.dp,
                            color =
                                Color.White
                                    .copy(
                                        alpha = 0.52f
                                    )
                        ),

                    shadowElevation = 3.dp
                ) {
                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text = "←",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            AuthBrandLogo(
                modifier =
                    Modifier.size(76.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text = "HomemadeFood",
                color =
                    AuthVisualColors.Cream,
                fontFamily =
                    FontFamily.Serif,
                fontWeight =
                    FontWeight.Medium,
                fontSize = 27.sp,
                lineHeight = 31.sp,
                textAlign =
                    TextAlign.Center
            )

            Text(
                text = "Ev yapımı lezzetler",
                modifier =
                    Modifier.padding(
                        top = 1.dp
                    ),
                color =
                    AuthVisualColors.Cream
                        .copy(
                            alpha = 0.88f
                        ),
                style =
                    MaterialTheme
                        .typography
                        .labelMedium,
                letterSpacing = 1.5.sp,
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
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
                        vertical = 20.dp
                    )
            ) {
                Surface(
                    modifier =
                        Modifier
                            .align(
                                Alignment.CenterHorizontally
                            )
                            .size(68.dp),

                    shape = CircleShape,

                    color =
                        Color(0xFFFFF5DF)
                            .copy(
                                alpha = 0.94f
                            ),

                    border =
                        BorderStroke(
                            width = 1.dp,
                            color =
                                AuthVisualColors.Gold
                                    .copy(
                                        alpha = 0.72f
                                    )
                        ),

                    shadowElevation = 5.dp
                ) {
                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text = "✉",
                            color =
                                AuthVisualColors
                                    .DeepOlive,
                            fontSize = 30.sp
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text = "Şifremi Unuttum",
                    modifier =
                        Modifier.fillMaxWidth(),
                    color =
                        AuthVisualColors
                            .OliveText,
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    fontWeight =
                        FontWeight.Bold,
                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text =
                        "Hesabınızda kullandığınız e-posta adresini girin. Uygunsa 6 haneli şifre sıfırlama kodu oluşturulacaktır.",
                    modifier =
                        Modifier.fillMaxWidth(),
                    color =
                        AuthVisualColors
                            .OliveText
                            .copy(
                                alpha = 0.90f
                            ),
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                OutlinedTextField(
                    value = email,

                    onValueChange = { value ->
                        if (
                            value.length <= 255
                        ) {
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
                            "E-posta adresinizi girin"
                        )
                    },

                    leadingIcon = {
                        Text(
                            text = "✉",
                            color =
                                AuthVisualColors
                                    .DeepOlive,
                            fontSize = 20.sp
                        )
                    },

                    singleLine = true,

                    enabled =
                        !uiState.isLoading,

                    isError =
                        visibleEmailError != null,

                    supportingText =
                        visibleEmailError
                            ?.let { error ->
                                {
                                    Text(error)
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
                        Modifier.height(14.dp)
                )

                Button(
                    onClick = {
                        submit()
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp),

                    enabled =
                        !uiState.isLoading,

                    shape =
                        RoundedCornerShape(
                            18.dp
                        ),

                    colors =
                        authPrimaryButtonColors()
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
                            text =
                                "Sıfırlama Kodu İste",
                            fontSize = 18.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text = "veya",
                    modifier =
                        Modifier.fillMaxWidth(),
                    color =
                        AuthVisualColors
                            .OliveText
                            .copy(
                                alpha = 0.56f
                            ),
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                OutlinedButton(
                    onClick = {
                        onClearMessage()
                        onNavigateToLogin()
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                    enabled =
                        !uiState.isLoading,

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    border =
                        BorderStroke(
                            width = 1.dp,
                            color =
                                AuthVisualColors
                                    .DeepOlive
                                    .copy(
                                        alpha = 0.66f
                                    )
                        )
                ) {
                    Text(
                        text =
                            "←  Giriş ekranına dön",

                        color =
                            AuthVisualColors
                                .DeepOlive,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    color =
                        Color(0xFFFFF7EA)
                            .copy(
                                alpha = 0.90f
                            ),

                    border =
                        BorderStroke(
                            width = 1.dp,
                            color =
                                AuthVisualColors.Gold
                                    .copy(
                                        alpha = 0.60f
                                    )
                        )
                ) {
                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 11.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Text(
                            text = "◷",
                            color =
                                AuthVisualColors
                                    .DeepOlive,
                            fontSize = 22.sp
                        )

                        Text(
                            text =
                                "E-posta adresinize gönderilecek kod 10 dakika geçerlidir. Kod gelmezse spam klasörünüzü kontrol edin.",
                            modifier =
                                Modifier.padding(
                                    start = 12.dp
                                ),
                            color =
                                AuthVisualColors
                                    .OliveText,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )
        }
    }
}