package com.epfl.esl.tidy.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.epfl.esl.tidy.MainActivity
import com.epfl.esl.tidy.tasks.CurrentTaskClass
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)
    var spaceID : String = ""

    var allTasks : MutableSet<String> = mutableSetOf()
    var allUsers : MutableList<String> = mutableListOf()
    var allRooms : MutableList<String> = mutableListOf()
    var unfinishedTasks : MutableSet<String> = mutableSetOf()

    @SuppressLint("SimpleDateFormat")
    val sdf = SimpleDateFormat("dd-MM-yyyy")

    override fun onReceive(context: Context?, intent: Intent?) {
        spaceID = MainActivity.loginInfo.space_id
        Toast.makeText(context, "New tasks will be assigned!", Toast.LENGTH_SHORT).show()
        assignTasks()
    }

    private fun addTaskToFirebase(){
        val cal: Calendar = Calendar.getInstance()

        cal.add(Calendar.DATE, 2) // set due date to be 2 days later for testing
        val dueDate = sdf.format(cal.time)

        // calculate total number of tasks and users
        val numUsers = allUsers.size
        val numTasks = allTasks.size

        // we remove unfinished tasks from the pool of all tasks
        val availableTasks = allTasks.minus(unfinishedTasks)

        println("Number of users is $numUsers and number of tasks is $numTasks")
        val newTasks = availableTasks.shuffled() // shuffle tasks

        for ((index, taskKey) in newTasks.withIndex()) {
            // Use modulus to assign each task
            val userIdx = index.mod(numUsers)
            val userKey = allUsers[userIdx]
            val currentTask = CurrentTaskClass(taskKey, userKey, dueDate.toString())

            //Add to firebase
            val key = spaceRef.push().key.toString()
            spaceRef.child(spaceID).child(Constants.CURRTASK).child(key).setValue(currentTask)
        }
    }

    private fun assignTasks() {
        spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val space = dataSnapshot.child(spaceID)
                // Gets the unique keys for all possible tasks for a space
                for (task in space.child(Constants.TASKS).children) {
                    allTasks.add(task.key.toString())
                }
                // Gets the unique keys for all users in space
                for (user in space.child(Constants.USERS).children) {
                    allUsers.add(user.key.toString())
                }
                // Gets all unfinished task keys (which are unique to the space)
                for (currentTask in space.child(Constants.CURRTASK).children){
                    unfinishedTasks.add(currentTask.child("task_key").getValue(String::class.java)!!)
                }
                // Get all existing rooms
                for (room in space.child(Constants.ROOMS).children){
                    allRooms.add(room.key.toString())
                }
                addTaskToFirebase()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(ContentValues.TAG, databaseError.message)
            }
        })
    }
}