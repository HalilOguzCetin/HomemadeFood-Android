package com.homemadefood.app.data.model

data class AddressResponse(
    val id: Int,
    val title: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean,
    val createdAt: String
)