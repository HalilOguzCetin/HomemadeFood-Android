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
import com.homemadefood.app.ui.producer.ProducerApplicationScreen
import com.homemadefood.app.ui.producer.ProducerApplicationViewModel
import com.homemadefood.app.ui.producer.ProducerApplicationViewModelFactory
import com.homemadefood.app.ui.producer.ProducerHomeScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.homemadefood.app.ui.producer.CreateFoodScreen
import com.homemadefood.app.ui.producer.CreateFoodViewModel
import com.homemadefood.app.ui.producer.CreateFoodViewModelFactory
import com.homemadefood.app.ui.producer.EditFoodScreen
import com.homemadefood.app.ui.producer.EditFoodViewModel
import com.homemadefood.app.ui.producer.EditFoodViewModelFactory
import com.homemadefood.app.ui.producer.ProducerFoodsScreen
import com.homemadefood.app.ui.producer.ProducerFoodsViewModel
import com.homemadefood.app.ui.producer.ProducerFoodsViewModelFactory
import com.homemadefood.app.ui.producer.ProducerOrdersScreen
import com.homemadefood.app.ui.producer.ProducerOrdersViewModel
import com.homemadefood.app.ui.producer.ProducerOrdersViewModelFactory
import com.homemadefood.app.ui.producer.ProducerReviewsScreen
import com.homemadefood.app.ui.producer.ProducerReviewsViewModel
import com.homemadefood.app.ui.producer.ProducerReviewsViewModelFactory
import com.homemadefood.app.ui.producer.ProducerProfileScreen
import com.homemadefood.app.ui.producer.ProducerProfileViewModel
import com.homemadefood.app.ui.producer.ProducerProfileViewModelFactory
import com.homemadefood.app.ui.auth.AuthViewModel
import com.homemadefood.app.ui.address.SelectedLocation
import com.homemadefood.app.ui.location.LocationMapScreen
private const val PRODUCER_PROFILE_MAP_RESULT_LATITUDE =
    "producer_profile_map_result_latitude"

private const val PRODUCER_PROFILE_MAP_RESULT_LONGITUDE =
    "producer_profile_map_result_longitude"

private const val PRODUCER_PROFILE_MAP_INITIAL_LATITUDE =
    "producer_profile_map_initial_latitude"

private const val PRODUCER_PROFILE_MAP_INITIAL_LONGITUDE =
    "producer_profile_map_initial_longitude"

fun NavGraphBuilder.producerNavGraph(
    navController: NavHostController,
    context: Context,
    authViewModel: AuthViewModel,
    onLogoutClick: () -> Unit
){
    navigation(
        route = AppGraph.PRODUCER,
        startDestination =
            AppDestination.ProducerHome.route
    ) {
        producerHomeDestination(
            navController = navController,
            authViewModel = authViewModel,
            onLogoutClick = onLogoutClick
        )

        producerApplicationDestination(
            navController = navController,
            context = context
        )
        producerProfileDestination(
            navController = navController,
            context = context
        )
        producerProfileAddressMapDestination(
            navController = navController
        )
        producerFoodsDestination(
            navController = navController,
            context = context
        )

        createFoodDestination(
            navController = navController,
            context = context
        )

        editFoodDestination(
            navController = navController,
            context = context
        )
        producerOrdersDestination(
            navController = navController,
            context = context
        )
        producerReviewsDestination(
            navController = navController,
            context = context
        )
    }
}

