package com.homemadefood.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.AdminUserListItemResponse
import com.homemadefood.app.ui.components.AppEmptyState
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppLoadingState
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminUsersScreen(
    uiState: AdminUsersUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onRoleFilterSelected:
        (AdminUserRoleFilter) -> Unit,
    onStatusFilterSelected:
        (AdminUserStatusFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onUserDetailClick: (Int) -> Unit,
    onClearSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {
        TextButton(
            onClick = onBackClick
        ) {
            Text("← Admin Paneline Dön")
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Kullanıcı Yönetimi",
            style =
                MaterialTheme.typography
                    .headlineMedium
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text =
                "Customer, Producer ve Admin hesaplarını arayabilir ve filtreleyebilirsiniz.",

            style =
                MaterialTheme.typography
                    .bodyMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value =
                uiState.searchQuery,

            onValueChange =
                onSearchQueryChange,

            label = {
                Text(
                    "Ad, e-posta veya telefon ara"
                )
            },

            singleLine = true,

            supportingText = {
                Text(
                    "${uiState.searchQuery.length}/100"
                )
            },

            modifier =
                Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick =
                    onSearchClick,

                enabled =
                    !uiState.isLoading,

                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Ara")
            }

            OutlinedButton(
                onClick =
                    onClearSearchClick,

                enabled =
                    !uiState.isLoading &&
                            uiState.searchQuery
                                .isNotBlank(),

                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Temizle")
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Rol",
            style =
                MaterialTheme.typography
                    .titleSmall
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        AdminUserRoleTabs(
            selectedFilter =
                uiState.selectedRoleFilter,

            enabled =
                !uiState.isLoading,

            onFilterSelected =
                onRoleFilterSelected
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Text(
            text = "Hesap Durumu",
            style =
                MaterialTheme.typography
                    .titleSmall
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        AdminUserStatusTabs(
            selectedFilter =
                uiState.selectedStatusFilter,

            enabled =
                !uiState.isLoading,

            onFilterSelected =
                onStatusFilterSelected
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        when {
            uiState.isLoading -> {
                AppLoadingState(
                    message = "Kullanıcılar yükleniyor..."
                )
            }

            uiState.errorMessage != null -> {
                AppErrorState(
                    message = uiState.errorMessage,
                    onRetryClick = onRetryClick
                )
            }

            uiState.users.isEmpty() -> {
                AppEmptyState(
                    title = "Kullanıcı bulunamadı",
                    message = uiState.emptyMessage
                )
            }

            else -> {
                Text(
                    text =
                        "${uiState.users.size} kullanıcı bulundu.",

                    style =
                        MaterialTheme.typography
                            .bodySmall
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize(),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items =
                            uiState.users,

                        key = { user ->
                            user.userId
                        }
                    ) { user ->

                        AdminUserCard(
                            user = user,

                            onDetailClick = {
                                onUserDetailClick(
                                    user.userId
                                )
                            }
                        )
                    }

                    item {
                        Spacer(
                            modifier =
                                Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUserRoleTabs(
    selectedFilter:
    AdminUserRoleFilter,

    enabled: Boolean,

    onFilterSelected:
        (AdminUserRoleFilter) -> Unit
) {
    val filters =
        AdminUserRoleFilter.entries

    val selectedIndex =
        filters.indexOf(selectedFilter)
            .coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex =
            selectedIndex
    ) {
        filters.forEach { filter ->
            Tab(
                selected =
                    filter == selectedFilter,

                onClick = {
                    onFilterSelected(filter)
                },

                enabled = enabled,

                text = {
                    Text(filter.displayName)
                }
            )
        }
    }
}

@Composable
private fun AdminUserStatusTabs(
    selectedFilter:
    AdminUserStatusFilter,

    enabled: Boolean,

    onFilterSelected:
        (AdminUserStatusFilter) -> Unit
) {
    val filters =
        AdminUserStatusFilter.entries

    val selectedIndex =
        filters.indexOf(selectedFilter)
            .coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex =
            selectedIndex
    ) {
        filters.forEach { filter ->
            Tab(
                selected =
                    filter == selectedFilter,

                onClick = {
                    onFilterSelected(filter)
                },

                enabled = enabled,

                text = {
                    Text(filter.displayName)
                }
            )
        }
    }
}

@Composable
private fun AdminUserCard(
    user: AdminUserListItemResponse,
    onDetailClick: () -> Unit
){
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
                    user.fullName.ifBlank {
                        "İsimsiz Kullanıcı"
                    },

                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    if (user.isActive) {
                        "Aktif Hesap"
                    } else {
                        "Pasif Hesap"
                    },

                color =
                    if (user.isActive) {
                        MaterialTheme
                            .colorScheme.primary
                    } else {
                        MaterialTheme
                            .colorScheme.error
                    },

                style =
                    MaterialTheme.typography
                        .titleSmall
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AdminUserInformation(
                title = "Kullanıcı ID",
                value = user.userId.toString()
            )

            AdminUserInformation(
                title = "Rol",
                value = user.role
            )

            AdminUserInformation(
                title = "E-posta",
                value =
                    user.email.ifBlank {
                        "-"
                    }
            )

            AdminUserInformation(
                title = "Telefon",
                value =
                    user.phone.ifBlank {
                        "-"
                    }
            )

            AdminUserInformation(
                title = "Kayıt Tarihi",
                value =
                    formatAdminUserDate(
                        user.createdAt
                    )
            )

            if (
                user.producerProfileId != null
            ) {
                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                HorizontalDivider()

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Üretici Bilgileri",
                    style =
                        MaterialTheme.typography
                            .titleMedium
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                AdminUserInformation(
                    title = "İşletme",
                    value =
                        user.businessName
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?: "-"
                )

                AdminUserInformation(
                    title = "Başvuru Durumu",
                    value =
                        translateProducerStatus(
                            user
                                .producerVerificationStatus
                        )
                )
            }
            Spacer(
                modifier = Modifier.height(14.dp)
            )

            OutlinedButton(
                onClick = onDetailClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Detayı Gör")
            }
        }
    }
}

@Composable
private fun AdminUserInformation(
    title: String,
    value: String
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography
                    .bodySmall
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography
                    .titleSmall
        )
    }
}

private fun translateProducerStatus(
    status: String?
): String {
    return when {
        status.isNullOrBlank() ->
            "-"

        status.equals(
            "Pending",
            ignoreCase = true
        ) ->
            "Onay Bekliyor"

        status.equals(
            "Approved",
            ignoreCase = true
        ) ->
            "Onaylandı"

        status.equals(
            "Rejected",
            ignoreCase = true
        ) ->
            "Reddedildi"

        else ->
            status
    }
}

private fun formatAdminUserDate(
    value: String
): String {
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