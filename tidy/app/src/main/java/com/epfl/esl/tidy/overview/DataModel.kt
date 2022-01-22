package com.epfl.esl.tidy.overview

import com.google.firebase.database.Exclude

sealed class DataModel {
    data class Room (val room:String = "",
                     val description: String = "",
                     var imageUrl: String = "",
                     @get:Exclude
                     var key : String = "") : DataModel()

    data class Supply (val name: String = "",
                       val quantity : Int = 0,
                       val description: String = "",
                       var imageUrl : String = "",
                       @get:Exclude
                       var key : String = "") : DataModel()

    data class Header (val title : String = "") : DataModel()
}