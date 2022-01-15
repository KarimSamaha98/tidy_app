package com.epfl.esl.tidy.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.utils.Constants.CURRTASK
import com.epfl.esl.tidy.utils.Constants.PREVTASK
import com.epfl.esl.tidy.utils.Constants.SPACEIDS
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class TasksViewModel : ViewModel() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference(SPACEIDS)
    val tempID : String = "12325345345erst22"
    val myKey : String = "-Mt87SEqJ9dLmEg579L8"

    var allTasks : MutableList<String> = mutableListOf()
    var allUsers : MutableList<String> = mutableListOf()

    var assignTasks : Boolean = false
    var dueDate : String = ""

    val tasks_key_list = ArrayList<String>()
    val tasks_list = ArrayList<String>()
    val dueDate_list = ArrayList<String>()
    val user_list = ArrayList<String>()
    val rank = ArrayList<String>()

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

        for ((index, taskKey) in newTasks.withIndex()) {
            // Use modulus to assign each task
            val userIdx = index.mod(numUsers)
            val userKey = allUsers[userIdx]
            val currentTask = CurrentTaskClass(taskKey, userKey, dueDate)

            //Add to firebase
            val key = spaceRef.push().key.toString()
            spaceRef.child(tempID).child(CURRTASK).child(key).setValue(currentTask)

            println("user $userKey has task $taskKey")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(): LocalDate {
        return LocalDate.now()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun checkDate() {
        val cal: Calendar = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // indexed starting at 1 for Sunday
        println("DAY $dayOfWeek")
        if (dayOfWeek == 1){
            assignTasks = true
            val today = getDate()
            println(today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)))
            dueDate = today.plusDays(7).toString() // set due date to be a week later
            println(dueDate)
        }
        else{
            assignTasks = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun completeTask(position: Int){
        // add completed task to history in Firebase
        val completedTask = PastTaskClass(tasks_list[position],
            user_list[position],
            getDate().toString(),
            dueDate_list[position])

        val key = spaceRef.push().key.toString()
        spaceRef.child(tempID).child(PREVTASK).child(key).setValue(completedTask)

        // remove from current task list in Firebase
        spaceRef.child(tempID).child(CURRTASK).child(tasks_key_list[position]).removeValue()

        // remove from list
        tasks_list.removeAt(position)
        user_list.removeAt(position)
        dueDate_list.removeAt(position)
    }
}