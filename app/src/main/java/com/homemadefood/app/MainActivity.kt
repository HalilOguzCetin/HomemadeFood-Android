package com.homemadefood.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.homemadefood.app.data.remote.RetrofitClient
import com.homemadefood.app.navigation.NavigationHomemadeFoodApp
import com.homemadefood.app.ui.theme.HomemadeFoodTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        RetrofitClient.initialize(
            applicationContext
        )

        enableEdgeToEdge()

        setContent {
            HomemadeFoodTheme {
                NavigationHomemadeFoodApp()
            }
        }
    }
}