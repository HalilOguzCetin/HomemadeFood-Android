package com.homemadefood.app.data.model

data class AddressResponse(
    val id: Int,
    val title: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean,
    val createdAt: String,
    val city: String = "",
    val district: String = "",
    val neighborhood: String = "",
    val street: String = "",
    val buildingNo: String = "",

    val floor: String? = null,
    val apartmentNo: String? = null,
    val addressNote: String? = null,

    )