package com.homemadefood.app.data.model

data class UpdateProducerBusinessHourRequest(
    /*
     * ISO-8601:
     * 1 = Pazartesi ... 7 = Pazar
     */
    val dayOfWeek: Int,

    val isClosed: Boolean,

    /*
     * Açık günlerde HH:mm.
     * Kapalı günlerde null.
     */
    val openTime: String?,

    val closeTime: String?
)
