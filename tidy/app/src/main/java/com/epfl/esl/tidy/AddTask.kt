package com.epfl.esl.tidy


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.epfl.esl.tidy.databinding.FragmentAddTasksBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import java.util.*

class AddTask : Fragment() {
    companion object {
        fun newInstance() = AddTask()
    }

    private lateinit var binding: FragmentAddTasksBinding
    private lateinit var viewModel: AddTaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_tasks,
            container, false
        )

        binding.AddTaskButton.setOnClickListener {
            viewModel.newTask = binding.taskName.text.toString()
            viewModel.taskDescription = binding.taskDescription.text.toString()

            if (viewModel.newTask == "") {
                Toast.makeText(context, "Enter the tile of the task.", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.taskKey = Random().nextInt().toString()

                viewModel.tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var taskExists: Boolean = false

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
                                    viewModel.taskKey = viewModel.tasksRef.push().key.toString()
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
                            viewModel.taskKey = Random().nextInt().toString()
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
        viewModel = ViewModelProvider(this).get(AddTaskViewModel::class.java)
    }
}