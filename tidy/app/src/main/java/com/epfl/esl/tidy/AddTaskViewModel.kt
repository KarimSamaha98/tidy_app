package com.epfl.esl.tidy

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddTaskViewModel : ViewModel() {
    var roomKey: String = ""
    var taskKey: String = ""
    var newTask: String = ""
    var taskRoom: String = ""
    var taskDescription: String = ""

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val tasksRef: DatabaseReference = database.getReference("Tasks")

    fun sendDataToFireBase() {
        tasksRef.child(taskKey).child("Title").setValue(newTask)
        tasksRef.child(taskKey).child("Description").setValue(taskDescription)
    }
}