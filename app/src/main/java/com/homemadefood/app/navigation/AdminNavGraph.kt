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