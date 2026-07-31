package com.homemadefood.app.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.homemadefood.app.ui.auth.AuthViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    context: Context,
    authViewModel: AuthViewModel,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    val authUiState by
    authViewModel.uiState
        .collectAsStateWithLifecycle()

    val currentBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentDestination =
        currentBackStackEntry?.destination

    val targetGraph =
        resolveStartDestination(
            isLoggedIn =
                authUiState.isLoggedIn,

            backendRole =
                authUiState.userRole
        )

    LaunchedEffect(
        authUiState.isSessionChecking,
        authUiState.isLoggedIn,
        authUiState.userRole,
        currentDestination?.route
    ) {
        if (authUiState.isSessionChecking) {
            return@LaunchedEffect
        }

        if (currentDestination == null) {
            return@LaunchedEffect
        }

        val currentGraphRoute =
            currentDestination
                .findCurrentAppGraphRoute()

        if (currentGraphRoute == targetGraph) {
            return@LaunchedEffect
        }

        navController.navigate(
            targetGraph
        ) {
            if (currentGraphRoute != null) {
                popUpTo(
                    currentGraphRoute
                ) {
                    inclusive = true
                }
            }

            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        authNavGraph(
            navController = navController,
            authViewModel = authViewModel
        )

        customerNavGraph(
            navController = navController,
            context = context,

            onLogoutClick = {
                authViewModel.logout()
            }
        )

        producerNavGraph(
            navController = navController,
            context = context,

            onLogoutClick = {
                authViewModel.logout()
            }
        )

        adminNavGraph(
            navController = navController,
            context = context,

            onLogoutClick = {
                authViewModel.logout()
            }
        )
    }
}

private fun NavDestination?.findCurrentAppGraphRoute():
        String? {

    val applicationGraphs =
        setOf(
            AppGraph.AUTH,
            AppGraph.CUSTOMER,
            AppGraph.PRODUCER,
            AppGraph.ADMIN
        )

    var destination =
        this

    while (destination != null) {
        val route =
            destination.route

        if (
            route != null &&
            route in applicationGraphs
        ) {
            return route
        }

        destination =
            destination.parent
    }

    return null
}