package com.homemadefood.app.data.model

data class CreateAddressRequest(
    val title: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean
)