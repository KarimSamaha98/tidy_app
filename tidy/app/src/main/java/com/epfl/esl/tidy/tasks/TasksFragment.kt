package com.epfl.esl.tidy.tasks

import android.content.ContentValues.TAG
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.esl.tidy.MainActivity
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.utils.SwipeGesture
import com.epfl.esl.tidy.databinding.FragmentTasksBinding
import com.epfl.esl.tidy.signin.UserDataClass
import com.epfl.esl.tidy.utils.Constants
import com.epfl.esl.tidy.utils.Constants.CURRTASK
import com.epfl.esl.tidy.utils.Constants.TASKS
import com.epfl.esl.tidy.utils.Constants.USERS
import com.google.firebase.database.*
import java.util.ArrayList

class TasksFragment : Fragment() {
    private lateinit var binding : FragmentTasksBinding
    private lateinit var viewModel: TasksViewModel

    companion object{
        var tasksList  = arrayListOf<TasksAdapterClass>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TasksViewModel::class.java)
        viewModel.spaceID = MainActivity.loginInfo.space_id
        viewModel.myKey = MainActivity.loginInfo.key
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasks,
            container, false)

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)

        // Get all current tasks
        viewModel.spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewModel.displayTasksList = ArrayList<TasksAdapterClass>()
                val space = dataSnapshot.child(viewModel.spaceID)
                // Gets the current tasks
                for (task in space.child(CURRTASK).children) {
                    viewModel.displayTask = TasksAdapterClass()
                    // get current task info
                    val currentTask = task.getValue(CurrentTaskClass::class.java)!!
                    val taskName = space.child(TASKS).child(currentTask.task_key)
                        .child(Constants.TASK_NAME).getValue(String::class.java)!!
                    val taskRoom = space.child(TASKS).child(currentTask.task_key)
                        .child(Constants.TASK_ROOM).getValue(String::class.java)!!
                    viewModel.displayTask.task_name =
                        taskName.plus(" in ").plus(taskRoom.lowercase())
                    viewModel.displayTask.user = space.child(USERS).child(currentTask.user_key)
                        .child("First_Name").getValue(String::class.java)!!

                    viewModel.displayTask.due_date = currentTask.due
                    viewModel.displayTask.task_key = task.key.toString()
                    var currentUserKey = space.child(USERS).child(currentTask.user_key)
                        .child("Key").getValue(String::class.java)!!
                    if (currentUserKey == viewModel.myKey) {
                        viewModel.displayTask.rank = 1
                    } else {
                        viewModel.displayTask.rank = 0
                    }

                    viewModel.displayTasksList.add(viewModel.displayTask)
                }

                // Adapter class is initialized and list is passed in the param.
                viewModel.displayTasksList.sortByDescending { it.rank }
                val tasksAdapter = TasksAdapter(context = context,
                    viewModel.displayTasksList)

                tasksList = viewModel.displayTasksList

                binding.recyclerViewTasks.adapter = tasksAdapter
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        })

        // Swipe right
        val swipeGesture = object : SwipeGesture(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                //only allow users to complete their own tasks
                if (tasksList[position].rank == 1){
                    viewModel.completeTask(position)
                    binding.recyclerViewTasks.adapter?.notifyItemRemoved(position)
                    Toast.makeText(context, "Task completed!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTasks)

        return binding.root
    }
}