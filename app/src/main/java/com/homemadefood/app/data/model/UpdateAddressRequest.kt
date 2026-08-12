package com.homemadefood.app.data.model

data class UpdateAddressRequest(
    val title: String,
    val fullAddress: String,

    val city: String,
    val district: String,
    val neighborhood: String,
    val street: String,
    val buildingNo: String,

    val floor: String?,
    val apartmentNo: String?,
    val addressNote: String?,

    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean
)
