package com.epfl.esl.tidy.services

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.epfl.esl.tidy.tasks.CurrentTaskClass
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)
    val tempID : String = "12325345345erst22"

    var allTasks : MutableList<String> = mutableListOf()
    var allUsers : MutableList<String> = mutableListOf()
    var unfinishedTasks : MutableList<String> = mutableListOf()

    var dueDate : String = ""
    val reassignTaskDay : Int = 2 // indexed starting at 1 for Sunday

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        assignTasks()
    }

    private fun addTaskToFirebase(){
        // calculate total number of tasks and users
        val numUsers = allUsers.size
        val numTasks = allTasks.size
        println("Number of users is $numUsers and number of tasks is $numTasks")
        val newTasks = allTasks.shuffled() // shuffle tasks

        for ((index, taskKey) in newTasks.withIndex()) {
            // Use modulus to assign each task
            val userIdx = index.mod(numUsers)
            val userKey = allUsers[userIdx]
            val currentTask = CurrentTaskClass(taskKey, userKey, dueDate)

            //Add to firebase
            val key = spaceRef.push().key.toString()
            spaceRef.child(tempID).child(Constants.CURRTASK).child(key).setValue(currentTask)
            //println("user $userKey has task $taskKey")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDate() : Boolean {
        val cal: Calendar = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        var assignNewTasks = false

        //println("DAY $dayOfWeek")
        if (dayOfWeek == reassignTaskDay){
            assignNewTasks = true
            val today = LocalDate.now()
            println(today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)))
            dueDate = today.plusDays(6).toString() // set due date to be a week later
            println(dueDate)
        }
        return assignNewTasks
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun assignTasks() {
        val assignNewTasks = checkDate()
        if (assignNewTasks) {
            spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val space = dataSnapshot.child(tempID)
                    // Gets the unique keys for all possible tasks for a space
                    for (task in space.child(Constants.TASKS).children) {
                        allTasks.add(task.key.toString())
                    }
                    // Gets the unique keys for all users in space
                    for (user in space.child(Constants.USERS).children) {
                        allUsers.add(user.key.toString())
                    }
                    // Gets all current tasks
                    for (undone in space.child(Constants.CURRTASK).children){
                        unfinishedTasks.add(undone.key.toString())
                    }
                    addTaskToFirebase()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(ContentValues.TAG, databaseError.message)
                }
            })
        }
    }
}