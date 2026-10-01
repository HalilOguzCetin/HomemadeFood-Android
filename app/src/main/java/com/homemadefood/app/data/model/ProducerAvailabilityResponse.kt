package com.homemadefood.app.data.model

data class ProducerAvailabilityResponse(
    val availabilityMode: String,
    val isCurrentlyOpen: Boolean,
    val hasSchedule: Boolean,
    val businessHours:
    List<ProducerBusinessHourResponse>
) {

    val mode: ProducerAvailabilityMode?
        get() =
            ProducerAvailabilityMode
                .fromBackendValue(
                    availabilityMode
                )
}
