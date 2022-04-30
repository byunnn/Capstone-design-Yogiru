package inu.withus.restructversion.dto

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.annotations.NotNull
import kotlinx.parcelize.Parcelize


@IgnoreExtraProperties
@Parcelize
data class FoodDTO (

    @NotNull
    var foodName : String? = null,

    @NotNull
    var expireDate : String? = null,

    @NotNull
    var count : Int? = null,

    var memo : String? = null
) : Parcelable