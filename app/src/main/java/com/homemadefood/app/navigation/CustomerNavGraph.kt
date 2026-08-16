package com.homemadefood.app.navigation

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.homemadefood.app.ui.customer.CustomerAccountScreen
import com.homemadefood.app.ui.customer.CustomerExploreScreen
import com.homemadefood.app.ui.customer.CustomerHomeScreen
import com.homemadefood.app.ui.customer.CustomerProfileScreen
import com.homemadefood.app.ui.customer.CustomerPhoneVerificationScreen
import com.homemadefood.app.ui.customer.CustomerPhoneVerificationViewModel
import com.homemadefood.app.ui.customer.CustomerPhoneVerificationViewModelFactory
import com.homemadefood.app.ui.customer.CustomerProfileViewModel
import com.homemadefood.app.ui.customer.CustomerProfileViewModelFactory
import com.homemadefood.app.ui.customer.CustomerHomeViewModel
import com.homemadefood.app.ui.customer.CustomerHomeViewModelFactory
import com.homemadefood.app.ui.customer.StorefrontMenuScreen
import com.homemadefood.app.ui.customer.StorefrontMenuViewModel
import com.homemadefood.app.ui.customer.StorefrontMenuViewModelFactory
import com.homemadefood.app.ui.customer.navigation.CustomerRootScaffold

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
import com.homemadefood.app.data.model.ProducerApplicationStatus

private const val ADDRESS_MAP_RESULT_LATITUDE =
    "address_map_result_latitude"

private const val ADDRESS_MAP_RESULT_LONGITUDE =
    "address_map_result_longitude"

private const val ADDRESS_MAP_INITIAL_LATITUDE =
    "address_map_initial_latitude"

private const val ADDRESS_MAP_INITIAL_LONGITUDE =
    "address_map_initial_longitude"

