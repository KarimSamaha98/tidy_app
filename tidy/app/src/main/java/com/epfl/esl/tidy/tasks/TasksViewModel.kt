package com.epfl.esl.tidy.tasks

import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.utils.Constants.SPACEIDS
import com.epfl.esl.tidy.utils.Constants.TASKS
import com.epfl.esl.tidy.utils.Constants.USERS
import com.google.firebase.database.*

class TasksViewModel : ViewModel() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference(SPACEIDS)

    var allTasks : MutableList<String> = mutableListOf()
    var allUsers : MutableList<String> = mutableListOf()
    var tempID : String = "12325345345erst22"


    private fun shuffleTasks(): List<String> {
        println(allTasks)
        val shuffledTasks = allTasks.shuffled()
        println(shuffledTasks)
        return shuffledTasks
    }

    fun assignTasks(){
        // calculate total number of tasks and users
        val numUsers = allUsers.size
        val numTasks = allTasks.size
        println("Number of users is $numUsers and number of tasks is $numTasks")

        val newTasks = shuffleTasks()

        for ((index, value) in newTasks.withIndex()) {
            // Use modulus to assign each task
            val userIdx = index.mod(numUsers)
            spaceRef.child(allUsers[userIdx]).child("Current task").setValue(value)
            val currentUser = allUsers[userIdx]
            println("the user is $currentUser")
            println("the element at $index is $value")
        }
    }
}