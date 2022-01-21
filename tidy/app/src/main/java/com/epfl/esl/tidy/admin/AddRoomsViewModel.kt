package com.epfl.esl.tidy.admin

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.Event
import com.epfl.esl.tidy.Response
import com.epfl.esl.tidy.datalayer.FirebaseRepository
import com.epfl.esl.tidy.onGetDataListener
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class AddRoomsViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "AddRoomsViewModel"
    val repository = FirebaseRepository()
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)

    var roomKey : String
    var imageUri: Uri?
    var roomName: String
    var roomDescription : String
    var imageUrl : String
    var spaceID : String = ""
    var itemList : List<Room?>? = null

    lateinit var listener : ValueEventListener
    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
        get() = statusMessage

    val roomMapping = mapOf(
        "Living Room" to 0,
        "Dining Room" to 1,
        "Kitchen" to 2,
        "Bathroom" to 3,
    )


    init{
        imageUri = null
        roomName = ""
        roomDescription = ""
        imageUrl = ""
        roomKey = ""
    }

    fun getRoomDetails(onGetDataListener: onGetDataListener) {
        listener = repository.getSpaceIdSnapshot(spaceID, onGetDataListener) { r, d ->
            repository.getRooms(r, d)
        }
    }

    fun removeListener(){
        repository.removeListener(listener)
    }

    fun checkExistingRooms(){
        spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                var isRegistered = false
//                loop@ for (rooms in dataSnapshot.child(spaceID).child(Constants.ROOMS).children) {
//                    if (roomMapping[roomName] == rooms.child(Constants.ROOMID)
//                            .getValue(Int::class.java) ?: break@loop
//                    ) {
//                        statusMessage.value = Event("This room is already registered to Firebase. Use another room")
//                        isRegistered = true
//                        break@loop
//                    }
//                }
//                if (!isRegistered) {
                statusMessage.value = Event("Your room is registered to Firebase")
                val imageBitmap =
                    MediaStore.Images.Media.getBitmap(
                        getApplication<Application>().contentResolver,
                        imageUri
                    )
                sendImagetoFirebase(imageBitmap)
//                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun updateExistingRoom(onGetDataListener: onGetDataListener) {
        if(imageUri != null) {
            val imageBitmap =
                MediaStore.Images.Media.getBitmap(
                    getApplication<Application>().contentResolver,
                    imageUri
                )
            Log.d(TAG,"Room Key Value: $roomKey")
            repository.sendDataToFireBase(imageBitmap, roomKey) { uri, key ->
                spaceRef.child(spaceID)
                    .child(Constants.ROOMS)
                    .child(roomKey)
                    .setValue(Room(roomName, roomDescription, uri.toString()))
                    .addOnSuccessListener {
                        statusMessage.value = Event("Room Update Successful")
                        onGetDataListener.onSuccess(Response())
                    }.addOnFailureListener{
                        statusMessage.value = Event("Room Update Failed")
                    }
            }
        }else {
            val updates : MutableMap<String, Any> = HashMap()
            updates["room"] = roomName
            updates["description"] = roomDescription
            spaceRef.child(spaceID)
                .child(Constants.ROOMS)
                .child(roomKey).updateChildren(updates).addOnSuccessListener {
                    statusMessage.value = Event("Room Update Successful")
                    onGetDataListener.onSuccess(Response())
                }.addOnFailureListener{
                    statusMessage.value = Event("Room Update Failed")
                }
        }

    }

    private fun sendImagetoFirebase(imageBitmap: Bitmap) {
        val room = Room(roomName, roomDescription,)
        val key = spaceRef.push().key.toString()
        repository.sendDataToFireBase(imageBitmap, key){uri, k ->
//            repository.putRoom(uri, dC as Room, temp, name, key)
            room.imageUrl = uri.toString()
            spaceRef.child(spaceID)
                .child(Constants.ROOMS)
                .child(k)
                .setValue(room)
        }
    }
}
//    fun getRoomDetails2() {
//        var roomListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (space in snapshot.children) {
//                    if (space.child(Constants.SPACEID).getValue(Int::class.java)!! == tempID) {
//                        response.objectList = space.child(Constants.ROOMS).children.map { snapShot ->
//                            snapShot.getValue(Room::class.java)
//                        }
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                response.exception = Throwable(error.toString())
//            }
//        }
//        repository.addEventListener(roomListener)
//    }