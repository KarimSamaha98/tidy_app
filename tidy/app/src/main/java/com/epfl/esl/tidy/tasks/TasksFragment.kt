package com.epfl.esl.tidy.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.databinding.FragmentTasksBinding
import com.epfl.esl.tidy.utils.Constants
import com.epfl.esl.tidy.utils.Constants.SPACEID
import com.epfl.esl.tidy.utils.Constants.TASKS
import com.epfl.esl.tidy.utils.Constants.USERS
import com.google.firebase.database.*
import java.util.*

class TasksFragment : Fragment() {
    private lateinit var binding : FragmentTasksBinding
    private lateinit var viewModel: TasksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this).get(TasksViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_tasks,
            container, false)

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)


        binding.AddTaskButton.setOnClickListener {view: View ->
            Navigation.findNavController(view).navigate(R.id.action_TasksFragment_to_addTasks)
        }

        viewModel.spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (space in dataSnapshot.children) {
                    println("MYSPACE $space")
                    if (space.key.toString() == viewModel.tempID) {
                        // Gets the unique keys for all possible tasks for a space
                        for (task in space.child(TASKS).children){
                            viewModel.allTasks.add(task.key.toString())
                        }
                        // Gets the unique keys for all users in space
                        for (user in space.child(USERS).children){
                            viewModel.allUsers.add(user.key.toString())
                        }
                    }
                }
                // get day of the week; assign tasks on 1 day only
                val cal = Calendar.getInstance()
                // indexed starting at 1 for Sunday
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                println("DAY $dayOfWeek")
                if (dayOfWeek == 6){
                    viewModel.assignTasks()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })



        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // grab all unique tasks

    }
}