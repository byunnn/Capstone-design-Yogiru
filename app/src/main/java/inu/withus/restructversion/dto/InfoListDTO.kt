package inu.withus.restructversion.dto

import com.google.firebase.database.annotations.NotNull
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class InfoListDTO (

    @NotNull
    var expireDate : String? = null,

    @NotNull
    var count : Int? = null,

    var memo : String? = ""
)
