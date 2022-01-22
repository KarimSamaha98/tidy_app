package com.epfl.esl.tidy.admin

import com.google.firebase.database.Exclude

data class Supply (val name: String = "",
                   val quantity : Int = 0,
                   val description: String = "",
                   var imageUrl : String = "",
                   @get:Exclude
                   var key : String = "")