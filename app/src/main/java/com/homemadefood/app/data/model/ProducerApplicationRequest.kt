package com.homemadefood.app.data.model

data class ProducerApplicationRequest(
    val businessName: String,
    val description: String,
    val address: String,

    val city: String = "",
    val district: String = "",
    val neighborhood: String = "",
    val street: String = "",
    val buildingNo: String = "",
    val floor: String? = null,
    val apartmentNo: String? = null,
    val addressNote: String? = null,

    val latitude: Double,
    val longitude: Double,
    val dailyCapacity: Int
)