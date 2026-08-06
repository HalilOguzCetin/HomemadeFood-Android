package com.homemadefood.app.navigation

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.homemadefood.app.ui.admin.AdminApplicationsScreen
import com.homemadefood.app.ui.admin.AdminApplicationsViewModel
import com.homemadefood.app.ui.admin.AdminApplicationsViewModelFactory
import com.homemadefood.app.ui.admin.AdminHomeScreen
import com.homemadefood.app.ui.admin.AdminUsersScreen
import com.homemadefood.app.ui.admin.AdminUsersViewModel
import com.homemadefood.app.ui.admin.AdminUsersViewModelFactory
import com.homemadefood.app.ui.admin.RecommendationAnalyticsScreen
import com.homemadefood.app.ui.admin.RecommendationAnalyticsViewModel
import com.homemadefood.app.ui.admin.RecommendationAnalyticsViewModelFactory
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.homemadefood.app.ui.admin.AdminUserDetailScreen
import com.homemadefood.app.ui.admin.AdminUserDetailViewModel
import com.homemadefood.app.ui.admin.AdminUserDetailViewModelFactory
import androidx.navigation.navArgument
import com.homemadefood.app.ui.admin.AdminOrderDetailScreen
import com.homemadefood.app.ui.admin.AdminOrderDetailViewModel
import com.homemadefood.app.ui.admin.AdminOrderDetailViewModelFactory
import com.homemadefood.app.ui.admin.AdminOrdersScreen
import com.homemadefood.app.ui.admin.AdminOrdersViewModel
import com.homemadefood.app.ui.admin.AdminOrdersViewModelFactory
fun NavGraphBuilder.adminNavGraph(
    navController: NavHostController,
    context: Context,
    onLogoutClick: () -> Unit
) {
    navigation(
        route = AppGraph.ADMIN,
        startDestination = AppDestination.AdminHome.route
    ) {
        adminHomeDestination(
            navController = navController,
            onLogoutClick = onLogoutClick
        )

        adminApplicationsDestination(
            navController = navController,
            context = context
        )

        adminUsersDestination(
            navController = navController,
            context = context
        )
        adminUserDetailDestination(
            navController = navController,
            context = context
        )
        adminOrdersDestination(
            navController = navController,
            context = context
        )

        adminOrderDetailDestination(
            navController = navController,
            context = context
        )

        recommendationAnalyticsDestination(
            navController = navController,
            context = context
        )
    }
}

