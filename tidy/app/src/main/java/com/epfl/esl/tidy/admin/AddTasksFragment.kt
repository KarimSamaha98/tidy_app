package com.epfl.esl.tidy.admin


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.epfl.esl.tidy.AddRoomsFragment
import com.epfl.esl.tidy.AddRoomsFragmentArgs
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.databinding.FragmentAddTasksBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class AddTasksFragment : Fragment() {

    private lateinit var binding: FragmentAddTasksBinding
    private lateinit var viewModel: AddTasksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_tasks,
            container, false
        )

        val args : AddRoomsFragmentArgs by navArgs()
        viewModel = ViewModelProvider(this).get(AddTasksViewModel::class.java)
        viewModel.spaceID = args.spaceID

        binding.AddTaskButton.setOnClickListener {
            viewModel.newTask = binding.taskName.text.toString()
            viewModel.taskDescription = binding.taskDescription.text.toString()

            if (viewModel.newTask == "") {
                Toast.makeText(context, "Enter the tile of the task.", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var taskExists = false

                        // Check if task already exists
                        loop@ for (task in dataSnapshot.children) {
                            if (task.child("Title")
                                    .getValue(String::class.java)!! == viewModel.newTask
                            ) {
                                taskExists = true

                                if (task.child("Description")
                                        .getValue(String::class.java)!! == viewModel.taskDescription){
                                    Toast.makeText(
                                        context, "Task and description already exists.",
                                        Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    viewModel.taskKey = task.key.toString()
                                    viewModel.tasksRef.child(viewModel.taskKey).child("Description")
                                        .setValue(viewModel.taskDescription)
                                    Toast.makeText(
                                        context, "Task exists, updating description.",
                                        Toast.LENGTH_SHORT).show()
                                }
                                break@loop
                                }
                            }

                        // If task does not exist we create new task
                        if (!taskExists) {
                            viewModel.taskKey = viewModel.tasksRef.push().key.toString()
                            // Send tasks data to firebase
                            viewModel.sendDataToFireBase()

                            Toast.makeText(
                                context,"New task created",
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