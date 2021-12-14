package com.epfl.esl.tidy

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddRoomsViewModel : ViewModel() {
    var imageUri: Uri?
    var roomName: String?
    var roomDescription : String?
    val tempID = 0
    var tempID_key : String?

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val roomRef: DatabaseReference = database.getReference("Space_IDs")
    var storageRef = Firebase.storage.reference

    val roomMapping = mapOf(
        "Living Room" to 0,
        "Dining Room" to 1,
        "Kitchen" to 2,
        "Bathroom" to 3,
    )
    var key: String


    init{
        imageUri = null
        roomName = ""
        roomDescription = ""
        key = roomRef.push().key.toString()
        tempID_key = null
    }
}