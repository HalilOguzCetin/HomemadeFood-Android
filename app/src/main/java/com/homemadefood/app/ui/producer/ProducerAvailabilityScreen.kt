package com.homemadefood.app.ui.producer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.ProducerAvailabilityMode
import com.homemadefood.app.data.model.ProducerBusinessHourResponse
import com.homemadefood.app.data.model.UpdateProducerBusinessHourRequest
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private data class ProducerBusinessHourDraft(
    val dayOfWeek: Int,
    val dayName: String,
    val isClosed: Boolean,
    val openTime: String,
    val closeTime: String
)

@Composable
fun ProducerAvailabilityScreen(
    uiState: ProducerAvailabilityUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onModeChange: (ProducerAvailabilityMode) -> Unit,
    onSaveBusinessHours: (
        List<UpdateProducerBusinessHourRequest>
    ) -> Unit,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    var drafts by remember {
        mutableStateOf(
            defaultBusinessHourDrafts()
        )
    }

    LaunchedEffect(
        uiState.availability?.businessHours
    ) {
        val businessHours =
            uiState.availability
                ?.businessHours
                .orEmpty()

        if (businessHours.isNotEmpty()) {
            drafts =
                mergeBusinessHours(
                    businessHours
                )
        }
    }

    LaunchedEffect(
        uiState.successMessage,
        uiState.errorMessage
    ) {
        val message =
            uiState.successMessage
                ?: uiState.errorMessage

        if (!message.isNullOrBlank()) {
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
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.Center
                ) {
                    CircularProgressIndicator()

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            "Çalışma saatleri yükleniyor..."
                    )
                }
            }

            uiState.availability == null -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(20.dp),

                    verticalArrangement =
                        Arrangement.Center,

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    Text(
                        text =
                            uiState.errorMessage
                                ?: "Çalışma bilgileri alınamadı.",

                        style =
                            MaterialTheme.typography
                                .bodyLarge
                    )

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    Button(
                        onClick = onRetryClick
                    ) {
                        Text("Tekrar Dene")
                    }

                    TextButton(
                        onClick = onBackClick
                    ) {
                        Text("Geri Dön")
                    }
                }
            }

            else -> {
                val availability =
                    uiState.availability

                val selectedMode =
                    availability.mode

                val scheduleIsValid =
                    drafts.all {
                        it.isClosed ||
                                (
                                        isValidTime(it.openTime) &&
                                                isValidTime(it.closeTime) &&
                                                it.openTime != it.closeTime
                                        )
                    }

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 12.dp,
                                bottom = 28.dp
                            )
                ) {
                    TextButton(
                        onClick = onBackClick,
                        enabled = !uiState.isBusy
                    ) {
                        Text("← Üretici Paneline Dön")
                    }

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "İşletme Çalışma Saatleri",

                        style =
                            MaterialTheme.typography
                                .headlineMedium,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "İşletmenizin otomatik çalışma saatlerini ve geçici açık/kapalı durumunu yönetin.",

                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    CurrentAvailabilityCard(
                        isCurrentlyOpen =
                            availability
                                .isCurrentlyOpen,

                        availabilityMode =
                            availability
                                .availabilityMode
                    )

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    Text(
                        text =
                            "Çalışma Modu",

                        style =
                            MaterialTheme.typography
                                .titleLarge,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Otomatik mod çalışma saatlerinizi kullanır. Şimdi Açık veya Şimdi Kapalı seçimleri programı geçici olarak geçersiz kılar.",

                        style =
                            MaterialTheme.typography
                                .bodySmall
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        ProducerAvailabilityMode
                            .entries
                            .forEach { mode ->

                                FilterChip(
                                    selected =
                                        selectedMode ==
                                                mode,

                                    onClick = {
                                        onModeChange(
                                            mode
                                        )
                                    },

                                    enabled =
                                        !uiState
                                            .isUpdatingMode,

                                    label = {
                                        Column {
                                            Text(
                                                text =
                                                    mode.displayName,

                                                fontWeight =
                                                    FontWeight
                                                        .SemiBold
                                            )

                                            Text(
                                                text =
                                                    modeDescription(
                                                        mode
                                                    ),

                                                style =
                                                    MaterialTheme
                                                        .typography
                                                        .bodySmall
                                            )
                                        }
                                    },

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                )
                            }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    Text(
                        text =
                            "Haftalık Program",

                        style =
                            MaterialTheme.typography
                                .titleLarge,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "Saatleri SS.DD biçiminde girin. Örneğin 09.00 veya 22.30. Gece yarısını aşan çalışma da desteklenir: 18.00 → 02.00.",

                        style =
                            MaterialTheme.typography
                                .bodySmall
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    drafts.forEachIndexed {
                            index,
                            draft ->

                        BusinessHourDraftCard(
                            draft = draft,

                            enabled =
                                !uiState
                                    .isSavingBusinessHours,

                            onClosedChange = {
                                    isClosed ->

                                drafts =
                                    drafts
                                        .toMutableList()
                                        .also {
                                                list ->
                                            list[index] =
                                                draft.copy(
                                                    isClosed =
                                                        isClosed
                                                )
                                        }
                            },

                            onOpenTimeChange = {
                                    value ->

                                if (
                                    value.length <= 5
                                ) {
                                    drafts =
                                        drafts
                                            .toMutableList()
                                            .also {
                                                    list ->
                                                list[index] =
                                                    draft.copy(
                                                        openTime =
                                                            value
                                                    )
                                            }
                                }
                            },

                            onCloseTimeChange = {
                                    value ->

                                if (
                                    value.length <= 5
                                ) {
                                    drafts =
                                        drafts
                                            .toMutableList()
                                            .also {
                                                    list ->
                                                list[index] =
                                                    draft.copy(
                                                        closeTime =
                                                            value
                                                    )
                                            }
                                }
                            }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )
                    }

                    if (!scheduleIsValid) {
                        Text(
                            text =
                                "Açık günlerde geçerli bir açılış ve kapanış saati girin. Aynı saat kullanılamaz.",

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .error,

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )
                    }

                    Button(
                        onClick = {
                            onSaveBusinessHours(
                                drafts.map {
                                        draft ->

                                    UpdateProducerBusinessHourRequest(
                                        dayOfWeek =
                                            draft
                                                .dayOfWeek,

                                        isClosed =
                                            draft
                                                .isClosed,

                                        openTime =
                                            if (
                                                draft.isClosed
                                            ) {
                                                null
                                            } else {
                                                draft
                                                    .openTime
                                                    .trim()
                                                    .replace(
                                                        '.',
                                                        ':'
                                                    )
                                            },

                                        closeTime =
                                            if (
                                                draft.isClosed
                                            ) {
                                                null
                                            } else {
                                                draft
                                                    .closeTime
                                                    .trim()
                                                    .replace(
                                                        '.',
                                                        ':'
                                                    )
                                            }
                                    )
                                }
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            scheduleIsValid &&
                                    !uiState
                                        .isSavingBusinessHours
                    ) {
                        if (
                            uiState
                                .isSavingBusinessHours
                        ) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.height(
                                        20.dp
                                    )
                            )
                        } else {
                            Text(
                                "Çalışma Saatlerini Kaydet"
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    OutlinedButton(
                        onClick = onRetryClick,
                        modifier =
                            Modifier.fillMaxWidth(),
                        enabled = !uiState.isBusy
                    ) {
                        Text(
                            "Sunucudan Yeniden Yükle"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentAvailabilityCard(
    isCurrentlyOpen: Boolean,
    availabilityMode: String
) {
    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            Text(
                text =
                    if (isCurrentlyOpen) {
                        "Şu Anda Açık"
                    } else {
                        "Şu Anda Kapalı"
                    },

                style =
                    MaterialTheme.typography
                        .titleLarge,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    "Aktif mod: " +
                            (
                                    ProducerAvailabilityMode
                                        .fromBackendValue(
                                            availabilityMode
                                        )
                                        ?.displayName
                                        ?: availabilityMode
                                    ),

                style =
                    MaterialTheme.typography
                        .bodyMedium
            )
        }
    }
}

@Composable
private fun BusinessHourDraftCard(
    draft: ProducerBusinessHourDraft,
    enabled: Boolean,
    onClosedChange: (Boolean) -> Unit,
    onOpenTimeChange: (String) -> Unit,
    onCloseTimeChange: (String) -> Unit
) {
    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Column(
            modifier =
                Modifier.padding(14.dp)
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text =
                        draft.dayName,

                    style =
                        MaterialTheme.typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text =
                            if (draft.isClosed) {
                                "Kapalı"
                            } else {
                                "Açık"
                            },

                        style =
                            MaterialTheme.typography
                                .bodySmall
                    )

                    Switch(
                        checked =
                            !draft.isClosed,

                        onCheckedChange = {
                                isOpen ->
                            onClosedChange(
                                !isOpen
                            )
                        },

                        enabled = enabled
                    )
                }
            }

            if (!draft.isClosed) {
                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {
                    OutlinedTextField(
                        value =
                            TextFieldValue(
                                text =
                                    draft.openTime,

                                selection =
                                    TextRange(
                                        draft.openTime
                                            .length
                                    )
                            ),

                        onValueChange = { value ->
                            onOpenTimeChange(
                                autoFormatTimeInput(
                                    newValue =
                                        value.text,

                                    previousValue =
                                        draft.openTime
                                )
                            )
                        },

                        modifier =
                            Modifier.weight(1f),

                        label = {
                            Text("Açılış")
                        },

                        placeholder = {
                            Text("09.00")
                        },

                        singleLine = true,

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            ),

                        enabled = enabled,

                        isError =
                            draft.openTime
                                .isNotBlank() &&
                                    !isValidTime(
                                        draft.openTime
                                    )
                    )

                    OutlinedTextField(
                        value =
                            TextFieldValue(
                                text =
                                    draft.closeTime,

                                selection =
                                    TextRange(
                                        draft.closeTime
                                            .length
                                    )
                            ),

                        onValueChange = { value ->
                            onCloseTimeChange(
                                autoFormatTimeInput(
                                    newValue =
                                        value.text,

                                    previousValue =
                                        draft.closeTime
                                )
                            )
                        },

                        modifier =
                            Modifier.weight(1f),

                        label = {
                            Text("Kapanış")
                        },

                        placeholder = {
                            Text("22.00")
                        },

                        singleLine = true,

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            ),

                        enabled = enabled,

                        isError =
                            draft.closeTime
                                .isNotBlank() &&
                                    !isValidTime(
                                        draft.closeTime
                                    )
                    )
                }
            }
        }
    }
}

