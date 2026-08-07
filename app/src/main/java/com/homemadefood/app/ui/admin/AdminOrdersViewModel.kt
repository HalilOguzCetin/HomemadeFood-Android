package com.homemadefood.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AdminRepository
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeParseException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

class AdminOrdersViewModel(
    private val adminRepository:
    AdminRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadOrdersJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            AdminOrdersUiState()
        )

    val uiState:
            StateFlow<AdminOrdersUiState> =
        _uiState.asStateFlow()

    fun loadOrders() {
        val currentState =
            _uiState.value

        val customerId =
            parsePositiveId(
                value =
                    currentState.customerIdInput,

                fieldName =
                    "Müşteri ID"
            )

        if (customerId == INVALID_ID) {
            return
        }

        val producerProfileId =
            parsePositiveId(
                value =
                    currentState
                        .producerProfileIdInput,

                fieldName =
                    "Üretici profil ID"
            )

        if (
            producerProfileId ==
            INVALID_ID
        ) {
            return
        }

        if (
            !validateDateRange(
                dateFrom =
                    currentState.dateFromInput,

                dateTo =
                    currentState.dateToInput
            )
        ) {
            return
        }

        loadOrdersJob?.cancel()

        loadOrdersJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    showError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                val state =
                    _uiState.value

                try {
                    val response =
                        adminRepository.getOrders(


                            status =
                                state.selectedStatus
                                    .backendValue,

                            customerId =
                                customerId,

                            producerProfileId =
                                producerProfileId,

                            search =
                                state.searchQuery
                                    .trim()
                                    .takeIf {
                                        it.isNotBlank()
                                    },

                            dateFrom =
                                state.dateFromInput
                                    .trim()
                                    .takeIf {
                                        it.isNotBlank()
                                    },

                            dateTo =
                                state.dateToInput
                                    .trim()
                                    .takeIf {
                                        it.isNotBlank()
                                    }
                        )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,

                                orders =
                                    responseBody.data
                                        .orEmpty()
                                        .sortedByDescending {
                                                order ->

                                            order.createdAt
                                        },

                                errorMessage = null
                            )
                    } else {
                        showError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Siparişler alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Siparişler yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun selectStatus(
        status: AdminOrderStatusFilter
    ) {
        if (
            _uiState.value.selectedStatus ==
            status
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedStatus = status
            )

        loadOrders()
    }

    fun updateSearchQuery(
        value: String
    ) {
        if (value.length > 100) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                searchQuery = value,
                errorMessage = null
            )
    }

    fun updateCustomerIdInput(
        value: String
    ) {
        if (
            value.isNotEmpty() &&
            !value.all { character ->
                character.isDigit()
            }
        ) {
            return
        }

        if (value.length > 10) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                customerIdInput = value,
                errorMessage = null
            )
    }

    fun updateProducerProfileIdInput(
        value: String
    ) {
        if (
            value.isNotEmpty() &&
            !value.all { character ->
                character.isDigit()
            }
        ) {
            return
        }

        if (value.length > 10) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                producerProfileIdInput =
                    value,

                errorMessage = null
            )
    }

    fun updateDateFromInput(
        value: String
    ) {
        if (
            value.length > 10 ||
            !isDateInputAllowed(value)
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                dateFromInput = value,
                errorMessage = null
            )
    }

    fun updateDateToInput(
        value: String
    ) {
        if (
            value.length > 10 ||
            !isDateInputAllowed(value)
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                dateToInput = value,
                errorMessage = null
            )
    }

    fun applyFilters() {
        loadOrders()
    }

    fun clearFilters() {
        _uiState.value =
            _uiState.value.copy(
                selectedStatus =
                    AdminOrderStatusFilter.ALL,

                searchQuery = "",
                customerIdInput = "",
                producerProfileIdInput = "",
                dateFromInput = "",
                dateToInput = "",
                errorMessage = null
            )

        loadOrders()
    }

    private fun parsePositiveId(
        value: String,
        fieldName: String
    ): Int? {
        if (value.isBlank()) {
            return null
        }

        val parsedValue =
            value.toIntOrNull()

        if (
            parsedValue == null ||
            parsedValue <= 0
        ) {
            showValidationError(
                "$fieldName değeri sıfırdan büyük olmalıdır."
            )

            return INVALID_ID
        }

        return parsedValue
    }

    private fun validateDateRange(
        dateFrom: String,
        dateTo: String
    ): Boolean {
        val normalizedDateFrom =
            dateFrom.trim()

        val normalizedDateTo =
            dateTo.trim()

        val parsedDateFrom =
            parseDateOrNull(
                value =
                    normalizedDateFrom,

                fieldName =
                    "Başlangıç tarihi"
            ) ?: if (
                normalizedDateFrom.isBlank()
            ) {
                null
            } else {
                return false
            }

        val parsedDateTo =
            parseDateOrNull(
                value =
                    normalizedDateTo,

                fieldName =
                    "Bitiş tarihi"
            ) ?: if (
                normalizedDateTo.isBlank()
            ) {
                null
            } else {
                return false
            }

        if (
            parsedDateFrom != null &&
            parsedDateTo != null &&
            parsedDateFrom >
            parsedDateTo
        ) {
            showValidationError(
                "Başlangıç tarihi bitiş tarihinden sonra olamaz."
            )

            return false
        }

        return true
    }

    private fun parseDateOrNull(
        value: String,
        fieldName: String
    ): LocalDate? {
        if (value.isBlank()) {
            return null
        }

        if (value.length != 10) {
            showValidationError(
                "$fieldName yyyy-MM-dd biçiminde olmalıdır."
            )

            return null
        }

        return try {
            LocalDate.parse(value)
        } catch (_: DateTimeParseException) {
            showValidationError(
                "$fieldName geçerli bir tarih olmalıdır."
            )

            null
        }
    }

    private fun isDateInputAllowed(
        value: String
    ): Boolean {
        return value.all { character ->
            character.isDigit() ||
                    character == '-'
        }
    }

    private fun showValidationError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                errorMessage = message
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                orders = emptyList(),
                errorMessage = message
            )
    }

    private fun parseErrorMessage(
        errorJson: String?
    ): String? {
        if (errorJson.isNullOrBlank()) {
            return null
        }

        return runCatching {
            JSONObject(errorJson)
                .optString("message")
                .takeIf { message ->
                    message.isNotBlank()
                }
        }.getOrNull()
    }

    private companion object {
        /*
         * Boş ID ile geçersiz ID değerini birbirinden
         * ayırmak için kullanılan özel işaret.
         */
        const val INVALID_ID =
            Int.MIN_VALUE
    }
}