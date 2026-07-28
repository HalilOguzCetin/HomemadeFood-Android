package com.homemadefood.app.ui.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.OrderRepository

class OrderDetailViewModelFactory(
    private val context: Context,
    private val orderId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                OrderDetailViewModel::class.java
            )
        ) {
            val orderRepository =
                OrderRepository()

            val sessionManager =
                SessionManager(
                    context.applicationContext
                )

            @Suppress("UNCHECKED_CAST")
            return OrderDetailViewModel(
                orderId = orderId,
                orderRepository = orderRepository,
                sessionManager = sessionManager
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}