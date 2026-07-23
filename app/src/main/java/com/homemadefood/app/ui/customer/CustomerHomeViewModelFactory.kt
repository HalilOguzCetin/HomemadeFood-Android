package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.FoodRepository

class CustomerHomeViewModelFactory :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                CustomerHomeViewModel::class.java
            )
        ) {
            val categoryRepository =
                CategoryRepository()

            val foodRepository =
                FoodRepository()

            @Suppress("UNCHECKED_CAST")
            return CustomerHomeViewModel(
                categoryRepository =
                    categoryRepository,

                foodRepository =
                    foodRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}