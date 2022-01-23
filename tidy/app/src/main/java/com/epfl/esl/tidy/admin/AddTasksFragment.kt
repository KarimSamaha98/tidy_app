package com.epfl.esl.tidy.admin


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.epfl.esl.tidy.AddRoomsFragmentArgs
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.databinding.FragmentAddTasksBinding
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AddTasksFragment : Fragment() {

    private lateinit var binding: FragmentAddTasksBinding
    private lateinit var viewModel: AddTasksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_tasks,
            container, false
        )

        // Add adapters for dropdown menu
        val tasks = resources.getStringArray(R.array.tasks)
        val tasksAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, tasks)
        binding.addTasksDropdown.setAdapter(tasksAdapter)

        val rooms = resources.getStringArray(R.array.rooms)
        val roomsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, rooms)
        binding.addRoomsDropdown.setAdapter(roomsAdapter)

        val args : AddRoomsFragmentArgs by navArgs()
        viewModel = ViewModelProvider(this).get(AddTasksViewModel::class.java)
        viewModel.spaceID = args.spaceID

        binding.AddTaskButton.setOnClickListener {
            viewModel.newTask = binding.addTasksDropdown.text.toString()
            viewModel.newRoom = binding.addRoomsDropdown.text.toString()
            viewModel.taskDescription = binding.taskDescription.text.toString()

            if (viewModel.newTask == "") {
                Toast.makeText(context, "Please enter a task.", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var taskExists = false
                        val space = dataSnapshot.child(viewModel.spaceID)
                        // Check if task already exists
                        for (task in space.child(Constants.TASKS).children) {
                            if (task.child("Name")
                                    .getValue(String::class.java)!! == viewModel.newTask
                            ) {
                                if (task.child("Room")
                                        .getValue(String::class.java)!! == viewModel.newRoom){
                                    taskExists = true
                                    if (task.child("Description")
                                            .getValue(String::class.java)!! == viewModel.taskDescription){
                                        Toast.makeText(
                                            context, "Task and room combination already exists.",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                    else{
                                        val taskKey = task.key.toString()
                                        viewModel.spaceRef.child(viewModel.spaceID).child(Constants.TASKS)
                                            .child(taskKey).child("Description")
                                            .setValue(viewModel.taskDescription)
                                        Toast.makeText(
                                            context, "Description of existing task updated.",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                        // If task does not exist we create new task
                        if (!taskExists) {
                            // Send tasks data to firebase
                            viewModel.sendDataToFireBase()

                            Toast.makeText(
                                context,"New task created!",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddTasksViewModel::class.java)
    }
}