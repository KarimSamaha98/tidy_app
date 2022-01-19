package com.epfl.esl.tidy.admin

import com.google.firebase.database.Exclude

data class Room (val room:String = "",
                 val description: String = "",
                 var imageUrl: String = "",
                 @get:Exclude
                 var key : String = ""){
    val roomID: Int
//    TODO fix this mapping so can add multiple bathrooms and multiple items
    init {
        val roomMapping = mapOf(
            "" to -1,
            "Living Room" to 0,
            "Dining Room" to 1,
            "Kitchen" to 2,
            "Bathroom" to 3,
        )
        roomID = roomMapping[room]!!
    }
}

fun main() {
    var room = Room("Kitchen", "desc", "URL")
    print(room.roomID)
}