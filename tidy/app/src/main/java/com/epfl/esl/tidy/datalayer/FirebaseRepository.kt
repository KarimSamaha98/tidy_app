package com.epfl.esl.tidy.datalayer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.epfl.esl.tidy.Reference
import com.epfl.esl.tidy.Response
import com.epfl.esl.tidy.admin.Room
import com.epfl.esl.tidy.onGetDataListener
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository {
    private val TAG = "FirebaseRepository"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val profileRef: DatabaseReference = database.getReference(Constants.PROFILES)
    private val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)
    private var storageRef: StorageReference = Firebase.storage.reference


    fun registerUser() {
    }

    fun getRoomDetails(spaceID: Int, onGetDataListener: onGetDataListener) {
        onGetDataListener.onStart()
        val response = Response()

        var roomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (space in snapshot.children) {
                    if (space.child(Constants.SPACEID).getValue(Int::class.java)!! == spaceID) {
//                        response.objectList =
//                            space.child(Constants.ROOMS).children.map { snapShot ->
//                                snapShot.getValue(Room::class.java)
//                            }
                        getRoom(response, space)
                    }
                }
                onGetDataListener.onSuccess(response)
            }

            override fun onCancelled(error: DatabaseError) {
                response.exception = Throwable(error.toString())
                onGetDataListener.onFailure(response)
                Log.d(TAG, "Database Error: ${error.toString()}")
            }
        }
        spaceRef.addListenerForSingleValueEvent(roomListener)
    }
    fun getRoom(response: Response, space: DataSnapshot) {
        response.objectList =
            space.child(Constants.ROOMS).children.map { snapShot ->
                snapShot.getValue(Room::class.java)
            }
    }
}