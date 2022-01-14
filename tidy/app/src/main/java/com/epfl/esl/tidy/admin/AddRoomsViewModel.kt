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
    val repository = FirebaseRepository()
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)

    var imageUri: Uri?
    var roomName: String
    var roomDescription : String
    var imageUrl : String
    val tempID = 0
    var tempID_key : String
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
        tempID_key = ""
    }

    fun getRoomDetails(onGetDataListener: onGetDataListener) {
        listener = repository.getSpaceIdSnapshot(tempID, onGetDataListener) { r, d ->
            repository.getRooms(r, d)
        }
    }

    fun removeListener(){
        repository.removeListener(listener)
    }

    fun checkExistingRooms(){
        spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val space = repository.checkSpaceId(dataSnapshot, tempID)
                if (space != null) {
                    var isRegistered = false
                    tempID_key = space.key.toString()
                    loop@ for (rooms in space.child(Constants.ROOMS).children) {
                        if (roomMapping[roomName] == rooms.child(Constants.ROOMID)
                                .getValue(Int::class.java) ?: break@loop
                        ) {
                            statusMessage.value = Event("This room is already registered to Firebase. Use another room")
                            isRegistered = true
                            break@loop
                        }
                    }
//                    TODO do I need this check
                    if (!isRegistered && imageUri != null) {
                        statusMessage.value = Event("Your room is registered to Firebase")
                        val imageBitmap =
                            MediaStore.Images.Media.getBitmap(
                                getApplication<Application>().contentResolver,
                                imageUri
                            )
                        sendImagetoFirebase(imageBitmap)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendImagetoFirebase(imageBitmap: Bitmap) {
        val saveString = tempID_key
        val room = Room(roomName, roomDescription,)
        repository.sendDataToFireBase(imageBitmap, saveString, Constants.ROOMS, tempID_key, room){uri, dC, temp, name ->
            repository.putRoom(uri, dC as Room, temp, name)
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
}