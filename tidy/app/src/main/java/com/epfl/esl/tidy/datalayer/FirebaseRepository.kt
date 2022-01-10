package com.epfl.esl.tidy.datalayer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.epfl.esl.tidy.Response
import com.epfl.esl.tidy.RoomUpload
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
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val profileRef: DatabaseReference = database.getReference(Constants.PROFILES)
    val roomRef: DatabaseReference = database.getReference(Constants.ROOMS)
    var storageRef: StorageReference = Firebase.storage.reference

    fun registerUser() {
    }

    fun getCurrentUserID() {
    }

    suspend fun getRoomDetailsCo(spaceID: Int): Response = suspendCoroutine { continuation ->
        val response = Response()
        var roomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (space in snapshot.children) {
                    if (space.child(Constants.SPACEID).getValue(Int::class.java)!! == spaceID) {
                        response.rooms = space.child(Constants.ROOMS).children.map { snapShot ->
                            snapShot.getValue(RoomUpload::class.java)
                        }
                        continuation.resume(response)
                    } else{
                        continuation.resumeWithException(Throwable("No userID matches"))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                response.exception = error
                continuation.resumeWithException(Throwable(error.toString()))
                Log.d(TAG, "Database Error: ${error.toString()}")
            }
        }
        roomRef.addListenerForSingleValueEvent(roomListener)
    }
}
//        withContext(IO) {
//            val job = launch {
//                roomRef.addListenerForSingleValueEvent(roomListener)
//            }
//            job.await()
//        }
//        roomRef.addListenerForSingleValueEvent(roomListener).await()
//        Log.d(TAG, "debug: ${Thread.currentThread().name}")
//        return response
//    }
//}
//    fun getRoomDetails(spaceID : Int): ArrayList<RoomUpload?> {
//        var roomList : ArrayList<RoomUpload?> = arrayListOf()
//
//        val roomListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (space in snapshot.children) {
//                    if (space.child(Constants.SPACEID).getValue(Int::class.java)!! == spaceID) {
//                        for (rooms in space.child(Constants.ROOMS).children) {
//                            val room: RoomUpload? = rooms.getValue(RoomUpload::class.java)
//                            roomList.add(room)
//                        }
//                    }
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
////                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
////                TODO couldn't figure out how to make this work
////                binding.progressCircular.visibility = View.INVISIBLE
//            }
//        }
//        roomRef.addListenerForSingleValueEvent(roomListener)
//
//    }
//}