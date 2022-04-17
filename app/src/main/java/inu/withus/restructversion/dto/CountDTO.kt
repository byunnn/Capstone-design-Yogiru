package inu.withus.cameraintegration.dto

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CountDTO(
    var count_fridge: Int? = null,
    var count_frozen: Int? = null,
    var count_room: Int? = null
)
