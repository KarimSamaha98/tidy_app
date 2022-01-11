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

        //viewModel = ViewModelProvider(this).get(AddTaskViewModel::class.java)

        binding.AddTaskButton.setOnClickListener {
            viewModel.taskRoom = binding.taskRoom.text.toString()
            viewModel.newTask = binding.taskName.text.toString()
            viewModel.taskDescription = binding.taskDescription.text.toString()
            if (viewModel.taskRoom == "") {
                Toast.makeText(context, "Enter location of the task.", Toast.LENGTH_SHORT).show()
            }
            else if (viewModel.newTask == "") {
                Toast.makeText(context, "Enter the tile of the task.", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.taskKey = Random().nextInt().toString()

                viewModel.tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var roomExists: Boolean = false
                        var taskExists: Boolean = false

                        // Check if room and task already exists
                        for (location in dataSnapshot.children) {
                            if (location.child("Room")
                                    .getValue(String::class.java)!! == viewModel.taskRoom
                            ) {
                                roomExists = true

                                for (task in location.children){
                                    if (task.child("Task")
                                            .getValue(String::class.java)!! == viewModel.newTask
                                    ) {
                                        taskExists = true
                                        android.widget.Toast.makeText(
                                            context,
                                            "Location and task pair already exist.",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                if (!taskExists) {
                                    viewModel.roomKey = location.key.toString()
                                    // Send tasks data to firebase
                                    viewModel.sendDataToFireBase()
                                }
                            }
                        }

                        // If room does not exist we create a new room
                        if (!roomExists) {
                            viewModel.roomKey = Random().nextInt().toString()
                            viewModel.tasksRef.child(viewModel.roomKey).child("Room")
                                .setValue(viewModel.taskRoom)
                            Toast.makeText(
                                context,
                                "Creating a new location for tasks in Firebase",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Send tasks data to firebase
                            viewModel.sendDataToFireBase()
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