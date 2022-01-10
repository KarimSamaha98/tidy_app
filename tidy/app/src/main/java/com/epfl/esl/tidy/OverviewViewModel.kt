package com.epfl.esl.tidy

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class OverviewViewModel : ViewModel() {
    var storageRef = Firebase.storage.reference
    val tempID = 0

}