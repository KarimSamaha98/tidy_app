package com.epfl.esl.tidy.admin

import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddTasksViewModel : ViewModel() {
    var newTask: String = ""
    var newRoom: String = ""
    var taskDescription: String = ""
    var spaceID: String = ""

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)

    var allRooms : ArrayList<String> = ArrayList()

    fun sendDataToFireBase() {
        val key = spaceRef.push().key.toString()

        val myNewTask = TaskDataClass(newTask, newRoom, taskDescription)
        spaceRef.child(spaceID).child(Constants.TASKS).child(key).setValue(myNewTask)
    }
}