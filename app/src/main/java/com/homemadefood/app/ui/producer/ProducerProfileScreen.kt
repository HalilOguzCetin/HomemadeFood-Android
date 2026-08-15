package com.homemadefood.app.ui.producer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.ui.customer.ProducerBusinessImagePicker
import com.homemadefood.app.ui.customer.ProducerBusinessImagePreview
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ProducerProfileScreen(
    uiState: ProducerProfileUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onStartEditingClick: () -> Unit,
    onCancelEditingClick: () -> Unit,

    onBusinessNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onBusinessImageSelected: (String) -> Unit,
    onRemoveSelectedBusinessImage: () -> Unit,

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
    onAvailabilityChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    LaunchedEffect(
        uiState.successMessage
    ) {
        val message =
            uiState.successMessage

        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message = message
            )

            onMessageShown()
        }
    }

    LaunchedEffect(
        uiState.errorMessage,
        uiState.profile
    ) {
        val message =
            uiState.errorMessage

        if (
            !message.isNullOrBlank() &&
            uiState.profile != null
        ) {
            snackbarHostState.showSnackbar(
                message = message
            )

            onMessageShown()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                ProducerProfileLoadingContent(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                )
            }

            uiState.errorMessage != null &&
                    uiState.profile == null -> {

                ProducerProfileErrorContent(
                    message =
                        uiState.errorMessage,

                    onRetryClick =
                        onRetryClick,

                    onBackClick =
                        onBackClick,

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                )
            }

            uiState.profile != null -> {
                ProducerProfileContent(
                    uiState = uiState,

                    profile =
                        uiState.profile,

                    onBackClick =
                        onBackClick,

                    onStartEditingClick =
                        onStartEditingClick,

                    onCancelEditingClick =
                        onCancelEditingClick,

                    onBusinessNameChange =
                        onBusinessNameChange,

                    onDescriptionChange =
                        onDescriptionChange,

                    onBusinessImageSelected =
                        onBusinessImageSelected,

                    onRemoveSelectedBusinessImage =
                        onRemoveSelectedBusinessImage,

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

                    onAvailabilityChange =
                        onAvailabilityChange,

                    onSaveClick =
                        onSaveClick,

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                )
            }

            else -> {
                ProducerProfileErrorContent(
                    message =
                        "Üretici profil bilgisi bulunamadı.",

                    onRetryClick =
                        onRetryClick,

                    onBackClick =
                        onBackClick,

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun ProducerProfileLoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text =
                    "İşletme profiliniz yükleniyor..."
            )
        }
    }
}

@Composable
private fun ProducerProfileErrorContent(
    message: String,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {
        Text(
            text =
                "İşletme profili yüklenemedi",

            style =
                MaterialTheme.typography
                    .titleLarge,

            color =
                MaterialTheme.colorScheme.error
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = message,
            style =
                MaterialTheme.typography
                    .bodyLarge
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tekrar Dene")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Üretici Paneline Dön")
        }
    }
}

@Composable
private fun ProducerProfileContent(
    uiState: ProducerProfileUiState,
    profile: ProducerApplicationStatusResponse,
    onBackClick: () -> Unit,
    onStartEditingClick: () -> Unit,
    onCancelEditingClick: () -> Unit,

    onBusinessNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onBusinessImageSelected: (String) -> Unit,
    onRemoveSelectedBusinessImage: () -> Unit,

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
    onAvailabilityChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            enabled = !uiState.isSaving
        ) {
            Text("← Üretici Paneline Dön")
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "İşletme Profilim",
            style =
                MaterialTheme.typography
                    .headlineMedium
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text =
                if (uiState.isEditing) {
                    "İşletme bilgilerinizi, konumunuzu ve sipariş alma durumunuzu güncelleyin."
                } else {
                    "İşletme, kapasite ve sipariş alma bilgilerinizi yönetin."
                },

            style =
                MaterialTheme.typography
                    .bodyLarge
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        ProducerProfileStatusCard(
            profile = profile
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        ProducerCapacityCard(
            profile = profile
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        if (uiState.isEditing) {
            ProducerProfileEditForm(
                uiState = uiState,

                onBusinessNameChange =
                    onBusinessNameChange,

                onDescriptionChange =
                    onDescriptionChange,

                onBusinessImageSelected =
                    onBusinessImageSelected,

                onRemoveSelectedBusinessImage =
                    onRemoveSelectedBusinessImage,

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

                onAvailabilityChange =
                    onAvailabilityChange,

                onSaveClick =
                    onSaveClick,

                onCancelClick =
                    onCancelEditingClick
            )
        } else {
            ProducerProfileInformationCard(
                profile = profile
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick =
                    onStartEditingClick,

                modifier =
                    Modifier.fillMaxWidth(),

                enabled =
                    uiState.canEdit
            ) {
                Text("Profili Düzenle")
            }

            if (!uiState.canEdit) {
                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text =
                        "Profil düzenleme işlemi yalnızca onaylanmış üreticiler için kullanılabilir.",

                    color =
                        MaterialTheme.colorScheme.error,

                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )
    }
}

@Composable
private fun ProducerProfileStatusCard(
    profile: ProducerApplicationStatusResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Profil Durumu",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ProducerProfileInfoLine(
                title = "Onay durumu",
                value =
                    producerApprovalStatusText(
                        profile
                    )
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            ProducerProfileInfoLine(
                title = "Sipariş alma durumu",

                value =
                    if (profile.isAvailable) {
                        "Sipariş Almaya Açık"
                    } else {
                        "Sipariş Almaya Kapalı"
                    }
            )

            if (profile.approvedAt != null) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                ProducerProfileInfoLine(
                    title = "Onay tarihi",

                    value =
                        formatProducerProfileDate(
                            profile.approvedAt
                        )
                )
            }
        }
    }
}

