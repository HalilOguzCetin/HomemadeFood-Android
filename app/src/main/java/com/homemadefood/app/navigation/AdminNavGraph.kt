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
import com.homemadefood.app.ui.admin.RecommendationAnalyticsScreen
import com.homemadefood.app.ui.admin.RecommendationAnalyticsViewModel
import com.homemadefood.app.ui.admin.RecommendationAnalyticsViewModelFactory

fun NavGraphBuilder.adminNavGraph(
    navController: NavHostController,
    context: Context,
    onLogoutClick: () -> Unit
) {
    navigation(
        route = AppGraph.ADMIN,
        startDestination =
            AppDestination.AdminHome.route
    ) {
        adminHomeDestination(
            navController = navController,
            onLogoutClick = onLogoutClick
        )

        adminApplicationsDestination(
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
                    AppDestination
                        .AdminApplications
                        .route
                )
            },

            onRecommendationAnalyticsClick = {
                navController.navigate(
                    AppDestination
                        .RecommendationAnalytics
                        .route
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
        route =
            AppDestination.AdminApplications.route
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
        adminApplicationsViewModel.uiState
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

            onApproveClick = {
                    producerProfileId ->

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

                        reason = reason
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

            modifier =
                Modifier.fillMaxSize()
        )
    }
}