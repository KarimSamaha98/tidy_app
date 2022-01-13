package com.epfl.esl.tidy

import com.google.firebase.database.DatabaseError
import java.lang.Exception

data class Response (
    var objectList: List<Any?>? = null,
    var exception: Throwable? = null
)

interface onGetDataListener {
    fun onSuccess(response: Response)
    fun onStart()
    fun onFailure(response: Response)
}