package com.homemadefood.app.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
    val focusManager = LocalFocusManager.current

    var resendSeconds by rememberSaveable {
        mutableIntStateOf(0)
    }

    LaunchedEffect(uiState.codeRequestVersion) {
        if (uiState.codeRequestVersion > 0) {
            resendSeconds = 60
        }
    }

    LaunchedEffect(resendSeconds) {
        if (resendSeconds > 0) {
            delay(1_000)
            resendSeconds--
        }
    }

    CustomerHomeTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CustomerHomeColors.Cream)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
        ) {
            TextButton(
                onClick = onBackClick,
                enabled = !uiState.isBusy,
                contentPadding =
                    androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 0.dp,
                        vertical = 4.dp
                    )
            ) {
                Text(
                    text = "←  Profilime Dön",
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Telefon Doğrulama",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CustomerHomeColors.DeepOlive
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = if (uiState.isCodeSent) {
                    "Telefonunuza gönderilen 6 haneli doğrulama kodunu girin."
                } else {
                    "Telefon numaranızı güvenli şekilde hesabınıza ekleyin veya değiştirin."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = CustomerHomeColors.TextMuted
            )

            Spacer(modifier = Modifier.height(22.dp))

            PhoneSecurityInfoCard(
                isCodeSent = uiState.isCodeSent
            )

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = CustomerHomeColors.Surface,
                shadowElevation = 2.dp,
                border = BorderStroke(
                    width = 1.dp,
                    color = CustomerHomeColors.Outline
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = if (uiState.isCodeSent) {
                            "Doğrulama Kodu"
                        } else {
                            "Telefon Numaranız"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CustomerHomeColors.DeepOlive
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (uiState.isCodeSent) {
                            "Kod gönderilen numara"
                        } else {
                            "Türkiye mobil numarası girin"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = CustomerHomeColors.TextMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = onPhoneChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Telefon Numarası")
                        },
                        placeholder = {
                            Text("0555 123 45 67")
                        },
                        singleLine = true,
                        enabled =
                            !uiState.isBusy &&
                                    !uiState.isCodeSent,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction =
                                if (uiState.isCodeSent) {
                                    ImeAction.Next
                                } else {
                                    ImeAction.Done
                                }
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (uiState.canRequestCode) {
                                    focusManager.clearFocus()
                                    onRequestCodeClick()
                                }
                            }
                        )
                    )

                    if (uiState.isCodeSent) {
                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = onEditPhoneClick,
                            enabled = !uiState.isBusy,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "Numarayı Değiştir",
                                color = CustomerHomeColors.Terracotta,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = uiState.code,
                            onValueChange = onCodeChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("6 Haneli Kod")
                            },
                            placeholder = {
                                Text("123456")
                            },
                            singleLine = true,
                            enabled = !uiState.isBusy,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType =
                                    KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (uiState.canVerify) {
                                        focusManager.clearFocus()
                                        onVerifyClick()
                                    }
                                }
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text =
                                "${uiState.code.length}/6 hane",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomerHomeColors.TextMuted,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            if (!uiState.message.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                PhoneVerificationMessage(
                    message = uiState.message,
                    isError = uiState.isError
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (!uiState.isCodeSent) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onRequestCodeClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = uiState.canRequestCode,
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            CustomerHomeColors.DeepOlive,
                        contentColor = Color.White,
                        disabledContainerColor =
                            CustomerHomeColors.DeepOlive
                                .copy(alpha = 0.35f),
                        disabledContentColor =
                            Color.White.copy(alpha = 0.85f)
                    )
                ) {
                    if (uiState.isRequestingCode) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(21.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Doğrulama Kodu Gönder",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onVerifyClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = uiState.canVerify,
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            CustomerHomeColors.Terracotta,
                        contentColor = Color.White,
                        disabledContainerColor =
                            CustomerHomeColors.Terracotta
                                .copy(alpha = 0.35f),
                        disabledContentColor =
                            Color.White.copy(alpha = 0.85f)
                    )
                ) {
                    if (uiState.isVerifying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(21.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Telefonu Doğrula",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onRequestCodeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled =
                        !uiState.isBusy &&
                                resendSeconds == 0,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color =
                            CustomerHomeColors.DeepOlive
                                .copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text =
                            if (resendSeconds > 0) {
                                "Kodu Tekrar Gönder ($resendSeconds sn)"
                            } else {
                                "Kodu Tekrar Gönder"
                            },
                        color =
                            if (
                                !uiState.isBusy &&
                                resendSeconds == 0
                            ) {
                                CustomerHomeColors.DeepOlive
                            } else {
                                CustomerHomeColors.TextMuted
                            },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text =
                    "Doğrulama kodu 10 dakika geçerlidir. " +
                            "Yeni kod istemek için 60 saniye beklemeniz gerekir.",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = CustomerHomeColors.TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PhoneSecurityInfoCard(
    isCodeSent: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CustomerHomeColors.OliveSoft
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = CustomerHomeColors.DeepOlive
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isCodeSent) "6" else "✓",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text =
                        if (isCodeSent) {
                            "Kod telefonunuza gönderildi"
                        } else {
                            "Güvenli telefon doğrulaması"
                        },
                    style = MaterialTheme.typography.titleSmall,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text =
                        if (isCodeSent) {
                            "Doğrulama tamamlanana kadar numara hesabınıza kaydedilmez."
                        } else {
                            "Numaranız yalnız doğru doğrulama kodundan sonra hesabınıza kaydedilir."
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = CustomerHomeColors.TextMuted
                )
            }
        }
    }
}

@Composable
private fun PhoneVerificationMessage(
    message: String,
    isError: Boolean
) {
    val background =
        if (isError) {
            CustomerHomeColors.TerracottaSoft
        } else {
            CustomerHomeColors.OliveSoft
        }

    val foreground =
        if (isError) {
            CustomerHomeColors.Error
        } else {
            CustomerHomeColors.DeepOlive
        }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = background
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = foreground,
            fontWeight = FontWeight.Medium
        )
    }
}
