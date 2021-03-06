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


open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}