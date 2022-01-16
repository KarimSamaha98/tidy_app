package com.epfl.esl.tidy.tasks

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.utils.SwipeGesture
import com.epfl.esl.tidy.databinding.FragmentTasksBinding
import com.epfl.esl.tidy.utils.Constants.CURRTASK
import com.epfl.esl.tidy.utils.Constants.TASKS
import com.epfl.esl.tidy.utils.Constants.USERS
import com.google.firebase.database.*
import java.util.ArrayList

class TasksFragment : Fragment() {
    private lateinit var binding : FragmentTasksBinding
    private lateinit var viewModel: TasksViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this).get(TasksViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasks,
            container, false)

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)

        binding.AddTaskButton.setOnClickListener {view: View ->
            Navigation.findNavController(view).navigate(R.id.action_TasksFragment_to_addTasks)
        }


        // Get all current tasks
        viewModel.spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val displayTasksList = ArrayList<TasksAdapterClass>()
                val space = dataSnapshot.child(viewModel.tempID)
                // Gets the current tasks
                for (task in space.child(CURRTASK).children) {
                    // get current task info
                    var currentTask: CurrentTaskClass = CurrentTaskClass()
                    currentTask = task.getValue(CurrentTaskClass::class.java)!!
                    val displayTask = TasksAdapterClass()

                    val taskName = space.child(TASKS).child(currentTask.task_key)
                        .child("Name").getValue(String::class.java)!!
                    val taskRoom = space.child(TASKS).child(currentTask.task_key)
                        .child("Room").getValue(String::class.java)!!

                    displayTask.task_name = taskName.plus(" in ").plus(taskRoom.lowercase())
                    displayTask.user = space.child(USERS).child(currentTask.user_key)
                        .child("Name").getValue(String::class.java)!!
                    displayTask.due_date = currentTask.due
                    displayTask.task_key = task.key.toString()
                    if (currentTask.user_key == viewModel.myKey){
                        displayTask.rank = 1
                    }
                    else{
                        displayTask.rank = 0
                    }

                    displayTasksList.add(displayTask)
                }

                // Adapter class is initialized and list is passed in the param.
                viewModel.displayTasksList = displayTasksList
                viewModel.displayTasksList.sortByDescending { it.rank }
                val tasksAdapter = TasksAdapter(context = context,
                    viewModel.displayTasksList)

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
                viewModel.completeTask(position)
                binding.recyclerViewTasks.adapter?.notifyItemRemoved(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTasks)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}