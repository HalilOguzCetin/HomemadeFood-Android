package com.homemadefood.app.ui.customer

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.ProducerApplicationStatus
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CustomerProducerApplicationScreen(
    uiState: CustomerProducerApplicationUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onBusinessNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onBusinessImageSelected: (String) -> Unit,
    onRemoveBusinessImage: () -> Unit,
    onCityChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onNeighborhoodChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onBuildingNoChange: (String) -> Unit,
    onFloorChange: (String) -> Unit,
    onApartmentNoChange: (String) -> Unit,
    onAddressNoteChange: (String) -> Unit,
    onSelectLocationClick: () -> Unit,
    onDailyCapacityChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onShowReapplicationFormClick: () -> Unit,
    onHideReapplicationFormClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        when {
            uiState.isLoading -> {
                ProducerApplicationLoading(
                    onBackClick = onBackClick,
                    modifier = modifier
                )
            }

            uiState.application == null &&
                    !uiState.isFormVisible -> {
                ProducerApplicationLoadError(
                    message =
                        uiState.errorMessage
                            ?: "Başvuru bilgisi alınamadı.",
                    onBackClick = onBackClick,
                    onRetryClick = onRetryClick,
                    modifier = modifier
                )
            }

            else -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(
                            CustomerHomeColors.Cream
                        )
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .imePadding()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 12.dp,
                            bottom = 30.dp
                        )
                ) {
                    TextButton(
                        onClick = onBackClick,
                        enabled = !uiState.isSubmitting,
                        contentPadding =
                            PaddingValues(
                                horizontal = 0.dp,
                                vertical = 4.dp
                            )
                    ) {
                        Text(
                            text = "←  Hesabıma Dön",
                            color =
                                CustomerHomeColors
                                    .DeepOlive,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    ProducerApplicationHeader(
                        hasApplication =
                            uiState.application != null
                    )

                    if (
                        !uiState.successMessage
                            .isNullOrBlank()
                    ) {
                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        ProducerApplicationMessage(
                            message =
                                uiState.successMessage,
                            isError = false
                        )
                    }

                    if (
                        !uiState.errorMessage
                            .isNullOrBlank()
                    ) {
                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        ProducerApplicationMessage(
                            message =
                                uiState.errorMessage,
                            isError = true
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    if (uiState.isFormVisible) {
                        ProducerApplicationForm(
                            uiState = uiState,
                            onBusinessNameChange =
                                onBusinessNameChange,
                            onDescriptionChange =
                                onDescriptionChange,
                            onBusinessImageSelected =
                                onBusinessImageSelected,
                            onRemoveBusinessImage =
                                onRemoveBusinessImage,
                            onCityChange =
                                onCityChange,
                            onDistrictChange =
                                onDistrictChange,
                            onNeighborhoodChange =
                                onNeighborhoodChange,
                            onStreetChange =
                                onStreetChange,
                            onBuildingNoChange =
                                onBuildingNoChange,
                            onFloorChange =
                                onFloorChange,
                            onApartmentNoChange =
                                onApartmentNoChange,
                            onAddressNoteChange =
                                onAddressNoteChange,
                            onSelectLocationClick =
                                onSelectLocationClick,
                            onDailyCapacityChange =
                                onDailyCapacityChange,
                            onSubmitClick =
                                onSubmitClick,
                            onCancelClick =
                                onHideReapplicationFormClick,
                            showCancelButton =
                                uiState.application != null
                        )
                    } else {
                        uiState.application?.let { application ->
                            ProducerApplicationStatusContent(
                                application = application,
                                onShowReapplicationFormClick =
                                    onShowReapplicationFormClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProducerApplicationHeader(
    hasApplication: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text =
                    if (hasApplication) {
                        "Üretici Başvurum"
                    } else {
                        "Üretici Ol"
                    },
                style =
                    MaterialTheme.typography
                        .headlineMedium,
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    if (hasApplication) {
                        "Başvurunuzun durumunu ve işletme bilgilerinizi buradan takip edin."
                    } else {
                        "Ev yapımı yemeklerinizi müşterilerle buluşturmak için işletme başvurunuzu oluşturun."
                    },
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                color =
                    CustomerHomeColors.TextMuted
            )
        }

        Surface(
            modifier = Modifier.size(46.dp),
            shape = CircleShape,
            color =
                CustomerHomeColors.OliveSoft
        ) {
            Box(
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text = "⌂",
                    style =
                        MaterialTheme.typography
                            .titleLarge,
                    color =
                        CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProducerApplicationForm(
    uiState: CustomerProducerApplicationUiState,
    onBusinessNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onBusinessImageSelected: (String) -> Unit,
    onRemoveBusinessImage: () -> Unit,
    onCityChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onNeighborhoodChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onBuildingNoChange: (String) -> Unit,
    onFloorChange: (String) -> Unit,
    onApartmentNoChange: (String) -> Unit,
    onAddressNoteChange: (String) -> Unit,
    onSelectLocationClick: () -> Unit,
    onDailyCapacityChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onCancelClick: () -> Unit,
    showCancelButton: Boolean
) {
    val formEnabled =
        !uiState.isSubmitting &&
                !uiState.isResolvingAddress

    ProducerApplicationIntroCard(
        isReapplication = showCancelButton
    )

    Spacer(
        modifier = Modifier.height(20.dp)
    )

    ProducerApplicationSectionHeader(
        step = "1",
        title = "İşletmenizi Tanıtın",
        subtitle =
            "Müşterilerin işletmenizi tanıyabilmesi için temel bilgileri ve vitrin görselinizi ekleyin."
    )

    Spacer(
        modifier = Modifier.height(11.dp)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border =
            BorderStroke(
                1.dp,
                CustomerHomeColors.Outline
            )
    ) {
        Column(
            modifier = Modifier.padding(17.dp)
        ) {
            OutlinedTextField(
                value = uiState.businessName,
                onValueChange =
                    onBusinessNameChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("İşletme Adı *")
                },
                supportingText = {
                    Text(
                        "${uiState.businessName.length}/150"
                    )
                },
                singleLine = true,
                enabled = !uiState.isSubmitting,
                shape =
                    RoundedCornerShape(15.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange =
                    onDescriptionChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("İşletme Açıklaması *")
                },
                placeholder = {
                    Text(
                        "Hazırladığınız yemekleri ve işletmenizi kısaca tanıtın."
                    )
                },
                supportingText = {
                    Text(
                        "${uiState.description.length}/1000"
                    )
                },
                minLines = 4,
                maxLines = 7,
                enabled = !uiState.isSubmitting,
                shape =
                    RoundedCornerShape(15.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "İşletme Vitrin Görseli *",
                style =
                    MaterialTheme.typography
                        .titleSmall,
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Bu görsel müşterilerin işletmenizi keşfederken gördüğü ana işletme görselidir.",
                style =
                    MaterialTheme.typography
                        .bodySmall,
                color =
                    CustomerHomeColors.TextMuted
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            ProducerBusinessImagePicker(
                selectedImageUri =
                    uiState.selectedBusinessImageUri,
                existingImageUrl =
                    uiState.existingBusinessImageUrl,
                isSubmitting =
                    uiState.isSubmitting,
                onImageSelected =
                    onBusinessImageSelected,
                onRemoveSelectedImage =
                    onRemoveBusinessImage,
                modifier =
                    Modifier.fillMaxWidth()
            )
        }
    }

    Spacer(
        modifier = Modifier.height(24.dp)
    )

    ProducerApplicationSectionHeader(
        step = "2",
        title = "İşletme Konumu",
        subtitle =
            "İşletmenizin gerçek konumunu haritadan seçin; adres alanları mümkün olduğunca otomatik doldurulur."
    )

    Spacer(
        modifier = Modifier.height(11.dp)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border =
            BorderStroke(
                1.dp,
                CustomerHomeColors.Outline
            )
    ) {
        Column(
            modifier = Modifier.padding(17.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(15.dp),
                color =
                    when {
                        uiState.isResolvingAddress ->
                            CustomerHomeColors
                                .SurfaceSoft

                        uiState.selectedLocation != null ->
                            CustomerHomeColors
                                .OliveSoft

                        else ->
                            CustomerHomeColors
                                .TerracottaSoft
                    }
            ) {
                Row(
                    modifier = Modifier.padding(13.dp),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    if (uiState.isResolvingAddress) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color =
                                CustomerHomeColors
                                    .Terracotta
                        )
                    } else {
                        Text(
                            text =
                                if (
                                    uiState.selectedLocation != null
                                ) {
                                    "✓"
                                } else {
                                    "⌖"
                                },
                            color =
                                CustomerHomeColors
                                    .DeepOlive,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text =
                                when {
                                    uiState.isResolvingAddress ->
                                        "Adres bilgileri hazırlanıyor"

                                    uiState.selectedLocation != null ->
                                        "İşletme konumu seçildi"

                                    else ->
                                        "Henüz işletme konumu seçilmedi"
                                },
                            style =
                                MaterialTheme.typography
                                    .titleSmall,
                            color =
                                CustomerHomeColors
                                    .DeepOlive,
                            fontWeight =
                                FontWeight.SemiBold
                        )

                        if (
                            !uiState.locationLookupMessage
                                .isNullOrBlank()
                        ) {
                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    uiState
                                        .locationLookupMessage,
                                style =
                                    MaterialTheme.typography
                                        .bodySmall,
                                color =
                                    CustomerHomeColors
                                        .TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            OutlinedButton(
                onClick =
                    onSelectLocationClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled =
                    !uiState.isSubmitting &&
                            !uiState.isResolvingAddress,
                shape =
                    RoundedCornerShape(15.dp),
                border =
                    BorderStroke(
                        1.dp,
                        CustomerHomeColors
                            .DeepOlive
                            .copy(alpha = 0.35f)
                    )
            ) {
                Text(
                    text =
                        if (
                            uiState.selectedLocation == null
                        ) {
                            "Haritadan İşletme Konumu Seç"
                        } else {
                            "İşletme Konumunu Değiştir"
                        },
                    color =
                        CustomerHomeColors.DeepOlive,
                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }
    }

    Spacer(
        modifier = Modifier.height(24.dp)
    )

    ProducerApplicationSectionHeader(
        step = "3",
        title = "İşletme Adresi",
        subtitle =
            "Haritadan gelen adresi kontrol edin ve eksik kalan alanları tamamlayın."
    )

    Spacer(
        modifier = Modifier.height(11.dp)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border =
            BorderStroke(
                1.dp,
                CustomerHomeColors.Outline
            )
    ) {
        Column(
            modifier = Modifier.padding(17.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(9.dp)
            ) {
                OutlinedTextField(
                    value = uiState.city,
                    onValueChange = onCityChange,
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("İl *")
                    },
                    singleLine = true,
                    enabled = formEnabled,
                    shape =
                        RoundedCornerShape(15.dp)
                )

                OutlinedTextField(
                    value = uiState.district,
                    onValueChange =
                        onDistrictChange,
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("İlçe *")
                    },
                    singleLine = true,
                    enabled = formEnabled,
                    shape =
                        RoundedCornerShape(15.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.neighborhood,
                onValueChange =
                    onNeighborhoodChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Mahalle *")
                },
                singleLine = true,
                enabled = formEnabled,
                shape =
                    RoundedCornerShape(15.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.street,
                onValueChange =
                    onStreetChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Cadde / Sokak *")
                },
                singleLine = true,
                enabled = formEnabled,
                shape =
                    RoundedCornerShape(15.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.buildingNo,
                onValueChange =
                    onBuildingNoChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Bina No *")
                },
                singleLine = true,
                enabled = formEnabled,
                shape =
                    RoundedCornerShape(15.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(9.dp)
            ) {
                OutlinedTextField(
                    value = uiState.floor,
                    onValueChange =
                        onFloorChange,
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("Kat")
                    },
                    singleLine = true,
                    enabled = !uiState.isSubmitting,
                    shape =
                        RoundedCornerShape(15.dp)
                )

                OutlinedTextField(
                    value = uiState.apartmentNo,
                    onValueChange =
                        onApartmentNoChange,
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("Daire / İş Yeri No")
                    },
                    singleLine = true,
                    enabled = !uiState.isSubmitting,
                    shape =
                        RoundedCornerShape(15.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.addressNote,
                onValueChange =
                    onAddressNoteChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Adres Tarifi")
                },
                placeholder = {
                    Text(
                        "Örn. Belediye binasının karşısı"
                    )
                },
                minLines = 2,
                maxLines = 4,
                enabled = !uiState.isSubmitting,
                shape =
                    RoundedCornerShape(15.dp)
            )
        }
    }

    if (uiState.fullAddress.isNotBlank()) {
        Spacer(
            modifier = Modifier.height(13.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color =
                CustomerHomeColors.OliveSoft
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    text =
                        "Kaydedilecek İşletme Adresi",
                    style =
                        MaterialTheme.typography
                            .titleSmall,
                    color =
                        CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text = uiState.fullAddress,
                    style =
                        MaterialTheme.typography
                            .bodyMedium,
                    color =
                        CustomerHomeColors.Text
                )
            }
        }
    }

    Spacer(
        modifier = Modifier.height(24.dp)
    )

    ProducerApplicationSectionHeader(
        step = "4",
        title = "Üretim Kapasitesi",
        subtitle =
            "Bir günde hazırlayabileceğiniz toplam ürün miktarını belirtin."
    )

    Spacer(
        modifier = Modifier.height(11.dp)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border =
            BorderStroke(
                1.dp,
                CustomerHomeColors.Outline
            )
    ) {
        OutlinedTextField(
            value = uiState.dailyCapacityText,
            onValueChange =
                onDailyCapacityChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            label = {
                Text("Günlük Kapasite *")
            },
            placeholder = {
                Text("Örn. 50")
            },
            supportingText = {
                Text(
                    "1 ile 1000 arasında bir değer girin."
                )
            },
            singleLine = true,
            enabled = !uiState.isSubmitting,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Number
                ),
            shape =
                RoundedCornerShape(15.dp)
        )
    }

    Spacer(
        modifier = Modifier.height(22.dp)
    )

    Button(
        onClick = onSubmitClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = uiState.canSubmit,
        shape = RoundedCornerShape(17.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor =
                    CustomerHomeColors.Terracotta,
                contentColor = Color.White,
                disabledContainerColor =
                    CustomerHomeColors
                        .Terracotta
                        .copy(alpha = 0.36f),
                disabledContentColor =
                    Color.White.copy(alpha = 0.82f)
            )
    ) {
        if (uiState.isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(21.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else {
            Text(
                text =
                    if (showCancelButton) {
                        "Yeniden Başvur"
                    } else {
                        "Başvuruyu Gönder"
                    },
                style =
                    MaterialTheme.typography
                        .titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showCancelButton) {
        Spacer(
            modifier = Modifier.height(9.dp)
        )

        OutlinedButton(
            onClick = onCancelClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !uiState.isSubmitting,
            shape = RoundedCornerShape(15.dp),
            border =
                BorderStroke(
                    1.dp,
                    CustomerHomeColors
                        .DeepOlive
                        .copy(alpha = 0.3f)
                )
        ) {
            Text(
                text = "Formu Kapat",
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProducerApplicationIntroCard(
    isReapplication: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color =
            if (isReapplication) {
                CustomerHomeColors
                    .TerracottaSoft
            } else {
                CustomerHomeColors
                    .DeepOlive
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text =
                    if (isReapplication) {
                        "Başvurunuzu Güncelleyin"
                    } else {
                        "HomemadeFood Üreticisi Olun"
                    },
                style =
                    MaterialTheme.typography
                        .titleMedium,
                color =
                    if (isReapplication) {
                        CustomerHomeColors
                            .Terracotta
                    } else {
                        Color.White
                    },
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    if (isReapplication) {
                        "Önceki bilgileriniz korundu. Red gerekçesini dikkate alarak gerekli alanları düzenleyip yeniden gönderebilirsiniz."
                    } else {
                        "İşletme bilgileriniz yönetici incelemesine gönderilir. Onaydan sonra aynı hesabınızla üretici modunu kullanabilirsiniz."
                    },
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                color =
                    if (isReapplication) {
                        CustomerHomeColors
                            .Text
                    } else {
                        Color.White.copy(
                            alpha = 0.82f
                        )
                    }
            )
        }
    }
}

@Composable
private fun ProducerApplicationSectionHeader(
    step: String,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(11.dp),
        verticalAlignment =
            Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = CircleShape,
            color =
                CustomerHomeColors.DeepOlive
        ) {
            Box(
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text = step,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography
                        .titleLarge,
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                style =
                    MaterialTheme.typography
                        .bodySmall,
                color =
                    CustomerHomeColors.TextMuted
            )
        }
    }
}

@Composable
private fun ProducerApplicationStatusContent(
    application:
    ProducerApplicationStatusResponse,
    onShowReapplicationFormClick:
        () -> Unit
) {
    val status =
        ProducerApplicationStatus
            .fromBackendValue(
                application.verificationStatus
            )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border =
            BorderStroke(
                1.dp,
                CustomerHomeColors.Outline
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (
                !application.businessImageUrl
                    .isNullOrBlank()
            ) {
                ProducerBusinessImagePreview(
                    businessImageUrl =
                        application.businessImageUrl
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )
            }

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text =
                            application.businessName
                                .ifBlank {
                                    "İşletme"
                                },
                        style =
                            MaterialTheme.typography
                                .headlineSmall,
                        color =
                            CustomerHomeColors
                                .DeepOlive,
                        fontWeight =
                            FontWeight.Bold,
                        maxLines = 2,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    if (
                        application.description
                            .isNotBlank()
                    ) {
                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                application.description,
                            style =
                                MaterialTheme.typography
                                    .bodyMedium,
                            color =
                                CustomerHomeColors
                                    .TextMuted
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.size(10.dp)
                )

                ProducerStatusBadge(
                    status = status,
                    rawStatus =
                        application
                            .verificationStatus
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            HorizontalDivider(
                color =
                    CustomerHomeColors.Outline
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ApplicationInformationRow(
                title = "Başvuru tarihi",
                value =
                    formatApplicationDate(
                        application.createdAt
                    )
            )

            if (
                !application.approvedAt
                    .isNullOrBlank()
            ) {
                ApplicationInformationRow(
                    title = "Onay tarihi",
                    value =
                        formatApplicationDate(
                            application.approvedAt!!
                        )
                )
            }

            if (
                !application.rejectedAt
                    .isNullOrBlank()
            ) {
                ApplicationInformationRow(
                    title = "Red tarihi",
                    value =
                        formatApplicationDate(
                            application.rejectedAt!!
                        )
                )
            }

            ApplicationInformationRow(
                title = "Günlük kapasite",
                value =
                    "${application.dailyCapacity} adet"
            )

            ApplicationInformationRow(
                title = "İşletme adresi",
                value =
                    application.address
                        .ifBlank { "-" }
            )
        }
    }

    Spacer(
        modifier = Modifier.height(14.dp)
    )

    when (status) {
        ProducerApplicationStatus.PENDING -> {
            ProducerApplicationStatusMessage(
                title = "Başvurunuz İnceleniyor",
                message =
                    "Başvurunuz yönetici incelemesindedir. Sonuçlandığında başvuru durumunuz burada güncellenecektir.",
                type =
                    ProducerStatusMessageType.Pending
            )
        }

        ProducerApplicationStatus.APPROVED -> {
            ProducerApplicationStatusMessage(
                title = "Başvurunuz Onaylandı",
                message =
                    "Üretici yetkiniz aktif edildi. Artık aynı hesabınız üzerinden üretici moduna geçebilirsiniz.",
                type =
                    ProducerStatusMessageType.Approved
            )
        }

        ProducerApplicationStatus.REJECTED -> {
            ProducerApplicationStatusMessage(
                title = "Başvuru Reddedildi",
                message =
                    application.rejectionReason
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Red nedeni belirtilmedi.",
                type =
                    ProducerStatusMessageType.Rejected
            )

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            Button(
                onClick =
                    onShowReapplicationFormClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape =
                    RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            CustomerHomeColors
                                .Terracotta,
                        contentColor =
                            Color.White
                    )
            ) {
                Text(
                    text =
                        "Bilgileri Düzenleyerek Yeniden Başvur",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        null -> {
            ProducerApplicationStatusMessage(
                title = "Başvuru Durumu",
                message =
                    ProducerApplicationStatus
                        .detailDisplayNameFor(
                            application
                                .verificationStatus
                        ),
                type =
                    ProducerStatusMessageType.Pending
            )
        }
    }
}

@Composable
private fun ProducerStatusBadge(
    status: ProducerApplicationStatus?,
    rawStatus: String
) {
    val background =
        when (status) {
            ProducerApplicationStatus.PENDING ->
                CustomerHomeColors
                    .SurfaceSoft

            ProducerApplicationStatus.APPROVED ->
                CustomerHomeColors
                    .OliveSoft

            ProducerApplicationStatus.REJECTED ->
                CustomerHomeColors
                    .TerracottaSoft

            null ->
                CustomerHomeColors
                    .SurfaceSoft
        }

    val foreground =
        when (status) {
            ProducerApplicationStatus.PENDING ->
                CustomerHomeColors
                    .TextMuted

            ProducerApplicationStatus.APPROVED ->
                CustomerHomeColors
                    .DeepOlive

            ProducerApplicationStatus.REJECTED ->
                CustomerHomeColors
                    .Terracotta

            null ->
                CustomerHomeColors
                    .TextMuted
        }

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = background
    ) {
        Text(
            text =
                ProducerApplicationStatus
                    .detailDisplayNameFor(
                        rawStatus
                    ),
            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                ),
            style =
                MaterialTheme.typography
                    .labelLarge,
            color = foreground,
            fontWeight = FontWeight.Bold
        )
    }
}

private enum class ProducerStatusMessageType {
    Pending,
    Approved,
    Rejected
}

@Composable
private fun ProducerApplicationStatusMessage(
    title: String,
    message: String,
    type: ProducerStatusMessageType
) {
    val containerColor =
        when (type) {
            ProducerStatusMessageType.Pending ->
                CustomerHomeColors
                    .SurfaceSoft

            ProducerStatusMessageType.Approved ->
                CustomerHomeColors
                    .OliveSoft

            ProducerStatusMessageType.Rejected ->
                CustomerHomeColors
                    .TerracottaSoft
        }

    val titleColor =
        when (type) {
            ProducerStatusMessageType.Pending ->
                CustomerHomeColors
                    .DeepOlive

            ProducerStatusMessageType.Approved ->
                CustomerHomeColors
                    .DeepOlive

            ProducerStatusMessageType.Rejected ->
                CustomerHomeColors
                    .Terracotta
        }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography
                        .titleMedium,
                color = titleColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = message,
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                color =
                    CustomerHomeColors.Text
            )
        }
    }
}

@Composable
private fun ApplicationInformationRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.Top
    ) {
        Text(
            text = title,
            modifier =
                Modifier.weight(0.42f),
            style =
                MaterialTheme.typography
                    .bodySmall,
            color =
                CustomerHomeColors.TextMuted
        )

        Text(
            text = value,
            modifier =
                Modifier.weight(0.58f),
            style =
                MaterialTheme.typography
                    .bodyMedium,
            color =
                CustomerHomeColors.Text,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}

@Composable
private fun ProducerApplicationMessage(
    message: String,
    isError: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color =
            if (isError) {
                CustomerHomeColors
                    .TerracottaSoft
            } else {
                CustomerHomeColors
                    .OliveSoft
            }
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
            style =
                MaterialTheme.typography
                    .bodyMedium,
            color =
                if (isError) {
                    CustomerHomeColors.Error
                } else {
                    CustomerHomeColors.DeepOlive
                },
            fontWeight =
                FontWeight.Medium
        )
    }
}

@Composable
private fun ProducerApplicationLoading(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomerHomeColors.Cream)
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            contentPadding =
                PaddingValues(
                    horizontal = 0.dp,
                    vertical = 4.dp
                )
        ) {
            Text(
                text = "←  Hesabıma Dön",
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight =
                    FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment =
                Alignment.Center
        ) {
            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color =
                        CustomerHomeColors
                            .Terracotta
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Üretici başvurunuz yükleniyor...",
                    color =
                        CustomerHomeColors
                            .TextMuted,
                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ProducerApplicationLoadError(
    message: String,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomerHomeColors.Cream)
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            contentPadding =
                PaddingValues(
                    horizontal = 0.dp,
                    vertical = 4.dp
                )
        ) {
            Text(
                text = "←  Hesabıma Dön",
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight =
                    FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment =
                Alignment.Center
        ) {
            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(22.dp),
                color =
                    CustomerHomeColors.Surface,
                border =
                    BorderStroke(
                        1.dp,
                        CustomerHomeColors
                            .Outline
                    )
            ) {
                Column(
                    modifier =
                        Modifier.padding(20.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    Text(
                        text =
                            "Başvuru Bilgisi Alınamadı",
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        color =
                            CustomerHomeColors
                                .DeepOlive,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text = message,
                        style =
                            MaterialTheme.typography
                                .bodyMedium,
                        color =
                            CustomerHomeColors
                                .TextMuted
                    )

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    Button(
                        onClick = onRetryClick,
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(15.dp),
                        colors =
                            ButtonDefaults
                                .buttonColors(
                                    containerColor =
                                        CustomerHomeColors
                                            .DeepOlive,
                                    contentColor =
                                        Color.White
                                )
                    ) {
                        Text(
                            text = "Tekrar Dene",
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun formatApplicationDate(
    value: String
): String {
    if (value.isBlank()) {
        return "-"
    }

    val formatter =
        DateTimeFormatter.ofPattern(
            "dd.MM.yyyy HH:mm",
            Locale("tr", "TR")
        )

    return runCatching {
        OffsetDateTime
            .parse(value)
            .format(formatter)
    }.recoverCatching {
        LocalDateTime
            .parse(value)
            .format(formatter)
    }.getOrElse {
        value
    }
}