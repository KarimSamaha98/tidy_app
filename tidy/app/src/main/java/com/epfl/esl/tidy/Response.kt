package com.epfl.esl.tidy

import com.google.firebase.database.DatabaseError
import java.lang.Exception

data class Response (
    var rooms: List<RoomUpload?>? = null,
    var exception: DatabaseError? = null
)