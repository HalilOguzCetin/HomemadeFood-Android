package com.homemadefood.app.ui.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.DeliveryAddressSelectionManager
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.CartRepository
import com.homemadefood.app.data.repository.OrderRepository

class CreateOrderViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                CreateOrderViewModel::class.java
            )
        ) {
            val cartRepository =
                CartRepository()

            val addressRepository =
                AddressRepository()

            val orderRepository =
                OrderRepository()

            val sessionManager =
                SessionManager(
                    context.applicationContext
                )

            val deliveryAddressSelectionManager =
                DeliveryAddressSelectionManager(
                    sessionManager =
                        sessionManager
                )

            @Suppress("UNCHECKED_CAST")
            return CreateOrderViewModel(
                cartRepository =
                    cartRepository,

                addressRepository =
                    addressRepository,

                orderRepository =
                    orderRepository,

                sessionManager =
                    sessionManager,

                deliveryAddressSelectionManager =
                    deliveryAddressSelectionManager
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}