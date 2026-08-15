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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.homemadefood.app.ui.customer.CustomerHomeScreen
import com.homemadefood.app.ui.customer.CustomerHomeViewModel
import com.homemadefood.app.ui.customer.CustomerHomeViewModelFactory

import com.homemadefood.app.ui.favorite.FavoritesScreen
import com.homemadefood.app.ui.favorite.FavoritesViewModel
import com.homemadefood.app.ui.favorite.FavoritesViewModelFactory

import com.homemadefood.app.ui.food.FoodDetailScreen
import com.homemadefood.app.ui.food.FoodDetailViewModel
import com.homemadefood.app.ui.food.FoodDetailViewModelFactory
import com.homemadefood.app.ui.address.AddAddressScreen
import com.homemadefood.app.ui.address.AddressFormViewModel
import com.homemadefood.app.ui.address.AddressFormViewModelFactory
import com.homemadefood.app.ui.address.AddressesScreen
import com.homemadefood.app.ui.address.AddressesViewModel
import com.homemadefood.app.ui.address.AddressesViewModelFactory
import com.homemadefood.app.ui.address.EditAddressScreen
import com.homemadefood.app.ui.address.EditAddressViewModel
import com.homemadefood.app.ui.address.EditAddressViewModelFactory
import com.homemadefood.app.ui.cart.CartScreen
import com.homemadefood.app.ui.cart.CartViewModel
import com.homemadefood.app.ui.cart.CartViewModelFactory
import com.homemadefood.app.ui.order.CreateOrderScreen
import com.homemadefood.app.ui.order.CreateOrderViewModel
import com.homemadefood.app.ui.order.CreateOrderViewModelFactory
import com.homemadefood.app.ui.order.OrderDetailScreen
import com.homemadefood.app.ui.order.OrderDetailViewModel
import com.homemadefood.app.ui.order.OrderDetailViewModelFactory
import com.homemadefood.app.ui.order.OrdersScreen
import com.homemadefood.app.ui.order.OrdersViewModel
import com.homemadefood.app.ui.order.OrdersViewModelFactory
import com.homemadefood.app.ui.recommendation.RecommendationScreen
import com.homemadefood.app.ui.recommendation.RecommendationViewModel
import com.homemadefood.app.ui.recommendation.RecommendationViewModelFactory
import com.homemadefood.app.ui.customer.CustomerProducerApplicationScreen
import com.homemadefood.app.ui.customer.CustomerProducerApplicationViewModel
import com.homemadefood.app.ui.customer.CustomerProducerApplicationViewModelFactory
import com.homemadefood.app.ui.customer.CustomerReviewsScreen
import com.homemadefood.app.ui.customer.CustomerReviewsViewModel
import com.homemadefood.app.ui.customer.CustomerReviewsViewModelFactory
import com.homemadefood.app.ui.auth.AuthViewModel
import com.homemadefood.app.ui.location.LocationMapScreen
import com.homemadefood.app.ui.address.SelectedLocation

private const val ADDRESS_MAP_RESULT_LATITUDE =
    "address_map_result_latitude"

private const val ADDRESS_MAP_RESULT_LONGITUDE =
    "address_map_result_longitude"

private const val ADDRESS_MAP_INITIAL_LATITUDE =
    "address_map_initial_latitude"

private const val ADDRESS_MAP_INITIAL_LONGITUDE =
    "address_map_initial_longitude"


fun NavGraphBuilder.customerNavGraph(
    navController: NavHostController,
    context: Context,
    authViewModel: AuthViewModel,
    onLogoutClick: () -> Unit
) {
    navigation(
        route = AppGraph.CUSTOMER,
        startDestination =
            AppDestination.CustomerHome.route
    ) {

        customerHomeDestination(
            navController = navController,
            authViewModel = authViewModel,
            onLogoutClick = onLogoutClick
        )

        foodDetailDestination(
            navController = navController,
            context = context
        )

        favoritesDestination(
            navController = navController,
            context = context
        )
        addressesDestination(
            navController = navController,
            context = context
        )

        addAddressDestination(
            navController = navController,
            context = context
        )
        addressMapDestination(
            navController = navController
        )


        editAddressDestination(
            navController = navController,
            context = context
        )
        cartDestination(
            navController = navController,
            context = context
        )

        createOrderDestination(
            navController = navController,
            context = context
        )
        ordersDestination(
            navController = navController,
            context = context
        )

        orderDetailDestination(
            navController = navController,
            context = context
        )
        recommendationDestination(
            navController = navController,
            context = context
        )
        customerProducerApplicationDestination(
            navController = navController,
            context = context,
            authViewModel = authViewModel
        )
        customerReviewsDestination(
            navController = navController,
            context = context
        )
    }
}

