package com.homemadefood.app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.homemadefood.app.ui.auth.AuthViewModel
import com.homemadefood.app.ui.auth.LoginScreen
import com.homemadefood.app.ui.auth.RegisterScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(
        route = AppGraph.AUTH,
        startDestination =
            AppDestination.Login.route
    ) {

        composable(
            route =
                AppDestination.Login.route
        ) {
            val authUiState by
            authViewModel.uiState
                .collectAsStateWithLifecycle()

            LoginScreen(
                uiState = authUiState,

                onLoginClick = {
                        email,
                        password ->

                    authViewModel.login(
                        email = email,
                        password = password
                    )
                },

                onNavigateToRegister = {
                    authViewModel.clearMessage()

                    navController.navigate(
                        AppDestination.Register.route
                    ) {
                        launchSingleTop = true
                    }
                },

                modifier =
                    Modifier.fillMaxSize()
            )
        }

        composable(
            route =
                AppDestination.Register.route
        ) {
            val authUiState by
            authViewModel.uiState
                .collectAsStateWithLifecycle()

            LaunchedEffect(
                authUiState.registrationSuccessful
            ) {
                if (
                    authUiState.registrationSuccessful
                ) {
                    authViewModel
                        .resetRegistrationState()

                    navController.navigate(
                        AppDestination.Login.route
                    ) {
                        popUpTo(
                            AppDestination.Register.route
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

            RegisterScreen(
                uiState = authUiState,

                onRegisterClick = {
                        fullName,
                        email,
                        password,
                        phone ->

                    authViewModel.register(
                        fullName = fullName,
                        email = email,
                        password = password,
                        phone = phone
                    )
                },

                onNavigateToLogin = {
                    authViewModel.clearMessage()

                    navController.navigate(
                        AppDestination.Login.route
                    ) {
                        popUpTo(
                            AppDestination.Register.route
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },

                modifier =
                    Modifier.fillMaxSize()
            )
        }
    }
}