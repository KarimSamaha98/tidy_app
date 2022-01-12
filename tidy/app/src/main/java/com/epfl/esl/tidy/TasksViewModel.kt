package com.epfl.esl.tidy

import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class TasksViewModel : ViewModel() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val tasksRef: DatabaseReference = database.getReference("Tasks")
    var allTasks : MutableSet<String> = mutableSetOf()

    fun getAllTaskKeys() {
        // Gets the unique keys for all possible tasks
        tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (task in dataSnapshot.children) {
                    allTasks.add(task.key.toString())}}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}