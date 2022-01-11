package com.epfl.esl.tidy

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class AddTaskViewModel : ViewModel() {
    var roomKey: String = ""
    var taskKey: String = ""
    var newTask: String = ""
    var taskRoom: String = ""
    var taskDescription: String = ""

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val tasksRef: DatabaseReference = database.getReference("Tasks")
    var storageRef = Firebase.storage.reference

    fun sendDataToFireBase() {
        tasksRef.child(roomKey).child(taskKey).child("Task")
            .setValue(newTask)
        tasksRef.child(roomKey).child(taskKey).child("Description")
            .setValue(taskDescription)
    }
}