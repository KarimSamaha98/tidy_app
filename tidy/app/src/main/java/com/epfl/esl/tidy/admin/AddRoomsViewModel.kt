package com.epfl.esl.tidy.admin

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.datalayer.FirebaseRepository
import com.epfl.esl.tidy.onGetDataListener
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class AddRoomsViewModel : ViewModel() {
    var imageUri: Uri?
    var roomName: String
    var roomDescription : String
    var imageUrl : String
    val tempID = 0
    var tempID_key : String

    val repository = FirebaseRepository()
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val profileRef: DatabaseReference = database.getReference(Constants.PROFILES)
    val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)
    var storageRef: StorageReference = Firebase.storage.reference

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
        repository.getSpaceIdSnapshot(tempID, onGetDataListener) { r, d ->
            repository.getRooms(r, d)
        }
    }

    fun sendImagetoFirebase(imageBitmap: Bitmap) {
        val saveString = tempID.toString() + "_" + roomMapping[roomName].toString()
        val room = Room(roomName, roomDescription,)
        repository.sendDataToFireBase(imageBitmap, saveString, Constants.ROOMS, tempID_key, room){uri, dC, temp, name ->
            repository.putRoom(uri, dC, temp, name)
        }
    }
}