@Composable
private fun ProducerCapacityCard(
    profile: ProducerApplicationStatusResponse
) {
    val usedCapacity =
        (
                profile.dailyCapacity -
                        profile.remainingCapacity
                )
            .coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Günlük Kapasite",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ProducerProfileInfoLine(
                title = "Toplam kapasite",
                value =
                    profile.dailyCapacity
                        .toString()
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            ProducerProfileInfoLine(
                title = "Kalan kapasite",
                value =
                    profile.remainingCapacity
                        .toString()
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            ProducerProfileInfoLine(
                title = "Kullanılan kapasite",
                value =
                    usedCapacity.toString()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    "Kapasiteyi değiştirdiğinizde, bugün daha önce kullanılan miktar korunur.",

                style =
                    MaterialTheme.typography
                        .bodySmall
            )
        }
    }
}

@Composable
private fun ProducerProfileInformationCard(
    profile: ProducerApplicationStatusResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "İşletme Bilgileri",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            ProducerBusinessImagePreview(
                businessImageUrl =
                    profile.businessImageUrl
            )

            if (
                !profile.businessImageUrl
                    .isNullOrBlank()
            ) {
                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                HorizontalDivider()

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )
            }

            ProducerProfileInformationSection(
                title = "İşletme adı",
                value = profile.businessName
            )

            HorizontalDivider()

            ProducerProfileInformationSection(
                title = "Açıklama",
                value = profile.description
            )

            HorizontalDivider()

            ProducerProfileInformationSection(
                title = "Adres",
                value = profile.address
            )
        }
    }
}

@Composable
private fun ProducerProfileInformationSection(
    title: String,
    value: String
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 12.dp
                )
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography
                    .labelLarge
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text =
                value.ifBlank {
                    "-"
                },

            style =
                MaterialTheme.typography
                    .bodyLarge
        )
    }
}

@Composable
private fun ProducerProfileInfoLine(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography
                    .bodySmall
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography
                    .titleMedium
        )
    }
}

