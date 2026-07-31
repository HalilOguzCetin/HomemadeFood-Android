package com.homemadefood.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.homemadefood.app.ui.auth.AuthViewModel
import com.homemadefood.app.ui.auth.AuthViewModelFactory

@Composable
fun NavigationHomemadeFoodApp() {
    val context =
        LocalContext.current

    val navController =
        rememberNavController()

    val authFactory =
        remember(context) {
            AuthViewModelFactory(
                context = context
            )
        }

    val authViewModel:
            AuthViewModel =
        viewModel(
            factory = authFactory
        )

    val authUiState by
    authViewModel.uiState
        .collectAsStateWithLifecycle()

    if (authUiState.isSessionChecking) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        return
    }

    val startDestination =
        remember(
            authUiState.isLoggedIn,
            authUiState.userRole
        ) {
            resolveStartDestination(
                isLoggedIn =
                    authUiState.isLoggedIn,

                backendRole =
                    authUiState.userRole
            )
        }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        AppNavGraph(
            navController = navController,
            context = context,
            authViewModel = authViewModel,
            startDestination = startDestination,

            modifier =
                Modifier.padding(
                    innerPadding
                )
        )
    }
}