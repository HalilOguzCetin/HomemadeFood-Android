package com.homemadefood.app.data.model

data class ReverseGeocodeResponse(
    val city: String = "",
    val district: String = "",
    val neighborhood: String = "",
    val street: String = "",
    val buildingNo: String = "",
    val formattedAddress: String = "",
    val granularity: String = ""
)