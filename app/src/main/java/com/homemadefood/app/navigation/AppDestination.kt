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

    // -------------------------
    // Auth ekranları
    //---------
    // Auth ekranları
    // -------------------------

    data object Login :
        AppDestination(
            route = "auth/login"
        )

    data object Register :
        AppDestination(
            route = "auth/register"
        )

    // -------------------------
    // Customer ekranları
    // -------------------------

    data object CustomerHome :
        AppDestination(
            route = "customer/home"
        )

    data object FoodDetail :
        AppDestination(
            route = "customer/food/{foodId}"
        ) {

        const val FOOD_ID_ARGUMENT =
            "foodId"

        fun createRoute(
            foodId: Int
        ): String {
            return "customer/food/$foodId"
        }
    }

    data object Favorites :
        AppDestination(
            route = "customer/favorites"
        )

    data object Addresses :
        AppDestination(
            route = "customer/addresses"
        )

    data object AddAddress :
        AppDestination(
            route = "customer/address/add"
        )

    data object EditAddress :
        AppDestination(
            route = "customer/address/edit/{addressId}"
        ) {

        const val ADDRESS_ID_ARGUMENT =
            "addressId"

        fun createRoute(
            addressId: Int
        ): String {
            return "customer/address/edit/$addressId"
        }
    }

    data object Cart :
        AppDestination(
            route = "customer/cart"
        )

    data object CreateOrder :
        AppDestination(
            route = "customer/order/create"
        )

    data object Orders :
        AppDestination(
            route = "customer/orders"
        )

    data object OrderDetail :
        AppDestination(
            route = "customer/order/{orderId}"
        ) {

        const val ORDER_ID_ARGUMENT =
            "orderId"

        fun createRoute(
            orderId: Int
        ): String {
            return "customer/order/$orderId"
        }
    }

    data object Recommendation :
        AppDestination(
            route = "customer/recommendation"
        )

    data object CustomerProducerApplication :
        AppDestination(
            route = "customer/producer-application"
        )

    data object ProducerHome :
        AppDestination(
            route = "producer/home"
        )

    data object ProducerApplication :
        AppDestination(
            route = "producer/application"
        )

    data object ProducerFoods :
        AppDestination(
            route = "producer/foods"
        )

    data object CreateFood :
        AppDestination(
            route = "producer/food/create"
        )

    data object EditFood :
        AppDestination(
            route = "producer/food/edit/{foodId}"
        ) {

        const val FOOD_ID_ARGUMENT =
            "foodId"

        fun createRoute(
            foodId: Int
        ): String {
            return "producer/food/edit/$foodId"
        }
    }

    data object ProducerOrders :
        AppDestination(
            route = "producer/orders"
        )

    // -------------------------
    // Admin ekranları
    // -------------------------

    data object AdminHome :
        AppDestination(
            route = "admin/home"
        )

    data object AdminApplications :
        AppDestination(
            route = "admin/applications"
        )

    data object RecommendationAnalytics :
        AppDestination(
            route = "admin/analytics"
        )
}