private const val PHONE_VERIFICATION_RESULT =
    "phone_verification_result"


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
            context = context
        )

        customerExploreDestination(
            navController = navController
        )

        customerAccountDestination(
            navController = navController,
            authViewModel = authViewModel,
            onLogoutClick = onLogoutClick
        )

        customerProfileDestination(
            navController = navController,
            context = context
        )

        customerPhoneVerificationDestination(
            navController = navController,
            context = context
        )

        storefrontMenuDestination(
            navController = navController,
            context = context
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

private fun navigateToCustomerRoot(
    navController: NavHostController,
    route: String
) {
    val currentRoute =
        navController
            .currentDestination
            ?.route

    if (currentRoute == route) {
        return
    }

    navController.navigate(route) {
        popUpTo(
            AppDestination
                .CustomerHome
                .route
        ) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }
}

private fun NavGraphBuilder.customerHomeDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .CustomerHome
                .route
    ) {
        val customerHomeViewModel:
                CustomerHomeViewModel =
            viewModel(
                factory =
                    CustomerHomeViewModelFactory(
                        context = context
                    )
            )

        val customerHomeUiState by
        customerHomeViewModel
            .uiState
            .collectAsStateWithLifecycle()

        val cartViewModel:
                CartViewModel =
            viewModel(
                factory =
                    CartViewModelFactory(
                        context = context
                    )
            )

        val cartUiState by
        cartViewModel
            .uiState
            .collectAsStateWithLifecycle()

        /*
         * Home'a her geri dönüşte sepeti yeniler.
         * Böylece FoodDetail/Cart ekranında miktar değiştiyse
         * sağ üst badge eski sayıda kalmaz.
         */
        val lifecycleOwner =
            LocalLifecycleOwner.current

        DisposableEffect(
            lifecycleOwner,
            cartViewModel,
            customerHomeViewModel
        ) {
            /*
             * C4D FIX:
             *
             * Home composable'ı başka bir destination'a gidince
             * composition'dan çıkabilir. Önceki
             * hasPassedInitialResume local değişkeni bu durumda
             * yeniden false oluyordu.
             *
             * Sonuç:
             * Adreslerim / Yeni Adres / Adres Düzenle ekranından
             * Home'a dönüldüğünde ilk ON_RESUME tekrar "ilk açılış"
             * sanılıyor ve loadDeliveryAddresses() atlanıyordu.
             *
             * Çözüm:
             * Home her ON_RESUME olduğunda cart ve delivery address
             * verilerini server'dan yeniler.
             *
             * HomeViewModel.init{} ilk açılışta da adresleri yüklediği
             * için ilk girişte fazladan bir GET /api/Address olabilir.
             * Bu küçük maliyet, stale adres göstermemekten daha güvenli.
             * C13'te request dedup/loading standardizasyonu sırasında
             * istersek optimize ederiz.
             */
            val observer =
                LifecycleEventObserver {
                        _,
                        event ->

                    if (
                        event ==
                        Lifecycle.Event.ON_RESUME
                    ) {
                        cartViewModel
                            .loadCart()

                        customerHomeViewModel
                            .loadDeliveryAddresses()
                    }
                }

            lifecycleOwner.lifecycle
                .addObserver(
                    observer
                )

            onDispose {
                lifecycleOwner.lifecycle
                    .removeObserver(
                        observer
                    )
            }
        }

        CustomerRootScaffold(
            selectedRoute =
                AppDestination
                    .CustomerHome
                    .route,

            onBottomDestinationClick = { route ->
                navigateToCustomerRoot(
                    navController =
                        navController,

                    route =
                        route
                )
            }
        ) { innerPadding ->

            CustomerHomeScreen(
                uiState =
                    customerHomeUiState,

                onSearchQueryChange = { query ->
                    customerHomeViewModel
                        .updateSearchQuery(
                            query
                        )
                },

                onSearchClick = {
                    customerHomeViewModel
                        .searchStorefronts()
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

                onRetryDeliveryAddressesClick = {
                    customerHomeViewModel
                        .loadDeliveryAddresses()
                },

                onDeliveryAddressSelected = {
                        addressId ->

                    customerHomeViewModel
                        .selectDeliveryAddress(
                            addressId
                        )
                },

                onAddAddressClick = {
                    navController.navigate(
                        AppDestination
                            .AddAddress
                            .route
                    )
                },

                onManageAddressesClick = {
                    navController.navigate(
                        AppDestination
                            .Addresses
                            .route
                    )
                },

                onRetryCategoriesClick = {
                    customerHomeViewModel
                        .loadCategories()
                },

                onRetryStorefrontsClick = {
                    customerHomeViewModel
                        .loadStorefronts()
                },

                onStorefrontClick = {
                        producerProfileId ->

                    navController.navigate(
                        AppDestination
                            .StorefrontMenu
                            .createRoute(
                                producerProfileId
                            )
                    )
                },

                cartTotalQuantity =
                    cartUiState
                        .cart
                        ?.totalQuantity
                        ?: 0,

                onCartClick = {
                    navController.navigate(
                        AppDestination
                            .Cart
                            .route
                    )
                },

                onRecommendationClick = {
                    navController.navigate(
                        AppDestination
                            .Recommendation
                            .route
                    )
                },

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding
                        )
            )
        }
    }
}

private fun NavGraphBuilder.customerExploreDestination(
    navController: NavHostController
) {
    composable(
        route =
            AppDestination
                .CustomerExplore
                .route
    ) {
        CustomerRootScaffold(
            selectedRoute =
                AppDestination
                    .CustomerExplore
                    .route,

            onBottomDestinationClick = { route ->
                navigateToCustomerRoot(
                    navController =
                        navController,

                    route =
                        route
                )
            }
        ) { innerPadding ->
            CustomerExploreScreen(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding
                        )
            )
        }
    }
}

private fun NavGraphBuilder.customerAccountDestination(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onLogoutClick: () -> Unit
) {
    composable(
        route =
            AppDestination
                .CustomerAccount
                .route
    ) {
        val authUiState by
        authViewModel.uiState
            .collectAsStateWithLifecycle()

        CustomerRootScaffold(
            selectedRoute =
                AppDestination
                    .CustomerAccount
                    .route,

            onBottomDestinationClick = { route ->
                navigateToCustomerRoot(
                    navController =
                        navController,

                    route =
                        route
                )
            }
        ) { innerPadding ->
            CustomerAccountScreen(
                canUseProducerMode =
                    authUiState
                        .canUseProducerMode,

                producerVerificationStatus =
                    authUiState
                        .producerVerificationStatus,

                onProfileClick = {
                    navController.navigate(
                        AppDestination
                            .CustomerProfile
                            .route
                    )
                },

                onAddressesClick = {
                    navController.navigate(
                        AppDestination
                            .Addresses
                            .route
                    )
                },

                onFavoritesClick = {
                    navController.navigate(
                        AppDestination
                            .Favorites
                            .route
                    )
                },

                onReviewsClick = {
                    navController.navigate(
                        AppDestination
                            .CustomerReviews
                            .route
                    )
                },

                onProducerApplicationClick = {
                    navController.navigate(
                        AppDestination
                            .CustomerProducerApplication
                            .route
                    )
                },

                onProducerModeClick = {
                    authViewModel
                        .switchToProducerMode()
                },

                onLogoutClick =
                    onLogoutClick,

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding
                        )
            )
        }
    }
}

