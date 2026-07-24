package com.homemadefood.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homemadefood.app.ui.auth.AuthViewModel
import com.homemadefood.app.ui.auth.AuthViewModelFactory
import com.homemadefood.app.ui.auth.LoginScreen
import com.homemadefood.app.ui.auth.RegisterScreen
import com.homemadefood.app.ui.customer.CustomerHomeScreen
import com.homemadefood.app.ui.customer.CustomerHomeViewModel
import com.homemadefood.app.ui.customer.CustomerHomeViewModelFactory
import com.homemadefood.app.ui.food.FoodDetailScreen
import com.homemadefood.app.ui.food.FoodDetailViewModel
import com.homemadefood.app.ui.food.FoodDetailViewModelFactory
import com.homemadefood.app.ui.theme.HomemadeFoodTheme
import com.homemadefood.app.ui.favorite.FavoritesScreen
import com.homemadefood.app.ui.favorite.FavoritesViewModel
import com.homemadefood.app.ui.favorite.FavoritesViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            HomemadeFoodTheme {
                HomemadeFoodApp()
            }
        }
    }
}

@Composable
private fun HomemadeFoodApp() {
    val context =
        LocalContext.current

    val authFactory =
        remember(context) {
            AuthViewModelFactory(
                context = context
            )
        }

    val authViewModel: AuthViewModel =
        viewModel(
            factory = authFactory
        )

    val authUiState by authViewModel.uiState
        .collectAsStateWithLifecycle()

    var showRegisterScreen by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedFoodId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }
    var showFavoritesScreen by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(
        authUiState.registrationSuccessful
    ) {
        if (
            authUiState.registrationSuccessful
        ) {
            showRegisterScreen = false

            authViewModel
                .resetRegistrationState()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        when {
            authUiState.isSessionChecking -> {
                SessionLoadingScreen(
                    modifier =
                        Modifier.padding(
                            innerPadding
                        )
                )
            }

            authUiState.isLoggedIn &&
                    authUiState.userRole ==
                    "Customer" -> {

                val foodId =
                    selectedFoodId

                if (foodId != null) {

                    val foodDetailViewModel:
                            FoodDetailViewModel =
                        viewModel(
                            key = "food_detail_$foodId",

                            factory =
                                FoodDetailViewModelFactory(
                                    context = context,
                                    foodId = foodId
                                )
                        )

                    val foodDetailUiState by
                    foodDetailViewModel.uiState
                        .collectAsStateWithLifecycle()
                    LaunchedEffect(foodId) {
                        foodDetailViewModel.loadFood()
                    }


                    FoodDetailScreen(
                        uiState = foodDetailUiState,

                        onBackClick = {
                            selectedFoodId = null
                            showFavoritesScreen = false
                        },

                        onRetryClick = {
                            foodDetailViewModel.loadFood()
                        },

                        onFavoriteClick = {
                            foodDetailViewModel.toggleFavorite()
                        },

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )
                    )

                } else if (showFavoritesScreen) {
                    val favoritesViewModel:
                            FavoritesViewModel =
                        viewModel(
                            key = "favorites_screen",
                            factory =
                                FavoritesViewModelFactory(
                                    context = context
                                )
                        )

                    val favoritesUiState by
                    favoritesViewModel.uiState
                        .collectAsStateWithLifecycle()
                    LaunchedEffect(Unit) {
                        favoritesViewModel.loadFavorites()
                    }

                    FavoritesScreen(
                        uiState = favoritesUiState,

                        onBackClick = {
                            selectedFoodId = null
                            showFavoritesScreen = false
                        },

                        onRetryClick = {
                            favoritesViewModel.loadFavorites()
                        },

                        onRemoveFavoriteClick = { foodId ->
                            favoritesViewModel.removeFavorite(
                                foodId = foodId
                            )
                        },

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )
                    )
                } else {
                    val customerHomeViewModel:
                            CustomerHomeViewModel =
                        viewModel(
                            factory =
                                CustomerHomeViewModelFactory()
                        )

                    val customerHomeUiState by
                    customerHomeViewModel.uiState
                        .collectAsStateWithLifecycle()

                    CustomerHomeScreen(
                        uiState =
                            customerHomeUiState,

                        onSearchQueryChange = {
                                query ->

                            customerHomeViewModel
                                .updateSearchQuery(query)
                        },

                        onSearchClick = {
                            customerHomeViewModel
                                .searchFoods()
                        },

                        onCategoryClick = {
                                categoryId ->

                            customerHomeViewModel
                                .selectCategory(
                                    categoryId
                                )
                        },

                        onClearFiltersClick = {
                            customerHomeViewModel
                                .clearFilters()
                        },

                        onRetryCategoriesClick = {
                            customerHomeViewModel
                                .loadCategories()
                        },

                        onRetryFoodsClick = {
                            customerHomeViewModel
                                .loadFoods()
                        },

                        onFoodClick = { clickedFoodId ->
                            showFavoritesScreen = false
                            selectedFoodId = clickedFoodId
                        },

                        onFavoritesClick = {
                            selectedFoodId = null
                            showFavoritesScreen = true
                        },

                        onLogoutClick = {
                            selectedFoodId = null
                            showFavoritesScreen = false
                            authViewModel.logout()
                        },
                        modifier =
                            Modifier.padding(
                                innerPadding
                            )
                    )
                }
            }

            showRegisterScreen -> {
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
                        authViewModel
                            .clearMessage()

                        showRegisterScreen =
                            false
                    },

                    modifier =
                        Modifier.padding(
                            innerPadding
                        )
                )
            }

            else -> {
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
                        authViewModel
                            .clearMessage()

                        showRegisterScreen =
                            true
                    },

                    modifier =
                        Modifier.padding(
                            innerPadding
                        )
                )
            }
        }
    }
}

@Composable
private fun SessionLoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}