private fun defaultBusinessHourDrafts():
        List<ProducerBusinessHourDraft> {

    return listOf(
        ProducerBusinessHourDraft(
            1,
            "Pazartesi",
            false,
            "09.00",
            "22.00"
        ),

        ProducerBusinessHourDraft(
            2,
            "Salı",
            false,
            "09.00",
            "22.00"
        ),

        ProducerBusinessHourDraft(
            3,
            "Çarşamba",
            false,
            "09.00",
            "22.00"
        ),

        ProducerBusinessHourDraft(
            4,
            "Perşembe",
            false,
            "09.00",
            "22.00"
        ),

        ProducerBusinessHourDraft(
            5,
            "Cuma",
            false,
            "09.00",
            "22.00"
        ),

        ProducerBusinessHourDraft(
            6,
            "Cumartesi",
            false,
            "10.00",
            "22.00"
        ),

        ProducerBusinessHourDraft(
            7,
            "Pazar",
            true,
            "",
            ""
        )
    )
}

private fun mergeBusinessHours(
    businessHours:
    List<ProducerBusinessHourResponse>
): List<ProducerBusinessHourDraft> {

    val byDay =
        businessHours
            .associateBy {
                it.dayOfWeek
            }

    return defaultBusinessHourDrafts()
        .map { default ->
            val current =
                byDay[
                    default.dayOfWeek
                ]

            if (current == null) {
                default
            } else {
                default.copy(
                    dayName =
                        current.dayName
                            .ifBlank {
                                default.dayName
                            },

                    isClosed =
                        current.isClosed,

                    openTime =
                        current.openTime
                            .orEmpty()
                            .replace(
                                ':',
                                '.'
                            ),

                    closeTime =
                        current.closeTime
                            .orEmpty()
                            .replace(
                                ':',
                                '.'
                            )
                )
            }
        }
}