@Composable
private fun ProducerProfileEditForm(
    uiState: ProducerProfileUiState,
    onBusinessNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onBusinessImageSelected: (String) -> Unit,
    onRemoveSelectedBusinessImage: () -> Unit,

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
    onAvailabilityChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Profili Düzenle",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = uiState.businessName,
                onValueChange =
                    onBusinessNameChange,

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("İşletme Adı")
                },

                supportingText = {
                    Text(
                        "${uiState.businessName.length}/150"
                    )
                },

                singleLine = true,
                enabled = !uiState.isSaving
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange =
                    onDescriptionChange,

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("İşletme Açıklaması")
                },

                supportingText = {
                    Text(
                        "${uiState.description.length}/1000"
                    )
                },

                minLines = 4,
                maxLines = 8,
                enabled = !uiState.isSaving
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            ProducerBusinessImagePicker(
                selectedImageUri =
                    uiState.selectedBusinessImageUri,

                existingImageUrl =
                    uiState.profile
                        ?.businessImageUrl,

                isSubmitting =
                    uiState.isSaving,

                onImageSelected =
                    onBusinessImageSelected,

                onRemoveSelectedImage =
                    onRemoveSelectedBusinessImage,

                isRequired = false
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "İşletme Konumu",
                style =
                    MaterialTheme.typography
                        .titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier =
                        Modifier.padding(14.dp)
                ) {
                    Text(
                        text =
                            if (
                                uiState.selectedLocation !=
                                null
                            ) {
                                "Kayıtlı işletme konumu hazır."
                            } else {
                                "İşletme konumu bulunamadı."
                            },

                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )

                    if (
                        !uiState.locationLookupMessage
                            .isNullOrBlank()
                    ) {
                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                uiState.locationLookupMessage,

                            style =
                                MaterialTheme.typography
                                    .bodySmall
                        )
                    }

                    if (uiState.isResolvingAddress) {
                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )

                        CircularProgressIndicator()
                    }

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    OutlinedButton(
                        onClick =
                            onSelectLocationClick,

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            !uiState.isSaving &&
                                    !uiState.isResolvingAddress
                    ) {
                        Text(
                            "İşletme Konumunu Değiştir"
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "İşletme Adresi",
                style =
                    MaterialTheme.typography
                        .titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = uiState.city,
                onValueChange = onCityChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("İl *") },
                singleLine = true,
                enabled =
                    !uiState.isSaving &&
                            !uiState.isResolvingAddress
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.district,
                onValueChange = onDistrictChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("İlçe *") },
                singleLine = true,
                enabled =
                    !uiState.isSaving &&
                            !uiState.isResolvingAddress
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.neighborhood,
                onValueChange =
                    onNeighborhoodChange,

                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mahalle *") },
                singleLine = true,
                enabled =
                    !uiState.isSaving &&
                            !uiState.isResolvingAddress
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.street,
                onValueChange = onStreetChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Cadde / Sokak *")
                },
                singleLine = true,
                enabled =
                    !uiState.isSaving &&
                            !uiState.isResolvingAddress
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value =
                        uiState.buildingNo,

                    onValueChange =
                        onBuildingNoChange,

                    modifier =
                        Modifier.weight(1f),

                    label = {
                        Text("Bina No *")
                    },

                    singleLine = true,

                    enabled =
                        !uiState.isSaving &&
                                !uiState.isResolvingAddress
                )

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
                    enabled = !uiState.isSaving
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.apartmentNo,
                onValueChange =
                    onApartmentNoChange,

                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Daire / İş Yeri No")
                },
                singleLine = true,
                enabled = !uiState.isSaving
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.addressNote,
                onValueChange =
                    onAddressNoteChange,

                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Adres Tarifi")
                },
                placeholder = {
                    Text(
                        "Örn: Belediye binasının karşısı"
                    )
                },
                minLines = 2,
                maxLines = 4,
                enabled = !uiState.isSaving
            )

            if (uiState.fullAddress.isNotBlank()) {
                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier =
                            Modifier.padding(14.dp)
                    ) {
                        Text(
                            text =
                                "Kaydedilecek İşletme Adresi",

                            style =
                                MaterialTheme.typography
                                    .labelLarge
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                uiState.fullAddress,

                            style =
                                MaterialTheme.typography
                                    .bodyMedium
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            OutlinedTextField(
                value =
                    uiState.dailyCapacityText,

                onValueChange =
                    onDailyCapacityChange,

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Günlük Kapasite")
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),

                supportingText = {
                    Text(
                        "1 ile 1000 arasında bir değer girin."
                    )
                },

                singleLine = true,
                enabled = !uiState.isSaving
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        Text(
                            text =
                                "Sipariş Almaya Açık",

                            style =
                                MaterialTheme.typography
                                    .titleMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                if (uiState.isAvailable) {
                                    "Yeni sipariş kabul edebilirsiniz."
                                } else {
                                    "Yeni sipariş kabul etmiyorsunuz."
                                },

                            style =
                                MaterialTheme.typography
                                    .bodySmall
                        )
                    }

                    Switch(
                        checked =
                            uiState.isAvailable,

                        onCheckedChange =
                            onAvailabilityChange,

                        enabled =
                            !uiState.isSaving
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = onSaveClick,
                modifier =
                    Modifier.fillMaxWidth(),

                enabled =
                    uiState.canSave
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.height(22.dp),

                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Değişiklikleri Kaydet"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedButton(
                onClick = onCancelClick,
                modifier =
                    Modifier.fillMaxWidth(),

                enabled =
                    !uiState.isSaving
            ) {
                Text("Vazgeç")
            }
        }
    }
}

private fun producerApprovalStatusText(
    profile: ProducerApplicationStatusResponse
): String {
    return when {
        profile.isApproved -> {
            "Onaylandı"
        }

        profile.verificationStatus
            .trim()
            .equals(
                "Rejected",
                ignoreCase = true
            ) -> {
            "Reddedildi"
        }

        else -> {
            "Onay Bekliyor"
        }
    }
}

private fun formatProducerProfileDate(
    dateText: String
): String {
    if (dateText.isBlank()) {
        return "-"
    }

    val outputFormatter =
        DateTimeFormatter.ofPattern(
            "dd.MM.yyyy HH:mm"
        )

    return runCatching {
        OffsetDateTime
            .parse(dateText)
            .format(outputFormatter)
    }.recoverCatching {
        LocalDateTime
            .parse(dateText)
            .format(outputFormatter)
    }.getOrElse {
        dateText
    }
}