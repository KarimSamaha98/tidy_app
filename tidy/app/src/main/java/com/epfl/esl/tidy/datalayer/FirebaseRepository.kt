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

    fun getRoomDetailsCo(spaceID: Int): MutableLiveData<Response> {
        val mutableLiveData = MutableLiveData<Response>()
        val response = Response()
        val roomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (space in snapshot.children) {
                    if (space.child(Constants.SPACEID).getValue(Int::class.java)!! == spaceID) {
                        Log.d(TAG, "Went into Loop")
                        response.rooms = space.child(Constants.ROOMS).children.map { snapShot ->
                                snapShot.getValue(RoomUpload::class.java)
                            }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                response.exception = error
            }
        }
        roomRef.addListenerForSingleValueEvent(roomListener)
        mutableLiveData.value = response

        return mutableLiveData
    }
}
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