private fun NavGraphBuilder.customerHomeDestination(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onLogoutClick: () -> Unit
){
    composable(
        route = AppDestination.CustomerHome.route
    ) {
        val customerHomeViewModel:
                CustomerHomeViewModel =
            viewModel(
                factory =
                    CustomerHomeViewModelFactory()
            )

        val customerHomeUiState by
        customerHomeViewModel.uiState
            .collectAsStateWithLifecycle()
        val authUiState by
        authViewModel.uiState
            .collectAsStateWithLifecycle()

        CustomerHomeScreen(
            uiState = customerHomeUiState,

            onSearchQueryChange = { query ->
                customerHomeViewModel
                    .updateSearchQuery(query)
            },

            onSearchClick = {
                customerHomeViewModel
                    .searchFoods()
            },

            onCategoryClick = { categoryId ->
                customerHomeViewModel
                    .selectCategory(categoryId)
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

            onFoodClick = { foodId ->
                navController.navigate(
                    AppDestination.FoodDetail
                        .createRoute(foodId)
                )
            },

            onFavoritesClick = {
                navController.navigate(
                    AppDestination.Favorites.route
                )
            },

            onAddressesClick = {
                navController.navigate(
                    AppDestination.Addresses.route
                )
            },

            onLogoutClick = onLogoutClick,

            onCartClick = {
                navController.navigate(
                    AppDestination.Cart.route
                )
            },

            onOrdersClick = {
                navController.navigate(
                    AppDestination.Orders.route
                )
            },

            onRecommendationClick = {
                navController.navigate(
                    AppDestination.Recommendation.route
                )
            },
            onProducerApplicationClick = {
                navController.navigate(
                    AppDestination
                        .CustomerProducerApplication
                        .route
                )
            },
            canUseProducerMode =
                authUiState.canUseProducerMode,

            producerVerificationStatus =
                authUiState
                    .producerVerificationStatus,

            onProducerModeClick = {
                authViewModel
                    .switchToProducerMode()
            },
            onReviewsClick = {
                navController.navigate(
                    AppDestination.CustomerReviews.route
                )
            },


            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.foodDetailDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.FoodDetail.route,

        arguments = listOf(
            navArgument(
                AppDestination.FoodDetail
                    .FOOD_ID_ARGUMENT
            ) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->

        val foodId =
            backStackEntry.arguments
                ?.getInt(
                    AppDestination.FoodDetail
                        .FOOD_ID_ARGUMENT
                )
                ?: return@composable

        val foodDetailViewModel:
                FoodDetailViewModel =
            viewModel(
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
                navController.popBackStack()
            },

            onRetryClick = {
                foodDetailViewModel.loadFood()
            },

            onFavoriteClick = {
                foodDetailViewModel.toggleFavorite()
            },

            onAddToCartClick = {
                foodDetailViewModel.addToCart()
            },

            onIncreaseCartClick = {
                foodDetailViewModel
                    .increaseCartQuantity()
            },

            onDecreaseCartClick = {
                foodDetailViewModel
                    .decreaseCartQuantity()
            },

            onGoToCartClick = {
                navController.navigate(
                    AppDestination.Cart.route
                ) {
                    launchSingleTop = true
                }
            },

            onFavoriteMessageShown = {
                foodDetailViewModel
                    .clearFavoriteMessage()
            },

            onCartMessageShown = {
                foodDetailViewModel
                    .clearCartMessage()
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.favoritesDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.Favorites.route
    ) {
        val favoritesViewModel:
                FavoritesViewModel =
            viewModel(
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
                navController.popBackStack()
            },

            onRetryClick = {
                favoritesViewModel.loadFavorites()
            },

            onRemoveFavoriteClick = { foodId ->
                favoritesViewModel
                    .removeFavorite(
                        foodId = foodId
                    )
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.addressesDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.Addresses.route
    ) {
        val addressesViewModel:
                AddressesViewModel =
            viewModel(
                factory =
                    AddressesViewModelFactory(
                        context = context
                    )
            )

        val addressesUiState by
        addressesViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            addressesViewModel.loadAddresses()
        }

        AddressesScreen(
            uiState = addressesUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                addressesViewModel.loadAddresses()
            },

            onAddAddressClick = {
                navController.navigate(
                    AppDestination.AddAddress.route
                )
            },

            onDeleteAddressClick = { addressId ->
                addressesViewModel.deleteAddress(
                    addressId = addressId
                )
            },

            onEditAddressClick = { addressId ->
                navController.navigate(
                    AppDestination.EditAddress
                        .createRoute(addressId)
                )
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.addAddressDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.AddAddress.route
    ) { backStackEntry ->

        val addressFormViewModel:
                AddressFormViewModel =
            viewModel(
                factory =
                    AddressFormViewModelFactory(
                        context = context
                    )
            )

        val addressFormUiState by
        addressFormViewModel.uiState
            .collectAsStateWithLifecycle()
        val selectedLatitude by
        backStackEntry.savedStateHandle
            .getStateFlow<Double?>(
                ADDRESS_MAP_RESULT_LATITUDE,
                null
            )
            .collectAsStateWithLifecycle()

        val selectedLongitude by
        backStackEntry.savedStateHandle
            .getStateFlow<Double?>(
                ADDRESS_MAP_RESULT_LONGITUDE,
                null
            )
            .collectAsStateWithLifecycle()

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
                addressFormViewModel
                    .updateSelectedLocation(
                        latitude = latitude,
                        longitude = longitude
                    )

                backStackEntry
                    .savedStateHandle
                    .remove<Double>(
                        ADDRESS_MAP_RESULT_LATITUDE
                    )

                backStackEntry
                    .savedStateHandle
                    .remove<Double>(
                        ADDRESS_MAP_RESULT_LONGITUDE
                    )
            }
        }

        LaunchedEffect(
            addressFormUiState.isSaved
        ) {
            if (addressFormUiState.isSaved) {
                addressFormViewModel
                    .resetSavedState()

                navController.popBackStack()
            }
        }

        AddAddressScreen(
            uiState = addressFormUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onTitleChange = { value ->
                addressFormViewModel
                    .updateTitle(value)
            },

            onCityChange = { value ->
                addressFormViewModel
                    .updateCity(value)
            },

            onDistrictChange = { value ->
                addressFormViewModel
                    .updateDistrict(value)
            },

            onNeighborhoodChange = { value ->
                addressFormViewModel
                    .updateNeighborhood(value)
            },

            onStreetChange = { value ->
                addressFormViewModel
                    .updateStreet(value)
            },

            onBuildingNoChange = { value ->
                addressFormViewModel
                    .updateBuildingNo(value)
            },

            onFloorChange = { value ->
                addressFormViewModel
                    .updateFloor(value)
            },

            onApartmentNoChange = { value ->
                addressFormViewModel
                    .updateApartmentNo(value)
            },

            onAddressNoteChange = { value ->
                addressFormViewModel
                    .updateAddressNote(value)
            },

            onIsDefaultChange = { value ->
                addressFormViewModel
                    .updateIsDefault(value)
            },

            onSelectLocationClick = {
                navController.navigate(
                    AppDestination.AddressMap.route
                )
            },

            onSaveClick = {
                addressFormViewModel
                    .saveAddress()
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.addressMapDestination(
    navController: NavHostController
) {
    composable(
        route =
            AppDestination.AddressMap.route
    ) {
        val previousEntry =
            navController
                .previousBackStackEntry

        val initialLatitude =
            previousEntry
                ?.savedStateHandle
                ?.get<Double>(
                    ADDRESS_MAP_INITIAL_LATITUDE
                )

        val initialLongitude =
            previousEntry
                ?.savedStateHandle
                ?.get<Double>(
                    ADDRESS_MAP_INITIAL_LONGITUDE
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
                    ADDRESS_MAP_INITIAL_LATITUDE
                )

            previousEntry
                ?.savedStateHandle
                ?.remove<Double>(
                    ADDRESS_MAP_INITIAL_LONGITUDE
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

                if (
                    destinationEntry != null
                ) {
                    destinationEntry
                        .savedStateHandle[
                        ADDRESS_MAP_RESULT_LATITUDE
                    ] =
                        latitude

                    destinationEntry
                        .savedStateHandle[
                        ADDRESS_MAP_RESULT_LONGITUDE
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



private fun NavGraphBuilder.editAddressDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.EditAddress.route,

        arguments = listOf(
            navArgument(
                AppDestination.EditAddress
                    .ADDRESS_ID_ARGUMENT
            ) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->

        val addressId =
            backStackEntry.arguments
                ?.getInt(
                    AppDestination.EditAddress
                        .ADDRESS_ID_ARGUMENT
                )
                ?: return@composable

        val editAddressViewModel:
                EditAddressViewModel =
            viewModel(
                factory =
                    EditAddressViewModelFactory(
                        context = context,
                        addressId = addressId
                    )
            )

        val editAddressUiState by
        editAddressViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(
            editAddressUiState.isSaved
        ) {
            if (editAddressUiState.isSaved) {
                editAddressViewModel
                    .resetSavedState()

                navController.popBackStack()
            }
        }
        val selectedLatitude by
        backStackEntry
            .savedStateHandle
            .getStateFlow<Double?>(
                ADDRESS_MAP_RESULT_LATITUDE,
                null
            )
            .collectAsStateWithLifecycle()

        val selectedLongitude by
        backStackEntry
            .savedStateHandle
            .getStateFlow<Double?>(
                ADDRESS_MAP_RESULT_LONGITUDE,
                null
            )
            .collectAsStateWithLifecycle()

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
                editAddressViewModel
                    .updateSelectedLocation(
                        latitude = latitude,
                        longitude = longitude
                    )

                backStackEntry
                    .savedStateHandle
                    .remove<Double>(
                        ADDRESS_MAP_RESULT_LATITUDE
                    )

                backStackEntry
                    .savedStateHandle
                    .remove<Double>(
                        ADDRESS_MAP_RESULT_LONGITUDE
                    )
            }
        }

        EditAddressScreen(
            uiState = editAddressUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                editAddressViewModel
                    .loadAddress()
            },

            onTitleChange = { value ->
                editAddressViewModel
                    .updateTitle(value)
            },

            onCityChange = { value ->
                editAddressViewModel
                    .updateCity(value)
            },

            onDistrictChange = { value ->
                editAddressViewModel
                    .updateDistrict(value)
            },

            onNeighborhoodChange = { value ->
                editAddressViewModel
                    .updateNeighborhood(value)
            },

            onStreetChange = { value ->
                editAddressViewModel
                    .updateStreet(value)
            },

            onBuildingNoChange = { value ->
                editAddressViewModel
                    .updateBuildingNo(value)
            },

            onFloorChange = { value ->
                editAddressViewModel
                    .updateFloor(value)
            },

            onApartmentNoChange = { value ->
                editAddressViewModel
                    .updateApartmentNo(value)
            },

            onAddressNoteChange = { value ->
                editAddressViewModel
                    .updateAddressNote(value)
            },

            onIsDefaultChange = { value ->
                editAddressViewModel
                    .updateIsDefault(value)
            },
            onSelectLocationClick = {
                val currentLocation =
                    editAddressUiState
                        .selectedLocation

                if (
                    currentLocation != null &&
                    currentLocation.isValid()
                ) {
                    backStackEntry
                        .savedStateHandle[
                        ADDRESS_MAP_INITIAL_LATITUDE
                    ] =
                        currentLocation.latitude

                    backStackEntry
                        .savedStateHandle[
                        ADDRESS_MAP_INITIAL_LONGITUDE
                    ] =
                        currentLocation.longitude
                }

                navController.navigate(
                    AppDestination.AddressMap.route
                )
            },

            onSaveClick = {
                editAddressViewModel
                    .updateAddress()
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.cartDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.Cart.route
    ) {
        val cartViewModel:
                CartViewModel =
            viewModel(
                factory =
                    CartViewModelFactory(
                        context = context
                    )
            )

        val cartUiState by
        cartViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            cartViewModel.loadCart()
        }

        CartScreen(
            uiState = cartUiState,

            onBackClick = {
                navController.navigate(
                    AppDestination.CustomerHome.route
                ) {
                    popUpTo(
                        AppDestination.CustomerHome.route
                    ) {
                        inclusive = false
                    }

                    launchSingleTop = true
                }
            },

            onRetryClick = {
                cartViewModel.loadCart()
            },

            onIncreaseQuantityClick = {
                    cartItemId,
                    currentQuantity ->

                cartViewModel.updateQuantity(
                    cartItemId = cartItemId,
                    newQuantity =
                        currentQuantity + 1
                )
            },

            onDecreaseQuantityClick = {
                    cartItemId,
                    currentQuantity ->

                cartViewModel.updateQuantity(
                    cartItemId = cartItemId,
                    newQuantity =
                        currentQuantity - 1
                )
            },

            onRemoveItemClick = { cartItemId ->
                cartViewModel.removeItem(
                    cartItemId = cartItemId
                )
            },

            onClearCartClick = {
                cartViewModel.clearCart()
            },

            onCreateOrderClick = {
                navController.navigate(
                    AppDestination.CreateOrder.route
                )
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.createOrderDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.CreateOrder.route
    ) {
        val createOrderViewModel:
                CreateOrderViewModel =
            viewModel(
                factory =
                    CreateOrderViewModelFactory(
                        context = context
                    )
            )

        val createOrderUiState by
        createOrderViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            createOrderViewModel.loadData()
        }

        CreateOrderScreen(
            uiState = createOrderUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                createOrderViewModel.loadData()
            },

            onAddressSelected = { addressId ->
                createOrderViewModel.selectAddress(
                    addressId = addressId
                )
            },

            onPaymentMethodSelected = {
                    paymentMethod ->

                createOrderViewModel
                    .selectPaymentMethod(
                        paymentMethod
                    )
            },

            onCustomerNoteChange = { value ->
                createOrderViewModel
                    .updateCustomerNote(value)
            },

            onCreateOrderClick = {
                createOrderViewModel
                    .createOrder()
            },

            onReturnHomeClick = {
                createOrderViewModel
                    .resetCreatedOrder()

                navController.navigate(
                    AppDestination.CustomerHome.route
                ) {
                    popUpTo(
                        AppDestination.CustomerHome.route
                    ) {
                        inclusive = false
                    }

                    launchSingleTop = true
                }
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.orderDetailDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.OrderDetail.route,

        arguments = listOf(
            navArgument(
                AppDestination.OrderDetail
                    .ORDER_ID_ARGUMENT
            ) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->

        val orderId =
            backStackEntry.arguments
                ?.getInt(
                    AppDestination.OrderDetail
                        .ORDER_ID_ARGUMENT
                )
                ?: return@composable

        val orderDetailViewModel:
                OrderDetailViewModel =
            viewModel(
                factory =
                    OrderDetailViewModelFactory(
                        context = context,
                        orderId = orderId
                    )
            )

        val orderDetailUiState by
        orderDetailViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(orderId) {
            orderDetailViewModel.loadOrder()
        }

        OrderDetailScreen(
            uiState = orderDetailUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                orderDetailViewModel.loadOrder()
            },

            onCancelOrderClick = {
                orderDetailViewModel.cancelOrder()
            },
            onShowReviewFormClick = {
                orderDetailViewModel
                    .showReviewForm()
            },

            onHideReviewFormClick = {
                orderDetailViewModel
                    .hideReviewForm()
            },

            onRatingSelected = { rating ->
                orderDetailViewModel
                    .selectRating(rating)
            },

            onReviewCommentChange = { value ->
                orderDetailViewModel
                    .updateReviewComment(value)
            },

            onSubmitReviewClick = {
                orderDetailViewModel
                    .submitReview()
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.ordersDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.Orders.route
    ) {
        val ordersViewModel:
                OrdersViewModel =
            viewModel(
                factory =
                    OrdersViewModelFactory(
                        context = context
                    )
            )

        val ordersUiState by
        ordersViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            ordersViewModel.loadOrders()
        }

        OrdersScreen(
            uiState = ordersUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                ordersViewModel.loadOrders()
            },

            onCancelOrderClick = { orderId ->
                ordersViewModel.cancelOrder(
                    orderId = orderId
                )
            },

            onOrderClick = { orderId ->
                navController.navigate(
                    AppDestination.OrderDetail
                        .createRoute(orderId)
                )
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.recommendationDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route = AppDestination.Recommendation.route
    ) {
        val recommendationViewModel:
                RecommendationViewModel =
            viewModel(
                factory =
                    RecommendationViewModelFactory(
                        context = context
                    )
            )

        val recommendationUiState by
        recommendationViewModel.uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            recommendationViewModel
                .loadAddresses()
        }

        RecommendationScreen(
            uiState = recommendationUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryAddressesClick = {
                recommendationViewModel
                    .loadAddresses()
            },

            onManageAddressesClick = {
                navController.navigate(
                    AppDestination.Addresses.route
                )
            },

            onSearchTextChange = { value ->
                recommendationViewModel
                    .updateSearchText(value)
            },

            onQuantityTextChange = { value ->
                recommendationViewModel
                    .updateQuantityText(value)
            },

            onAddressSelected = { addressId ->
                recommendationViewModel
                    .selectAddress(
                        addressId = addressId
                    )
            },

            onSearchClick = {
                recommendationViewModel
                    .searchRecommendations()
            },

            onSelectRecommendationClick = { foodId ->
                recommendationViewModel
                    .selectRecommendation(
                        foodId = foodId
                    )
            },

            onAddSelectedToCartClick = {
                recommendationViewModel
                    .addSelectedRecommendationToCart()
            },

            onGoToCartClick = {
                navController.navigate(
                    AppDestination.Cart.route
                )
            },

            onOpenFoodClick = { foodId ->
                navController.navigate(
                    AppDestination.FoodDetail
                        .createRoute(foodId)
                )
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder
        .customerProducerApplicationDestination(
    navController: NavHostController,
    context: Context,
    authViewModel: AuthViewModel
) {
    composable(
        route =
            AppDestination
                .CustomerProducerApplication
                .route
    ) { backStackEntry ->

        val applicationViewModel:
                CustomerProducerApplicationViewModel =
            viewModel(
                factory =
                    CustomerProducerApplicationViewModelFactory(
                        context = context
                    )
            )

        val applicationUiState by
        applicationViewModel.uiState
            .collectAsStateWithLifecycle()

        val selectedLatitude by
        backStackEntry
            .savedStateHandle
            .getStateFlow<Double?>(
                ADDRESS_MAP_RESULT_LATITUDE,
                null
            )
            .collectAsStateWithLifecycle()

        val selectedLongitude by
        backStackEntry
            .savedStateHandle
            .getStateFlow<Double?>(
                ADDRESS_MAP_RESULT_LONGITUDE,
                null
            )
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            /*
             * Haritadan geri dönüldüğünde ViewModel'deki
             * form state'i zaten korunur.
             *
             * Başvuruyu yalnızca ekran ilk kez açılırken
             * yükle. Böylece seçilen işletme konumu
             * tekrar loadApplication tarafından silinmez.
             */
            if (applicationUiState.isLoading) {
                applicationViewModel
                    .loadApplication()
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
                applicationViewModel
                    .updateSelectedLocation(
                        latitude = latitude,
                        longitude = longitude
                    )

                backStackEntry
                    .savedStateHandle
                    .remove<Double>(
                        ADDRESS_MAP_RESULT_LATITUDE
                    )

                backStackEntry
                    .savedStateHandle
                    .remove<Double>(
                        ADDRESS_MAP_RESULT_LONGITUDE
                    )
            }
        }

        LaunchedEffect(
            applicationUiState
                .application
                ?.verificationStatus
        ) {
            val verificationStatus =
                applicationUiState
                    .application
                    ?.verificationStatus

            if (
                verificationStatus.equals(
                    "Approved",
                    ignoreCase = true
                )
            ) {
                authViewModel.refreshProfile()
            }
        }

        CustomerProducerApplicationScreen(
            uiState = applicationUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                applicationViewModel
                    .loadApplication()
            },

            onBusinessNameChange = { value ->
                applicationViewModel
                    .updateBusinessName(value)
            },

            onDescriptionChange = { value ->
                applicationViewModel
                    .updateDescription(value)
            },

            onBusinessImageSelected = { uri ->
                applicationViewModel
                    .updateBusinessImage(uri)
            },

            onRemoveBusinessImage = {
                applicationViewModel
                    .removeSelectedBusinessImage()
            },

            onCityChange = { value ->
                applicationViewModel
                    .updateCity(value)
            },

            onDistrictChange = { value ->
                applicationViewModel
                    .updateDistrict(value)
            },

            onNeighborhoodChange = { value ->
                applicationViewModel
                    .updateNeighborhood(value)
            },

            onStreetChange = { value ->
                applicationViewModel
                    .updateStreet(value)
            },

            onBuildingNoChange = { value ->
                applicationViewModel
                    .updateBuildingNo(value)
            },

            onFloorChange = { value ->
                applicationViewModel
                    .updateFloor(value)
            },

            onApartmentNoChange = { value ->
                applicationViewModel
                    .updateApartmentNo(value)
            },

            onAddressNoteChange = { value ->
                applicationViewModel
                    .updateAddressNote(value)
            },

            onSelectLocationClick = {
                val currentLocation =
                    applicationUiState
                        .selectedLocation

                if (
                    currentLocation != null &&
                    currentLocation.isValid()
                ) {
                    backStackEntry
                        .savedStateHandle[
                        ADDRESS_MAP_INITIAL_LATITUDE
                    ] =
                        currentLocation.latitude

                    backStackEntry
                        .savedStateHandle[
                        ADDRESS_MAP_INITIAL_LONGITUDE
                    ] =
                        currentLocation.longitude
                }

                navController.navigate(
                    AppDestination.AddressMap.route
                )
            },

            onDailyCapacityChange = { value ->
                applicationViewModel
                    .updateDailyCapacityText(value)
            },

            onSubmitClick = {
                applicationViewModel
                    .submitApplication()
            },

            onShowReapplicationFormClick = {
                applicationViewModel
                    .showReapplicationForm()
            },

            onHideReapplicationFormClick = {
                applicationViewModel
                    .hideReapplicationForm()
            },

            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun NavGraphBuilder.customerReviewsDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .CustomerReviews
                .route
    ) {
        val customerReviewsViewModel:
                CustomerReviewsViewModel =
            viewModel(
                factory =
                    CustomerReviewsViewModelFactory(
                        context = context
                    )
            )

        val customerReviewsUiState by
        customerReviewsViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            customerReviewsViewModel
                .loadReviews()
        }

        CustomerReviewsScreen(
            uiState =
                customerReviewsUiState,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                customerReviewsViewModel
                    .loadReviews()
            },

            onDeleteReviewClick = { review ->
                customerReviewsViewModel
                    .requestDeleteReview(review)
            },

            onDismissDeleteDialog = {
                customerReviewsViewModel
                    .dismissDeleteDialog()
            },

            onConfirmDeleteReview = {
                customerReviewsViewModel
                    .confirmDeleteReview()
            },

            onMessageShown = {
                customerReviewsViewModel
                    .clearMessages()
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}