private fun NavGraphBuilder.producerHomeDestination(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onLogoutClick: () -> Unit
){
    composable(
        route =
            AppDestination.ProducerHome.route
    ) {
        ProducerHomeScreen(
            onApplicationStatusClick = {
                navController.navigate(
                    AppDestination
                        .ProducerApplication
                        .route
                )
            },

            onCustomerModeClick = {
                authViewModel
                    .switchToCustomerMode()
            },
            onProfileClick = {
                navController.navigate(
                    AppDestination
                        .ProducerProfile
                        .route
                )
            },

            onFoodsClick = {
                navController.navigate(
                    AppDestination
                        .ProducerFoods
                        .route
                )
            },

            onOrdersClick = {
                navController.navigate(
                    AppDestination
                        .ProducerOrders
                        .route
                )
            },
            onReviewsClick = {
                navController.navigate(
                    AppDestination
                        .ProducerReviews
                        .route
                )
            },

            onLogoutClick =
                onLogoutClick,

            modifier =
                Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.producerApplicationDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .ProducerApplication
                .route
    ) {
        val producerApplicationViewModel:
                ProducerApplicationViewModel =
            viewModel(
                factory =
                    ProducerApplicationViewModelFactory(
                        context = context
                    )
            )

        val producerApplicationUiState by
        producerApplicationViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            producerApplicationViewModel
                .loadApplication()
        }

        ProducerApplicationScreen(
            uiState =
                producerApplicationUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                producerApplicationViewModel
                    .loadApplication()
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.producerFoodsDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.ProducerFoods.route
    ) {
        val producerFoodsViewModel:
                ProducerFoodsViewModel =
            viewModel(
                factory =
                    ProducerFoodsViewModelFactory(
                        context = context
                    )
            )

        val producerFoodsUiState by
        producerFoodsViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            producerFoodsViewModel.loadFoods()
        }

        ProducerFoodsScreen(
            uiState = producerFoodsUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                producerFoodsViewModel.loadFoods()
            },

            onAddFoodClick = {
                navController.navigate(
                    AppDestination.CreateFood.route
                )
            },

            onEditFoodClick = { foodId ->
                navController.navigate(
                    AppDestination.EditFood
                        .createRoute(foodId)
                )
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.createFoodDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.CreateFood.route
    ) {
        val createFoodViewModel:
                CreateFoodViewModel =
            viewModel(
                factory =
                    CreateFoodViewModelFactory(
                        context = context
                    )
            )

        val createFoodUiState by
        createFoodViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(
            createFoodUiState.createdFood?.id
        ) {
            if (createFoodUiState.createdFood != null) {
                createFoodViewModel.resetForm()

                navController.popBackStack()
            }
        }

        CreateFoodScreen(
            uiState = createFoodUiState,

            onCategorySelected = { categoryId ->
                createFoodViewModel
                    .onCategorySelected(categoryId)
            },

            onRetryCategoriesClick = {
                createFoodViewModel
                    .loadCategories()
            },

            onNameChange = { value ->
                createFoodViewModel
                    .onNameChange(value)
            },

            onDescriptionChange = { value ->
                createFoodViewModel
                    .onDescriptionChange(value)
            },

            onPriceChange = { value ->
                createFoodViewModel
                    .onPriceChange(value)
            },

            onPreparationTimeChange = { value ->
                createFoodViewModel
                    .onPreparationTimeChange(value)
            },

            onImageSelected = { uri ->
                createFoodViewModel
                    .onImageSelected(uri)
            },

            onRemoveImage = {
                createFoodViewModel
                    .onImageRemoved()
            },

            onSaveClick = {
                createFoodViewModel.createFood()
            },

            onBackClick = {
                createFoodViewModel.resetForm()

                navController.popBackStack()
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.editFoodDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.EditFood.route,

        arguments = listOf(
            navArgument(
                AppDestination.EditFood
                    .FOOD_ID_ARGUMENT
            ) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->

        val foodId =
            backStackEntry.arguments
                ?.getInt(
                    AppDestination.EditFood
                        .FOOD_ID_ARGUMENT
                )
                ?: return@composable

        val editFoodViewModel:
                EditFoodViewModel =
            viewModel(
                factory =
                    EditFoodViewModelFactory(
                        context = context
                    )
            )

        val editFoodUiState by
        editFoodViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(foodId) {
            editFoodViewModel.loadFood(
                foodId = foodId
            )
        }

        LaunchedEffect(
            editFoodUiState.updatedFood?.id
        ) {
            if (editFoodUiState.updatedFood != null) {
                navController.popBackStack()
            }
        }

        EditFoodScreen(
            uiState = editFoodUiState,

            onCategorySelected = { categoryId ->
                editFoodViewModel
                    .onCategorySelected(categoryId)
            },

            onRetryCategoriesClick = {
                editFoodViewModel
                    .loadCategories()
            },

            onNameChange = { value ->
                editFoodViewModel
                    .onNameChange(value)
            },

            onDescriptionChange = { value ->
                editFoodViewModel
                    .onDescriptionChange(value)
            },

            onPriceChange = { value ->
                editFoodViewModel
                    .onPriceChange(value)
            },

            onPreparationTimeChange = { value ->
                editFoodViewModel
                    .onPreparationTimeChange(value)
            },

            onImageSelected = { uri ->
                editFoodViewModel
                    .onImageSelected(uri)
            },

            onCancelImageSelection = {
                editFoodViewModel
                    .onImageSelectionRemoved()
            },

            onAvailabilityChange = { value ->
                editFoodViewModel
                    .onAvailabilityChange(value)
            },

            onSaveClick = {
                editFoodViewModel.updateFood()
            },

            onRetryClick = {
                editFoodViewModel.resetState()

                editFoodViewModel.loadFood(
                    foodId = foodId
                )
            },

            onBackClick = {
                navController.popBackStack()
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.producerOrdersDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.ProducerOrders.route
    ) {
        val producerOrdersViewModel:
                ProducerOrdersViewModel =
            viewModel(
                factory =
                    ProducerOrdersViewModelFactory(
                        context = context
                    )
            )

        val producerOrdersUiState by
        producerOrdersViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            producerOrdersViewModel.loadOrders()
        }

        ProducerOrdersScreen(
            uiState = producerOrdersUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                producerOrdersViewModel.loadOrders()
            },

            onAcceptClick = { orderId ->
                producerOrdersViewModel.acceptOrder(
                    orderId = orderId
                )
            },

            onRejectClick = { orderId ->
                producerOrdersViewModel.rejectOrder(
                    orderId = orderId
                )
            },

            onStartPreparingClick = { orderId ->
                producerOrdersViewModel.startPreparing(
                    orderId = orderId
                )
            },

            onMarkReadyClick = { orderId ->
                producerOrdersViewModel.markReady(
                    orderId = orderId
                )
            },

            onOutForDeliveryClick = { orderId ->
                producerOrdersViewModel
                    .markOutForDelivery(
                        orderId = orderId
                    )
            },

            onDeliveredClick = { orderId ->
                producerOrdersViewModel.markDelivered(
                    orderId = orderId
                )
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.producerReviewsDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .ProducerReviews
                .route
    ) {
        val producerReviewsViewModel:
                ProducerReviewsViewModel =
            viewModel(
                factory =
                    ProducerReviewsViewModelFactory(
                        context = context
                    )
            )

        val producerReviewsUiState by
        producerReviewsViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            producerReviewsViewModel
                .loadReviews()
        }

        ProducerReviewsScreen(
            uiState =
                producerReviewsUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                producerReviewsViewModel
                    .loadReviews()
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.producerProfileDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .ProducerProfile
                .route
    ) { backStackEntry ->

        val producerProfileViewModel:
                ProducerProfileViewModel =
            viewModel(
                factory =
                    ProducerProfileViewModelFactory(
                        context = context
                    )
            )

        val producerProfileUiState by
        producerProfileViewModel
            .uiState
            .collectAsStateWithLifecycle()

        val selectedLatitude by
        backStackEntry
            .savedStateHandle
            .getStateFlow<Double?>(
                PRODUCER_PROFILE_MAP_RESULT_LATITUDE,
                null
            )
            .collectAsStateWithLifecycle()

        val selectedLongitude by
        backStackEntry
            .savedStateHandle
            .getStateFlow<Double?>(
                PRODUCER_PROFILE_MAP_RESULT_LONGITUDE,
                null
            )
            .collectAsStateWithLifecycle()

        /*
         * Haritadan geri dönüldüğünde form state'ini
         * tekrar backend verisiyle ezmeyelim.
         */
        LaunchedEffect(Unit) {
            if (producerProfileUiState.isLoading) {
                producerProfileViewModel
                    .loadProfile()
            }
        }

        LaunchedEffect(
            selectedLatitude,
            selectedLongitude
        ) {
            val latitude =
                selectedLatitude

            val longitude =
                selectedLongitude

            if (
                latitude != null &&
                longitude != null
            ) {
                producerProfileViewModel
                    .updateSelectedLocation(
                        latitude = latitude,
                        longitude = longitude
                    )

                backStackEntry
                    .savedStateHandle
                    .remove<Double>(
                        PRODUCER_PROFILE_MAP_RESULT_LATITUDE
                    )

                backStackEntry
                    .savedStateHandle
                    .remove<Double>(
                        PRODUCER_PROFILE_MAP_RESULT_LONGITUDE
                    )
            }
        }

        ProducerProfileScreen(
            uiState =
                producerProfileUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                producerProfileViewModel
                    .loadProfile()
            },

            onStartEditingClick = {
                producerProfileViewModel
                    .startEditing()
            },

            onCancelEditingClick = {
                producerProfileViewModel
                    .cancelEditing()
            },

            onBusinessNameChange = { value ->
                producerProfileViewModel
                    .updateBusinessName(value)
            },

            onDescriptionChange = { value ->
                producerProfileViewModel
                    .updateDescription(value)
            },

            onBusinessImageSelected = { uri ->
                producerProfileViewModel
                    .onBusinessImageSelected(uri)
            },

            onRemoveSelectedBusinessImage = {
                producerProfileViewModel
                    .onRemoveSelectedBusinessImage()
            },

            onCityChange = { value ->
                producerProfileViewModel
                    .updateCity(value)
            },

            onDistrictChange = { value ->
                producerProfileViewModel
                    .updateDistrict(value)
            },

            onNeighborhoodChange = { value ->
                producerProfileViewModel
                    .updateNeighborhood(value)
            },

            onStreetChange = { value ->
                producerProfileViewModel
                    .updateStreet(value)
            },

            onBuildingNoChange = { value ->
                producerProfileViewModel
                    .updateBuildingNo(value)
            },

            onFloorChange = { value ->
                producerProfileViewModel
                    .updateFloor(value)
            },

            onApartmentNoChange = { value ->
                producerProfileViewModel
                    .updateApartmentNo(value)
            },

            onAddressNoteChange = { value ->
                producerProfileViewModel
                    .updateAddressNote(value)
            },

            onSelectLocationClick = {
                val currentLocation =
                    producerProfileUiState
                        .selectedLocation

                if (
                    currentLocation != null &&
                    currentLocation.isValid()
                ) {
                    backStackEntry
                        .savedStateHandle[
                        PRODUCER_PROFILE_MAP_INITIAL_LATITUDE
                    ] =
                        currentLocation.latitude

                    backStackEntry
                        .savedStateHandle[
                        PRODUCER_PROFILE_MAP_INITIAL_LONGITUDE
                    ] =
                        currentLocation.longitude
                }

                navController.navigate(
                    AppDestination
                        .ProducerProfileAddressMap
                        .route
                )
            },

            onDailyCapacityChange = { value ->
                producerProfileViewModel
                    .updateDailyCapacityText(value)
            },

            onAvailabilityChange = { value ->
                producerProfileViewModel
                    .updateAvailability(value)
            },

            onSaveClick = {
                producerProfileViewModel
                    .saveProfile()
            },

            onMessageShown = {
                producerProfileViewModel
                    .clearMessages()
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder
        .producerProfileAddressMapDestination(
    navController: NavHostController
) {
    composable(
        route =
            AppDestination
                .ProducerProfileAddressMap
                .route
    ) {
        val previousEntry =
            navController
                .previousBackStackEntry

        val initialLatitude =
            previousEntry
                ?.savedStateHandle
                ?.get<Double>(
                    PRODUCER_PROFILE_MAP_INITIAL_LATITUDE
                )

        val initialLongitude =
            previousEntry
                ?.savedStateHandle
                ?.get<Double>(
                    PRODUCER_PROFILE_MAP_INITIAL_LONGITUDE
                )

        val initialLocation =
            if (
                initialLatitude != null &&
                initialLongitude != null
            ) {
                SelectedLocation(
                    latitude =
                        initialLatitude,

                    longitude =
                        initialLongitude
                ).takeIf {
                    it.isValid()
                }
            } else {
                null
            }

        LaunchedEffect(
            previousEntry
        ) {
            previousEntry
                ?.savedStateHandle
                ?.remove<Double>(
                    PRODUCER_PROFILE_MAP_INITIAL_LATITUDE
                )

            previousEntry
                ?.savedStateHandle
                ?.remove<Double>(
                    PRODUCER_PROFILE_MAP_INITIAL_LONGITUDE
                )
        }

        LocationMapScreen(
            initialSelectedLocation =
                initialLocation,

            onBackClick = {
                navController
                    .popBackStack()
            },

            onConfirmLocation = {
                    latitude,
                    longitude ->

                val destinationEntry =
                    navController
                        .previousBackStackEntry

                if (destinationEntry != null) {
                    destinationEntry
                        .savedStateHandle[
                        PRODUCER_PROFILE_MAP_RESULT_LATITUDE
                    ] =
                        latitude

                    destinationEntry
                        .savedStateHandle[
                        PRODUCER_PROFILE_MAP_RESULT_LONGITUDE
                    ] =
                        longitude
                }

                navController
                    .popBackStack()
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}