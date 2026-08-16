package com.homemadefood.app.navigation

object AppGraph {
    const val AUTH = "auth_graph"
    const val CUSTOMER = "customer_graph"
    const val PRODUCER = "producer_graph"
    const val ADMIN = "admin_graph"
}

sealed class AppDestination(
    val route: String
) {

    // Auth
    data object Login :
        AppDestination("auth/login")

    data object Register :
        AppDestination("auth/register")

    data object EmailVerification :
        AppDestination("auth/verify-email")

    data object ForgotPassword :
        AppDestination("auth/forgot-password")

    data object ResetPassword :
        AppDestination("auth/reset-password")

    // Customer
    data object CustomerHome :
        AppDestination("customer/home")

    data object CustomerExplore :
        AppDestination("customer/explore")

    data object CustomerAccount :
        AppDestination("customer/account")

    data object CustomerProfile :
        AppDestination("customer/profile")

    data object CustomerPhoneVerification :
        AppDestination(
            "customer/profile/phone-verification"
        )

    data object FoodDetail :
        AppDestination(
            "customer/food/{foodId}"
        ) {
        const val FOOD_ID_ARGUMENT = "foodId"

        fun createRoute(foodId: Int) =
            "customer/food/$foodId"
    }

    data object StorefrontMenu :
        AppDestination(
            "customer/storefront/{producerProfileId}"
        ) {
        const val PRODUCER_PROFILE_ID_ARGUMENT =
            "producerProfileId"

        fun createRoute(
            producerProfileId: Int
        ) =
            "customer/storefront/$producerProfileId"
    }

    data object Favorites :
        AppDestination("customer/favorites")

    data object Addresses :
        AppDestination("customer/addresses")

    data object AddAddress :
        AppDestination("customer/address/add")

    data object AddressMap :
        AppDestination("customer/address/map")

    data object EditAddress :
        AppDestination(
            "customer/address/edit/{addressId}"
        ) {
        const val ADDRESS_ID_ARGUMENT =
            "addressId"

        fun createRoute(addressId: Int) =
            "customer/address/edit/$addressId"
    }

    data object Cart :
        AppDestination("customer/cart")

    data object CreateOrder :
        AppDestination("customer/order/create")

    data object Orders :
        AppDestination("customer/orders")

    data object OrderDetail :
        AppDestination(
            "customer/order/{orderId}"
        ) {
        const val ORDER_ID_ARGUMENT =
            "orderId"

        fun createRoute(orderId: Int) =
            "customer/order/$orderId"
    }

    data object Recommendation :
        AppDestination(
            "customer/recommendation"
        )

    data object CustomerProducerApplication :
        AppDestination(
            "customer/producer-application"
        )

    data object CustomerReviews :
        AppDestination("customer/reviews")

    // Producer
    data object ProducerHome :
        AppDestination("producer/home")

    data object ProducerApplication :
        AppDestination(
            "producer/application"
        )

    data object ProducerFoods :
        AppDestination("producer/foods")

    data object CreateFood :
        AppDestination("producer/food/create")

    data object EditFood :
        AppDestination(
            "producer/food/edit/{foodId}"
        ) {
        const val FOOD_ID_ARGUMENT =
            "foodId"

        fun createRoute(foodId: Int) =
            "producer/food/edit/$foodId"
    }

    data object ProducerOrders :
        AppDestination("producer/orders")

    data object ProducerReviews :
        AppDestination("producer/reviews")

    data object ProducerProfile :
        AppDestination("producer/profile")

    data object ProducerProfileAddressMap :
        AppDestination(
            "producer/profile/address/map"
        )

    // Admin
    data object AdminHome :
        AppDestination("admin/home")

    data object AdminApplications :
        AppDestination("admin/applications")

    data object AdminUsers :
        AppDestination("admin/users")

    data object AdminUserDetail :
        AppDestination(
            "admin/users/{userId}"
        ) {
        const val USER_ID_ARGUMENT =
            "userId"

        fun createRoute(userId: Int) =
            "admin/users/$userId"
    }

    data object AdminOrders :
        AppDestination("admin/orders")

    data object AdminOrderDetail :
        AppDestination(
            "admin/orders/{orderId}"
        ) {
        const val ORDER_ID_ARGUMENT =
            "orderId"

        fun createRoute(orderId: Int) =
            "admin/orders/$orderId"
    }

    data object RecommendationAnalytics :
        AppDestination("admin/analytics")
}