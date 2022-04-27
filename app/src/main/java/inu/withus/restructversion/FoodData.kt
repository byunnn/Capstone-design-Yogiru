package inu.withus.restructversion

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodData ( val foodName : String, val expireDate : String, val quantity : Int): Parcelable{


}

//
//@Parcelize
//data class FoodData ( val foodName : String, val expireDate : String, val quantity : Int): Parcelable{
//
//    constructor(parcel : Parcel) : this(){
//        parcel.run {
//
//        }
//    }
//}