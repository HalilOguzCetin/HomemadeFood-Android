package com.homemadefood.app.ui.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AdminRepository

class AdminUserDetailViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                AdminUserDetailViewModel::class.java
            )
        ) {
            val adminRepository =
                AdminRepository()

            val sessionManager =
                SessionManager(
                    context.applicationContext
                )

            @Suppress("UNCHECKED_CAST")
            return AdminUserDetailViewModel(
                adminRepository =
                    adminRepository,

                sessionManager =
                    sessionManager
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}