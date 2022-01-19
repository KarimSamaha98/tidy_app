package com.epfl.esl.tidy

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import java.lang.Exception

data class Reference (
    var reference : DatabaseReference? = null,
    var exception : Throwable? = null,
        )

data class Response (
    var objectList: List<Any?>? = null,
    var exception: Throwable? = null
)

interface onGetDataListener {
    fun onSuccess(response: Response)
    fun onFailure(response: Response)
}