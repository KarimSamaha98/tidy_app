package com.epfl.esl.tidy

data class RoomUpload (val room:String = "",
                       val description: String = "",
                       val imageUrl: String = ""){
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
    var room = RoomUpload("Kitchen", "desc", "URL")
    print(room.roomID)
}