private fun NavGraphBuilder.adminHomeDestination(
    navController: NavHostController,
    onLogoutClick: () -> Unit
) {
    composable(
        route = AppDestination.AdminHome.route
    ) {
        AdminHomeScreen(
            onProducerApplicationsClick = {
                navController.navigate(
                    AppDestination.AdminApplications.route
                )
            },

            onUsersClick = {
                navController.navigate(
                    AppDestination.AdminUsers.route
                )
            },
            onOrdersClick = {
                navController.navigate(
                    AppDestination
                        .AdminOrders
                        .route
                )
            },

            onRecommendationAnalyticsClick = {
                navController.navigate(
                    AppDestination.RecommendationAnalytics.route
                )
            },

            onLogoutClick = onLogoutClick,

            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.adminApplicationsDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.AdminApplications.route
    ) {
        val adminApplicationsViewModel:
                AdminApplicationsViewModel =
            viewModel(
                factory =
                    AdminApplicationsViewModelFactory(
                        context = context
                    )
            )

        val adminApplicationsUiState by
        adminApplicationsViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            adminApplicationsViewModel
                .loadApplications()
        }

        AdminApplicationsScreen(
            uiState =
                adminApplicationsUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                adminApplicationsViewModel
                    .loadApplications()
            },

            onStatusSelected = { status ->
                adminApplicationsViewModel
                    .selectStatus(status)
            },

            onApproveClick = { producerProfileId ->
                adminApplicationsViewModel
                    .approveApplication(
                        producerProfileId
                    )
            },

            onRejectClick = {
                    producerProfileId,
                    reason ->

                adminApplicationsViewModel
                    .rejectApplication(
                        producerProfileId =
                            producerProfileId,

                        reason =
                            reason
                    )
            },

            onClearMessage = {
                adminApplicationsViewModel
                    .clearMessage()
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.adminUsersDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.AdminUsers.route
    ) {
        val adminUsersViewModel:
                AdminUsersViewModel =
            viewModel(
                factory =
                    AdminUsersViewModelFactory(
                        context = context
                    )
            )

        val adminUsersUiState by
        adminUsersViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            adminUsersViewModel.loadUsers()
        }

        AdminUsersScreen(
            uiState =
                adminUsersUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                adminUsersViewModel.loadUsers()
            },

            onRoleFilterSelected = { filter ->
                adminUsersViewModel
                    .selectRoleFilter(filter)
            },

            onStatusFilterSelected = { filter ->
                adminUsersViewModel
                    .selectStatusFilter(filter)
            },

            onSearchQueryChange = { value ->
                adminUsersViewModel
                    .updateSearchQuery(value)
            },

            onSearchClick = {
                adminUsersViewModel.searchUsers()
            },

            onClearSearchClick = {
                adminUsersViewModel.clearSearch()
            },
            onUserDetailClick = { userId ->
                navController.navigate(
                    AppDestination
                        .AdminUserDetail
                        .createRoute(userId)
                )
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder
        .adminUserDetailDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .AdminUserDetail
                .route,

        arguments =
            listOf(
                navArgument(
                    AppDestination
                        .AdminUserDetail
                        .USER_ID_ARGUMENT
                ) {
                    type = NavType.IntType
                }
            )
    ) { backStackEntry ->

        val userId =
            backStackEntry.arguments
                ?.getInt(
                    AppDestination
                        .AdminUserDetail
                        .USER_ID_ARGUMENT
                )
                ?: 0

        val adminUserDetailViewModel:
                AdminUserDetailViewModel =
            viewModel(
                factory =
                    AdminUserDetailViewModelFactory(
                        context = context
                    )
            )

        val adminUserDetailUiState by
        adminUserDetailViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(userId) {
            adminUserDetailViewModel
                .loadUser(userId)
        }

        AdminUserDetailScreen(
            uiState =
                adminUserDetailUiState,

            onBackClick = {
                navController
                    .popBackStack()
            },

            onRetryClick = {
                adminUserDetailViewModel
                    .loadUser(userId)
            },

            onUpdateStatusClick = {
                    isActive ->

                adminUserDetailViewModel
                    .updateUserStatus(
                        isActive
                    )
            },

            onClearMessage = {
                adminUserDetailViewModel
                    .clearMessage()
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder
        .adminOrdersDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .AdminOrders
                .route
    ) {
        val adminOrdersViewModel:
                AdminOrdersViewModel =
            viewModel(
                factory =
                    AdminOrdersViewModelFactory(
                        context = context
                    )
            )

        val adminOrdersUiState by
        adminOrdersViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            adminOrdersViewModel
                .loadOrders()
        }

        AdminOrdersScreen(
            uiState =
                adminOrdersUiState,

            onBackClick = {
                navController
                    .popBackStack()
            },

            onRetryClick = {
                adminOrdersViewModel
                    .loadOrders()
            },

            onStatusSelected = { status ->
                adminOrdersViewModel
                    .selectStatus(status)
            },

            onSearchQueryChange = { value ->
                adminOrdersViewModel
                    .updateSearchQuery(value)
            },

            onCustomerIdChange = { value ->
                adminOrdersViewModel
                    .updateCustomerIdInput(value)
            },

            onProducerProfileIdChange = { value ->
                adminOrdersViewModel
                    .updateProducerProfileIdInput(
                        value
                    )
            },

            onDateFromChange = { value ->
                adminOrdersViewModel
                    .updateDateFromInput(value)
            },

            onDateToChange = { value ->
                adminOrdersViewModel
                    .updateDateToInput(value)
            },

            onApplyFiltersClick = {
                adminOrdersViewModel
                    .applyFilters()
            },

            onClearFiltersClick = {
                adminOrdersViewModel
                    .clearFilters()
            },

            onOrderDetailClick = { orderId ->
                navController.navigate(
                    AppDestination
                        .AdminOrderDetail
                        .createRoute(orderId)
                )
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder
        .adminOrderDetailDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .AdminOrderDetail
                .route,

        arguments =
            listOf(
                navArgument(
                    AppDestination
                        .AdminOrderDetail
                        .ORDER_ID_ARGUMENT
                ) {
                    type =
                        NavType.IntType
                }
            )
    ) { backStackEntry ->

        val orderId =
            backStackEntry.arguments
                ?.getInt(
                    AppDestination
                        .AdminOrderDetail
                        .ORDER_ID_ARGUMENT
                )
                ?: 0

        val adminOrderDetailViewModel:
                AdminOrderDetailViewModel =
            viewModel(
                factory =
                    AdminOrderDetailViewModelFactory(
                        context = context
                    )
            )

        val adminOrderDetailUiState by
        adminOrderDetailViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(orderId) {
            adminOrderDetailViewModel
                .loadOrder(orderId)
        }

        AdminOrderDetailScreen(
            uiState =
                adminOrderDetailUiState,

            onBackClick = {
                navController
                    .popBackStack()
            },

            onRetryClick = {
                adminOrderDetailViewModel
                    .loadOrder(orderId)
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.recommendationAnalyticsDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .RecommendationAnalytics
                .route
    ) {
        val recommendationAnalyticsViewModel:
                RecommendationAnalyticsViewModel =
            viewModel(
                factory =
                    RecommendationAnalyticsViewModelFactory(
                        context = context
                    )
            )

        val recommendationAnalyticsUiState by
        recommendationAnalyticsViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            recommendationAnalyticsViewModel
                .loadAnalytics()
        }

        RecommendationAnalyticsScreen(
            uiState =
                recommendationAnalyticsUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                recommendationAnalyticsViewModel
                    .loadAnalytics()
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}