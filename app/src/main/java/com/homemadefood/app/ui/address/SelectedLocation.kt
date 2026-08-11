package com.homemadefood.app.ui.address

data class SelectedLocation(
    val latitude: Double,
    val longitude: Double
) {
    fun isValid(): Boolean {
        return latitude in -90.0..90.0 &&
                longitude in -180.0..180.0
    }
}