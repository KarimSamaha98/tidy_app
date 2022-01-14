package com.epfl.esl.tidy.tasks

import android.os.Build
import android.os.Bundle
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
import com.epfl.esl.tidy.SwipeGesture
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

        // Adapter class is initialized and list is passed in the param.
        var tasksAdapter = TasksAdapter(context = context,
            task_names = viewModel.tasks_list,
            users = viewModel.user_list,
            due_dates = viewModel.dueDate_list)

        // Checks date and assigns new tasks if needed
        viewModel.checkDate()
        if (viewModel.assignTasks) {
            viewModel.spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val space = dataSnapshot.child(viewModel.tempID)
                    // Gets the unique keys for all possible tasks for a space
                    for (task in space.child(TASKS).children) {
                        viewModel.allTasks.add(task.key.toString())
                    }
                    // Gets the unique keys for all users in space
                    for (user in space.child(USERS).children) {
                        viewModel.allUsers.add(user.key.toString())
                    }
                    viewModel.assignTasks()
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

        // Get all current tasks
        viewModel.spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val space = dataSnapshot.child(viewModel.tempID)
                // Gets the current tasks
                for (task in space.child(CURRTASK).children) {
                    // get task name
                    val taskKey = task.child("task_key").getValue(String::class.java)!!
                    val userKey = task.child("user_key").getValue(String::class.java)!!
                    val dueDate = task.child("due").getValue(String::class.java)!!

                    val taskName = space.child(TASKS).child(taskKey)
                        .child("Name").getValue(String::class.java)!!
                    val taskRoom = space.child(TASKS).child(taskKey)
                        .child("Room").getValue(String::class.java)!!
                    viewModel.tasks_list.add(taskName.plus(" in ").plus(taskRoom.lowercase()))
                    viewModel.user_list.add(space.child(USERS).child(userKey)
                        .child("Name").getValue(String::class.java)!!)
                    viewModel.dueDate_list.add(dueDate)
                    viewModel.tasks_key_list.add(task.key.toString())
                }

                // Adapter class is initialized and list is passed in the param.
                tasksAdapter = TasksAdapter(context = context,
                    task_names = viewModel.tasks_list,
                    users = viewModel.user_list,
                    due_dates = viewModel.dueDate_list)

                binding.recyclerViewTasks.adapter = tasksAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (viewModel.tasks_key_list.size == 0){
            val text = "All done"
        }
        super.onActivityCreated(savedInstanceState)
    }
}