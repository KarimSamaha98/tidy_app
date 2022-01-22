package com.epfl.esl.tidy.tasks

import android.annotation.SuppressLint
import android.os.Build
import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.MainActivity
import com.epfl.esl.tidy.utils.Constants.CURRTASK
import com.epfl.esl.tidy.utils.Constants.PREVTASK
import com.epfl.esl.tidy.utils.Constants.SPACEIDS
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class TasksViewModel : ViewModel() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference(SPACEIDS)
    var tempID : String = ""
    var myKey : String = ""

    var displayTasksList = ArrayList<TasksAdapterClass>()
    var displayTask = TasksAdapterClass()

    @SuppressLint("SimpleDateFormat")
    val sdf = SimpleDateFormat("dd-MM-yyyy")

    fun completeTask(position: Int){
        // add completed task to history in Firebase
        val task = displayTasksList[position]
        val cal: Calendar = Calendar.getInstance()
        val todayDate = sdf.format(cal.time)

        val completedTask = PastTaskClass(task.task_name,
            task.user,
            todayDate,
            task.due_date)

        val key = spaceRef.push().key.toString()
        spaceRef.child(tempID).child(PREVTASK).child(key).setValue(completedTask)

        // remove from current task list in Firebase
        spaceRef.child(tempID).child(CURRTASK).child(task.task_key).removeValue()

        // remove from list
        displayTasksList.removeAt(position)
    }
}