private fun NavGraphBuilder.customerProfileDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .CustomerProfile
                .route
    ) { backStackEntry ->

        val customerProfileViewModel:
                CustomerProfileViewModel =
            viewModel(
                factory =
                    CustomerProfileViewModelFactory(
                        context = context
                    )
            )

        val customerProfileUiState by
        customerProfileViewModel
            .uiState
            .collectAsStateWithLifecycle()

        val phoneVerificationResult by
        backStackEntry
            .savedStateHandle
            .getStateFlow(
                PHONE_VERIFICATION_RESULT,
                false
            )
            .collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            if (
                customerProfileUiState
                    .isLoading
            ) {
                customerProfileViewModel
                    .loadProfile()
            }
        }

        /*
         * Telefon doğrulama/değiştirme ekranı başarılı
         * olduğunda Profil ekranına result bırakır.
         * Böylece Profile ViewModel yeniden backend'den
         * güncel phone/isPhoneVerified değerlerini alır.
         */
        LaunchedEffect(
            phoneVerificationResult
        ) {
            if (phoneVerificationResult) {
                customerProfileViewModel
                    .loadProfile()

                backStackEntry
                    .savedStateHandle[
                    PHONE_VERIFICATION_RESULT
                ] = false
            }
        }

        CustomerProfileScreen(
            uiState =
                customerProfileUiState,

            onBackClick = {
                navController
                    .popBackStack()
            },

            onRetryClick = {
                customerProfileViewModel
                    .loadProfile()
            },

            onStartEditingClick = {
                customerProfileViewModel
                    .startEditing()
            },

            onCancelEditingClick = {
                customerProfileViewModel
                    .cancelEditing()
            },

            onFullNameChange = { value ->
                customerProfileViewModel
                    .updateFullName(
                        value
                    )
            },

            onSaveClick = {
                customerProfileViewModel
                    .saveProfile()
            },

            onPhoneVerificationClick = {
                navController.navigate(
                    AppDestination
                        .CustomerPhoneVerification
                        .route
                )
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.customerPhoneVerificationDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination
                .CustomerPhoneVerification
                .route
    ) {
        val customerPhoneVerificationViewModel:
                CustomerPhoneVerificationViewModel =
            viewModel(
                factory =
                    CustomerPhoneVerificationViewModelFactory(
                        context = context
                    )
            )

        val phoneUiState by
        customerPhoneVerificationViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(
            phoneUiState
                .isVerificationCompleted
        ) {
            if (
                phoneUiState
                    .isVerificationCompleted
            ) {
                /*
                 * Başarı mesajının kullanıcı tarafından
                 * fark edilmesi için çok kısa bekleme.
                 */
                kotlinx.coroutines.delay(
                    650
                )

                navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        PHONE_VERIFICATION_RESULT,
                        true
                    )

                navController
                    .popBackStack()
            }
        }

        CustomerPhoneVerificationScreen(
            uiState =
                phoneUiState,

            onBackClick = {
                navController
                    .popBackStack()
            },

            onPhoneChange = { value ->
                customerPhoneVerificationViewModel
                    .updatePhone(
                        value
                    )
            },

            onCodeChange = { value ->
                customerPhoneVerificationViewModel
                    .updateCode(
                        value
                    )
            },

            onRequestCodeClick = {
                customerPhoneVerificationViewModel
                    .requestCode()
            },

            onVerifyClick = {
                customerPhoneVerificationViewModel
                    .verifyCode()
            },

            onEditPhoneClick = {
                customerPhoneVerificationViewModel
                    .editPhoneNumber()
            },

            modifier =
                Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.storefrontMenuDestination(
    navController: NavHostController,
    context: Context
) {
    composable(
        route =
            AppDestination.StorefrontMenu.route,

        arguments = listOf(
            navArgument(
                AppDestination
                    .StorefrontMenu
                    .PRODUCER_PROFILE_ID_ARGUMENT
            ) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->

        val producerProfileId =
            backStackEntry.arguments
                ?.getInt(
                    AppDestination
                        .StorefrontMenu
                        .PRODUCER_PROFILE_ID_ARGUMENT
                )
                ?: return@composable

        val storefrontMenuViewModel:
                StorefrontMenuViewModel =
            viewModel(
                factory =
                    StorefrontMenuViewModelFactory(
                        producerProfileId =
                            producerProfileId
                    )
            )

        val storefrontMenuUiState by
        storefrontMenuViewModel
            .uiState
            .collectAsStateWithLifecycle()

        val cartViewModel:
                CartViewModel =
            viewModel(
                factory =
                    CartViewModelFactory(
                        context = context
                    )
            )

        val cartUiState by
        cartViewModel
            .uiState
            .collectAsStateWithLifecycle()

        val lifecycleOwner =
            LocalLifecycleOwner.current

        DisposableEffect(
            lifecycleOwner,
            cartViewModel
        ) {
            val observer =
                LifecycleEventObserver {
                        _,
                        event ->

                    if (
                        event ==
                        Lifecycle.Event.ON_RESUME
                    ) {
                        cartViewModel
                            .loadCart()
                    }
                }

            lifecycleOwner.lifecycle
                .addObserver(
                    observer
                )

            onDispose {
                lifecycleOwner.lifecycle
                    .removeObserver(
                        observer
                    )
            }
        }

        LaunchedEffect(
            producerProfileId
        ) {
            storefrontMenuViewModel
                .loadMenu()
        }

        StorefrontMenuScreen(
            uiState =
                storefrontMenuUiState,

            cartTotalQuantity =
                cartUiState
                    .cart
                    ?.totalQuantity
                    ?: 0,

            onBackClick = {
                navController
                    .popBackStack()
            },

            onCartClick = {
                navController.navigate(
                    AppDestination
                        .Cart
                        .route
                ) {
                    launchSingleTop = true
                }
            },

            onRetryClick = {
                storefrontMenuViewModel
                    .loadMenu()
            },

            onFoodClick = { foodId ->
                navController.navigate(
                    AppDestination
                        .FoodDetail
                        .createRoute(
                            foodId
                        )
                )
            },

            modifier =
                Modifier.fillMaxSize()
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

        val cartViewModel:
                CartViewModel =
            viewModel(
                factory =
                    CartViewModelFactory(
                        context = context
                    )
            )

        val cartUiState by
        cartViewModel
            .uiState
            .collectAsStateWithLifecycle()

        LaunchedEffect(foodId) {
            foodDetailViewModel.loadFood()
            cartViewModel.loadCart()
        }

        /*
         * FoodDetail kendi FoodDetailViewModel'i ile sepete
         * ekleme/miktar işlemlerini yapar.
         *
         * İşlem bittiğinde global CartResponse tekrar yüklenir;
         * böylece sağ üst badge yalnız bu yemeğin miktarını değil,
         * sepetteki TOPLAM adedi gösterir.
         */
        LaunchedEffect(
            foodDetailUiState.cartQuantity,
            foodDetailUiState.isCartActionLoading
        ) {
            if (
                !foodDetailUiState
                    .isCartActionLoading
            ) {
                cartViewModel
                    .loadCart()
            }
        }

        FoodDetailScreen(
            uiState = foodDetailUiState,

            cartTotalQuantity =
                cartUiState
                    .cart
                    ?.totalQuantity
                    ?: 0,

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
    ) { backStackEntry ->
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

        /*
         * C3C.2:
         * Checkout için ayrı ve paralel bir auth modeli
         * üretmek yerine mevcut CustomerProfileViewModel'i
         * reuse ediyoruz.
         *
         * Böylece GET /api/Auth/profile tek kaynak olarak:
         * phone
         * isPhoneVerified
         * phoneVerifiedAt
         * bilgilerini sağlar.
         */
        val checkoutProfileViewModel:
                CustomerProfileViewModel =
            viewModel(
                factory =
                    CustomerProfileViewModelFactory(
                        context = context
                    )
            )

        val checkoutProfileUiState by
        checkoutProfileViewModel
            .uiState
            .collectAsStateWithLifecycle()

        val phoneVerificationResult by
        backStackEntry
            .savedStateHandle
            .getStateFlow(
                PHONE_VERIFICATION_RESULT,
                false
            )
            .collectAsStateWithLifecycle()

        /*
         * Checkout ilk kez açıldığında order verilerini yükle.
         *
         * OTP ekranına gidip geri dönüldüğünde bu back stack
         * entry'nin ViewModel'i korunur. O sırada loadData()
         * tekrar çağrılmaz; seçilmiş adres, ödeme yöntemi ve
         * sipariş notu kaybolmaz.
         */
        LaunchedEffect(Unit) {
            if (
                createOrderUiState.isLoading &&
                createOrderUiState.cart == null
            ) {
                createOrderViewModel
                    .loadData()
            }

            if (
                checkoutProfileUiState
                    .isLoading
            ) {
                checkoutProfileViewModel
                    .loadProfile()
            }
        }

        /*
         * Mevcut OTP ekranı doğrulama başarılı olduğunda
         * sonucu bir önceki destination'ın SavedStateHandle'ına
         * bırakır.
         *
         * Checkout bu sonucu yakalar ve SADECE profile bilgisini
         * yeniler. CreateOrderViewModel yeniden oluşturulmaz,
         * form state korunur.
         */
        LaunchedEffect(
            phoneVerificationResult
        ) {
            if (phoneVerificationResult) {
                checkoutProfileViewModel
                    .loadProfile()

                backStackEntry
                    .savedStateHandle[
                    PHONE_VERIFICATION_RESULT
                ] = false
            }
        }

        val checkoutProfile =
            checkoutProfileUiState
                .profile

        val isPhoneVerifiedForOrder =
            checkoutProfile
                ?.isPhoneVerified == true &&
                    checkoutProfile
                        .phoneVerifiedAt != null &&
                    checkoutProfile
                        .phone
                        .isNotBlank()

        CreateOrderScreen(
            uiState =
                createOrderUiState,

            isPhoneVerificationLoading =
                checkoutProfileUiState
                    .isLoading,

            isPhoneVerifiedForOrder =
                isPhoneVerifiedForOrder,

            phoneVerificationErrorMessage =
                checkoutProfileUiState
                    .errorMessage,

            onBackClick = {
                navController.popBackStack()
            },

            onRetryClick = {
                createOrderViewModel.loadData()
            },

            onRetryPhoneVerificationStatusClick = {
                checkoutProfileViewModel
                    .loadProfile()
            },

            onPhoneVerificationClick = {
                navController.navigate(
                    AppDestination
                        .CustomerPhoneVerification
                        .route
                )
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
                /*
                 * UI yalnız profile guard geçtiğinde bu
                 * callback'i sunar.
                 *
                 * Backend C3C.1 ayrıca tekrar kontrol eder;
                 * Android tek güvenlik katmanı değildir.
                 */
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

        CustomerRootScaffold(
            selectedRoute =
                AppDestination
                    .Orders
                    .route,

            onBottomDestinationClick = { route ->
                navigateToCustomerRoot(
                    navController =
                        navController,

                    route =
                        route
                )
            }
        ) { innerPadding ->

            OrdersScreen(
                uiState =
                    ordersUiState,

                onBackClick = {
                    navigateToCustomerRoot(
                        navController =
                            navController,

                        route =
                            AppDestination
                                .CustomerHome
                                .route
                    )
                },

                onRetryClick = {
                    ordersViewModel
                        .loadOrders()
                },

                onCancelOrderClick = {
                        orderId ->

                    ordersViewModel
                        .cancelOrder(
                            orderId =
                                orderId
                        )
                },

                onOrderClick = {
                        orderId ->

                    navController.navigate(
                        AppDestination
                            .OrderDetail
                            .createRoute(
                                orderId
                            )
                    )
                },

                showBackButton = false,

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding
                        )
            )
        }
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
                ProducerApplicationStatus
                    .fromBackendValue(
                        verificationStatus
                    ) ==
                ProducerApplicationStatus.APPROVED
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