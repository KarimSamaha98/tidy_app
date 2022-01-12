package com.epfl.esl.tidy


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.epfl.esl.tidy.databinding.FragmentAddTasksBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class AddNewTask : Fragment() {
    companion object {
        fun newInstance() = AddNewTask()
    }

    private lateinit var binding: FragmentAddTasksBinding
    private lateinit var viewModelNew: AddNewTaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_tasks,
            container, false
        )

        binding.AddTaskButton.setOnClickListener {
            viewModelNew.newTask = binding.taskName.text.toString()
            viewModelNew.taskDescription = binding.taskDescription.text.toString()

            if (viewModelNew.newTask == "") {
                Toast.makeText(context, "Enter the tile of the task.", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModelNew.taskKey = Random().nextInt().toString()

                viewModelNew.tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var taskExists = false

                        // Check if task already exists
                        loop@ for (task in dataSnapshot.children) {
                            if (task.child("Title")
                                    .getValue(String::class.java)!! == viewModelNew.newTask
                            ) {
                                taskExists = true

                                if (task.child("Description")
                                        .getValue(String::class.java)!! == viewModelNew.taskDescription){
                                    Toast.makeText(
                                        context, "Task and description already exists.",
                                        Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    viewModelNew.taskKey = viewModelNew.tasksRef.push().key.toString()
                                    viewModelNew.tasksRef.child(viewModelNew.taskKey).child("Description")
                                        .setValue(viewModelNew.taskDescription)
                                    Toast.makeText(
                                        context, "Task exists, updating description.",
                                        Toast.LENGTH_SHORT).show()
                                }
                                break@loop
                                }
                            }

                        // If task does not exist we create new task
                        if (!taskExists) {
                            viewModelNew.taskKey = Random().nextInt().toString()
                            // Send tasks data to firebase
                            viewModelNew.sendDataToFireBase()

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
        viewModelNew = ViewModelProvider(this).get(AddNewTaskViewModel::class.java)
    }
}