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
import com.homemadefood.app.ui.address.AddressesScreen
import com.homemadefood.app.ui.address.AddressesViewModel
import com.homemadefood.app.ui.address.AddressesViewModelFactory
import com.homemadefood.app.ui.address.AddAddressScreen
import com.homemadefood.app.ui.address.AddressFormViewModel
import com.homemadefood.app.ui.address.AddressFormViewModelFactory
import com.homemadefood.app.ui.address.EditAddressScreen
import com.homemadefood.app.ui.address.EditAddressViewModel
import com.homemadefood.app.ui.address.EditAddressViewModelFactory
import com.homemadefood.app.ui.cart.CartScreen
import com.homemadefood.app.ui.cart.CartViewModel
import com.homemadefood.app.ui.cart.CartViewModelFactory
import com.homemadefood.app.ui.order.CreateOrderScreen
import com.homemadefood.app.ui.order.CreateOrderViewModel
import com.homemadefood.app.ui.order.CreateOrderViewModelFactory
import com.homemadefood.app.ui.order.OrdersScreen
import com.homemadefood.app.ui.order.OrdersViewModel
import com.homemadefood.app.ui.order.OrdersViewModelFactory
import com.homemadefood.app.ui.order.OrderDetailScreen
import com.homemadefood.app.ui.order.OrderDetailViewModel
import com.homemadefood.app.ui.order.OrderDetailViewModelFactory
import com.homemadefood.app.ui.recommendation.RecommendationScreen
import com.homemadefood.app.ui.recommendation.RecommendationViewModel
import com.homemadefood.app.ui.recommendation.RecommendationViewModelFactory
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
    var showAddressesScreen by rememberSaveable {
        mutableStateOf(false)
    }
    var showAddAddressScreen by rememberSaveable {
        mutableStateOf(false)
    }

    var addAddressFormKey by rememberSaveable {
        mutableStateOf(0)
    }
    var selectedEditAddressId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var editAddressFormKey by rememberSaveable {
        mutableStateOf(0)
    }
    var showCartScreen by rememberSaveable {
        mutableStateOf(false)
    }
    var showCreateOrderScreen by rememberSaveable {
        mutableStateOf(false)
    }

    var createOrderScreenKey by rememberSaveable {
        mutableStateOf(0)
    }
    var showOrdersScreen by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedOrderId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }
    var showRecommendationScreen by rememberSaveable {
        mutableStateOf(false)
    }

    var recommendationScreenKey by rememberSaveable {
        mutableStateOf(0)
    }

    var returnToRecommendationAfterFoodDetail by rememberSaveable {
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

                            if (returnToRecommendationAfterFoodDetail) {
                                showRecommendationScreen = true

                                returnToRecommendationAfterFoodDetail =
                                    false
                            }
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
                } else if (showRecommendationScreen) {

                    val recommendationViewModel:
                            RecommendationViewModel =
                        viewModel(
                            key =
                                "recommendation_$recommendationScreenKey",

                            factory =
                                RecommendationViewModelFactory(
                                    context = context
                                )
                        )

                    val recommendationUiState by
                    recommendationViewModel.uiState
                        .collectAsStateWithLifecycle()

                    LaunchedEffect(
                        recommendationScreenKey
                    ) {
                        recommendationViewModel.loadAddresses()
                    }

                    RecommendationScreen(
                        uiState = recommendationUiState,

                        onBackClick = {
                            showRecommendationScreen = false
                        },

                        onRetryAddressesClick = {
                            recommendationViewModel.loadAddresses()
                        },

                        onManageAddressesClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null

                            showRecommendationScreen = false
                            showAddAddressScreen = false
                            showAddressesScreen = true
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
                            showRecommendationScreen = false
                            showCartScreen = true
                        },

                        onOpenFoodClick = { foodId ->
                            returnToRecommendationAfterFoodDetail = true

                            showRecommendationScreen = false
                            selectedFoodId = foodId
                        },

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )
                    )
                } else if (showAddAddressScreen) {

                    val addressFormViewModel:
                            AddressFormViewModel =
                        viewModel(
                            key =
                                "add_address_$addAddressFormKey",

                            factory =
                                AddressFormViewModelFactory(
                                    context = context
                                )
                        )

                    val addressFormUiState by
                    addressFormViewModel.uiState
                        .collectAsStateWithLifecycle()

                    LaunchedEffect(
                        addressFormUiState.isSaved
                    ) {
                        if (addressFormUiState.isSaved) {
                            addressFormViewModel
                                .resetSavedState()

                            showAddAddressScreen = false
                            showAddressesScreen = true
                        }
                    }

                    AddAddressScreen(
                        uiState = addressFormUiState,

                        onBackClick = {
                            showAddAddressScreen = false
                            showAddressesScreen = true
                        },

                        onTitleChange = {
                                value ->

                            addressFormViewModel
                                .updateTitle(value)
                        },

                        onFullAddressChange = {
                                value ->

                            addressFormViewModel
                                .updateFullAddress(value)
                        },

                        onLatitudeChange = {
                                value ->

                            addressFormViewModel
                                .updateLatitude(value)
                        },

                        onLongitudeChange = {
                                value ->

                            addressFormViewModel
                                .updateLongitude(value)
                        },

                        onIsDefaultChange = {
                                value ->

                            addressFormViewModel
                                .updateIsDefault(value)
                        },

                        onSaveClick = {
                            addressFormViewModel
                                .saveAddress()
                        },

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )
                    )
                } else if (showCreateOrderScreen) {

                    val createOrderViewModel:
                            CreateOrderViewModel =
                        viewModel(
                            key =
                                "create_order_$createOrderScreenKey",

                            factory =
                                CreateOrderViewModelFactory(
                                    context = context
                                )
                        )

                    val createOrderUiState by
                    createOrderViewModel.uiState
                        .collectAsStateWithLifecycle()

                    LaunchedEffect(
                        createOrderScreenKey
                    ) {
                        createOrderViewModel.loadData()
                    }

                    CreateOrderScreen(
                        uiState = createOrderUiState,

                        onBackClick = {
                            showCreateOrderScreen = false
                            showCartScreen = true
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
                            createOrderViewModel.createOrder()
                        },

                        onReturnHomeClick = {
                            createOrderViewModel
                                .resetCreatedOrder()

                            selectedFoodId = null
                            selectedEditAddressId = null

                            showFavoritesScreen = false
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            showCartScreen = false
                            showCreateOrderScreen = false
                            showOrdersScreen = false
                        },

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )
                    )
                } else if (selectedOrderId != null) {

                    val orderId =
                        selectedOrderId!!

                    val orderDetailViewModel:
                            OrderDetailViewModel =
                        viewModel(
                            key = "order_detail_$orderId",

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
                            selectedOrderId = null
                            showOrdersScreen = true
                        },

                        onRetryClick = {
                            orderDetailViewModel.loadOrder()
                        },

                        onCancelOrderClick = {
                            orderDetailViewModel.cancelOrder()
                        },

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )
                    )
                } else if (showOrdersScreen) {

                    val ordersViewModel:
                            OrdersViewModel =
                        viewModel(
                            key = "orders_screen",

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
                            showOrdersScreen = false
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
                            selectedOrderId = orderId
                            showOrdersScreen = false
                        },

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )
                    )
                } else if (showCartScreen) {

                    val cartViewModel:
                            CartViewModel =
                        viewModel(
                            key = "cart_screen",

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
                            showCartScreen = false
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

                        onRemoveItemClick = {
                                cartItemId ->

                            cartViewModel.removeItem(
                                cartItemId = cartItemId
                            )
                        },

                        onClearCartClick = {
                            cartViewModel.clearCart()
                        },

                        onCreateOrderClick = {
                            createOrderScreenKey += 1
                            showOrdersScreen = false
                            showCartScreen = false
                            showCreateOrderScreen = true
                        },

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )
                    )
                } else if (selectedEditAddressId != null) {

                    val editAddressId =
                        selectedEditAddressId!!

                    val editAddressViewModel:
                            EditAddressViewModel =
                        viewModel(
                            key =
                                "edit_address_${editAddressId}_$editAddressFormKey",

                            factory =
                                EditAddressViewModelFactory(
                                    context = context,
                                    addressId = editAddressId
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

                            selectedEditAddressId = null
                            showAddressesScreen = true
                        }
                    }

                    EditAddressScreen(
                        uiState = editAddressUiState,

                        onBackClick = {
                            selectedEditAddressId = null
                            showAddressesScreen = true
                        },

                        onRetryClick = {
                            editAddressViewModel
                                .loadAddress()
                        },

                        onTitleChange = { value ->
                            editAddressViewModel
                                .updateTitle(value)
                        },

                        onFullAddressChange = { value ->
                            editAddressViewModel
                                .updateFullAddress(value)
                        },

                        onLatitudeChange = { value ->
                            editAddressViewModel
                                .updateLatitude(value)
                        },

                        onLongitudeChange = { value ->
                            editAddressViewModel
                                .updateLongitude(value)
                        },

                        onIsDefaultChange = { value ->
                            editAddressViewModel
                                .updateIsDefault(value)
                        },

                        onSaveClick = {
                            editAddressViewModel
                                .updateAddress()
                        },

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )
                    )
                } else if (showAddressesScreen) {

                    val addressesViewModel:
                            AddressesViewModel =
                        viewModel(
                            key = "addresses_screen",

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
                            showAddAddressScreen = false
                            showAddressesScreen = false
                        },

                        onRetryClick = {
                            addressesViewModel.loadAddresses()
                        },

                        onAddAddressClick = {
                            addAddressFormKey += 1
                            showAddressesScreen = false
                            showAddAddressScreen = true
                        },

                        onDeleteAddressClick = { addressId ->
                            addressesViewModel.deleteAddress(
                                addressId = addressId
                            )
                        },
                        onEditAddressClick = { addressId ->
                            editAddressFormKey += 1
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            selectedEditAddressId = addressId
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
                            returnToRecommendationAfterFoodDetail = false

                            showFavoritesScreen = false
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            showCartScreen = false
                            showCreateOrderScreen = false
                            showOrdersScreen = false
                            showRecommendationScreen = false

                            selectedEditAddressId = null
                            selectedOrderId = null
                            selectedFoodId = clickedFoodId
                        },

                        onFavoritesClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null

                            showAddressesScreen = false
                            showAddAddressScreen = false
                            showCartScreen = false
                            showCreateOrderScreen = false
                            showOrdersScreen = false
                            showRecommendationScreen = false

                            showFavoritesScreen = true
                        },

                        onAddressesClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null

                            showFavoritesScreen = false
                            showAddAddressScreen = false
                            showCartScreen = false
                            showCreateOrderScreen = false
                            showOrdersScreen = false
                            showRecommendationScreen = false

                            showAddressesScreen = true
                        },

                        onLogoutClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null
                            selectedOrderId = null

                            returnToRecommendationAfterFoodDetail = false

                            showFavoritesScreen = false
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            showCartScreen = false
                            showCreateOrderScreen = false
                            showOrdersScreen = false
                            showRecommendationScreen = false

                            authViewModel.logout()
                        },

                        onCartClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null

                            showFavoritesScreen = false
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            showCreateOrderScreen = false
                            showOrdersScreen = false
                            showRecommendationScreen = false

                            showCartScreen = true
                        },

                        onOrdersClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null
                            selectedOrderId = null

                            showFavoritesScreen = false
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            showCartScreen = false
                            showCreateOrderScreen = false
                            showRecommendationScreen = false

                            showOrdersScreen = true
                        },
                        onRecommendationClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null
                            selectedOrderId = null

                            returnToRecommendationAfterFoodDetail = false

                            showFavoritesScreen = false
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            showCartScreen = false
                            showCreateOrderScreen = false
                            showOrdersScreen = false

                            recommendationScreenKey += 1
                            showRecommendationScreen = true
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