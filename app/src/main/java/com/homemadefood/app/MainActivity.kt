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
                            /*
                             * Sipariş oluşturma ekranını
                             * sonraki aşamada bağlayacağız.
                             */
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
                            showFavoritesScreen = false
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            showCartScreen = false
                            selectedEditAddressId = null
                            selectedFoodId = clickedFoodId
                        },

                        onFavoritesClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            showCartScreen = false
                            showFavoritesScreen = true
                        },

                        onAddressesClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null
                            showFavoritesScreen = false
                            showCartScreen = false
                            showAddAddressScreen = false
                            showAddressesScreen = true
                        },

                        onLogoutClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null
                            showFavoritesScreen = false
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            authViewModel.logout()
                            showCartScreen = false
                        },
                        onCartClick = {
                            selectedFoodId = null
                            selectedEditAddressId = null

                            showFavoritesScreen = false
                            showAddressesScreen = false
                            showAddAddressScreen = false
                            showCartScreen = true
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