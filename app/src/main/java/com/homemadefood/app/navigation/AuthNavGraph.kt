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
import com.homemadefood.app.ui.auth.EmailVerificationScreen
import com.homemadefood.app.ui.auth.ForgotPasswordScreen
import com.homemadefood.app.ui.auth.LoginScreen
import com.homemadefood.app.ui.auth.RegisterScreen
import com.homemadefood.app.ui.auth.ResetPasswordScreen

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

            LaunchedEffect(
                authUiState.emailVerificationRequired,
                authUiState.pendingVerificationEmail
            ) {
                if (
                    authUiState.emailVerificationRequired &&
                    !authUiState
                        .pendingVerificationEmail
                        .isNullOrBlank()
                ) {
                    authViewModel
                        .consumeEmailVerificationRequired()

                    navController.navigate(
                        AppDestination
                            .EmailVerification
                            .route
                    ) {
                        popUpTo(
                            AppDestination
                                .Login
                                .route
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

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

                onForgotPasswordClick = {
                    authViewModel
                        .resetPasswordResetState()

                    navController.navigate(
                        AppDestination
                            .ForgotPassword
                            .route
                    ) {
                        launchSingleTop = true
                    }
                },

                onClearMessage = {
                    authViewModel
                        .clearMessage()
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
                authUiState.registrationSuccessful,
                authUiState.pendingVerificationEmail
            ) {
                if (
                    authUiState.registrationSuccessful &&
                    !authUiState
                        .pendingVerificationEmail
                        .isNullOrBlank()
                ) {
                    authViewModel
                        .resetRegistrationState()

                    navController.navigate(
                        AppDestination
                            .EmailVerification
                            .route
                    ) {
                        popUpTo(
                            AppDestination
                                .Register
                                .route
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
                        password ->

                    authViewModel.register(
                        fullName = fullName,
                        email = email,
                        password = password
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
                    }
                },

                onClearMessage = {
                    authViewModel.clearMessage()
                }
            )
        }

        composable(
            route =
                AppDestination
                    .EmailVerification
                    .route
        ) {
            val authUiState by
            authViewModel.uiState
                .collectAsStateWithLifecycle()

            val email =
                authUiState
                    .pendingVerificationEmail
                    .orEmpty()

            LaunchedEffect(email) {
                if (email.isBlank()) {
                    navController.navigate(
                        AppDestination.Login.route
                    ) {
                        popUpTo(
                            AppDestination
                                .EmailVerification
                                .route
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

            LaunchedEffect(
                authUiState
                    .emailVerificationSuccessful
            ) {
                if (
                    authUiState
                        .emailVerificationSuccessful
                ) {
                    authViewModel
                        .resetEmailVerificationState()

                    navController.navigate(
                        AppDestination.Login.route
                    ) {
                        popUpTo(
                            AppDestination
                                .EmailVerification
                                .route
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

            if (email.isNotBlank()) {
                EmailVerificationScreen(
                    email = email,

                    uiState =
                        authUiState,

                    onVerifyClick = { code ->
                        authViewModel
                            .verifyEmail(
                                email = email,
                                code = code
                            )
                    },

                    onResendClick = {
                        authViewModel
                            .resendEmailVerification(
                                email = email
                            )
                    },

                    onNavigateToLogin = {
                        authViewModel
                            .resetEmailVerificationState()

                        navController.navigate(
                            AppDestination.Login.route
                        ) {
                            popUpTo(
                                AppDestination
                                    .EmailVerification
                                    .route
                            ) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    },

                    onClearMessage = {
                        authViewModel
                            .clearMessage()
                    },

                    modifier =
                        Modifier.fillMaxSize()
                )
            }
        }

        composable(
            route =
                AppDestination
                    .ForgotPassword
                    .route
        ) {
            val authUiState by
            authViewModel.uiState
                .collectAsStateWithLifecycle()

            LaunchedEffect(
                authUiState
                    .passwordResetRequestSuccessful,
                authUiState
                    .pendingPasswordResetEmail
            ) {
                if (
                    authUiState
                        .passwordResetRequestSuccessful &&
                    !authUiState
                        .pendingPasswordResetEmail
                        .isNullOrBlank()
                ) {
                    authViewModel
                        .consumePasswordResetRequestSuccessful()

                    navController.navigate(
                        AppDestination
                            .ResetPassword
                            .route
                    ) {
                        popUpTo(
                            AppDestination
                                .ForgotPassword
                                .route
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

            ForgotPasswordScreen(
                uiState =
                    authUiState,

                onRequestResetClick = {
                        email ->

                    authViewModel
                        .requestPasswordReset(
                            email = email
                        )
                },

                onNavigateToLogin = {
                    authViewModel
                        .resetPasswordResetState()

                    navController.popBackStack()
                },

                onClearMessage = {
                    authViewModel
                        .clearMessage()
                },

                modifier =
                    Modifier.fillMaxSize()
            )
        }

        composable(
            route =
                AppDestination
                    .ResetPassword
                    .route
        ) {
            val authUiState by
            authViewModel.uiState
                .collectAsStateWithLifecycle()

            val email =
                authUiState
                    .pendingPasswordResetEmail
                    .orEmpty()

            LaunchedEffect(email) {
                if (email.isBlank()) {
                    authViewModel
                        .resetPasswordResetState()

                    navController.navigate(
                        AppDestination.Login.route
                    ) {
                        popUpTo(
                            AppDestination
                                .ResetPassword
                                .route
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

            LaunchedEffect(
                authUiState
                    .passwordResetSuccessful
            ) {
                if (
                    authUiState
                        .passwordResetSuccessful
                ) {
                    authViewModel
                        .consumePasswordResetSuccessful()

                    navController.navigate(
                        AppDestination.Login.route
                    ) {
                        popUpTo(
                            AppDestination
                                .ResetPassword
                                .route
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

            if (email.isNotBlank()) {
                ResetPasswordScreen(
                    email = email,

                    uiState =
                        authUiState,

                    onResetPasswordClick = {
                            code,
                            newPassword ->

                        authViewModel
                            .resetPassword(
                                email = email,
                                code = code,
                                newPassword =
                                    newPassword
                            )
                    },

                    onResendCodeClick = {
                        authViewModel
                            .resendPasswordResetCode(
                                email = email
                            )
                    },

                    onNavigateToLogin = {
                        authViewModel
                            .resetPasswordResetState()

                        navController.navigate(
                            AppDestination.Login.route
                        ) {
                            popUpTo(
                                AppDestination
                                    .ResetPassword
                                    .route
                            ) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    },

                    onClearMessage = {
                        authViewModel
                            .clearMessage()
                    },

                    modifier =
                        Modifier.fillMaxSize()
                )
            }
        }
    }
}