private fun autoFormatTimeInput(
    newValue: String,
    previousValue: String
): String {

    /*
     * Arayüzde saat biçimi SS.DD şeklindedir.
     * Kullanıcı yalnızca rakam girer; ayırıcı nokta
     * ilk iki rakamdan sonra otomatik eklenir.
     */
    if (
        previousValue.length == 3 &&
        previousValue[2] == '.' &&
        newValue == previousValue.dropLast(1)
    ) {
        return previousValue
            .take(1)
    }

    val digits =
        newValue
            .filter {
                it.isDigit()
            }
            .take(4)

    return when {
        digits.isEmpty() ->
            ""

        digits.length == 1 ->
            digits

        digits.length == 2 ->
            "${digits}."

        else ->
            "${digits.take(2)}.${digits.drop(2)}"
    }
}

private fun isValidTime(
    value: String
): Boolean {

    if (value.length != 5) {
        return false
    }

    return try {
        LocalTime.parse(
            value.replace(
                '.',
                ':'
            ),

            DateTimeFormatter.ofPattern(
                "HH:mm"
            )
        )

        true
    } catch (_: DateTimeParseException) {
        false
    }
}

private fun modeDescription(
    mode: ProducerAvailabilityMode
): String {

    return when (mode) {
        ProducerAvailabilityMode.SCHEDULED ->
            "Çalışma saatlerine göre otomatik açılır ve kapanır."

        ProducerAvailabilityMode.FORCE_OPEN ->
            "Çalışma programından bağımsız olarak şimdi açık kabul edilir."

        ProducerAvailabilityMode.FORCE_CLOSED ->
            "Çalışma programından bağımsız olarak şimdi kapalı kabul edilir."
    }
}