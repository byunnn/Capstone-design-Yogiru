package inu.withus.cameraintegration.dto

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.annotations.NotNull

@IgnoreExtraProperties
data class FoodInfoDTO(
    @NotNull
    var place : String? = null,

    @NotNull
    var foodName : String? = null,

    @NotNull
    var expirationDate : String? = null,

    @NotNull
    var count : Int? = null,

    var memo : String? = null
)
