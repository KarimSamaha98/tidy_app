package com.epfl.esl.tidy.tasks

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.epfl.esl.tidy.databinding.FragmentHistoryBinding
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.admin.Supply
import com.epfl.esl.tidy.utils.Constants
import com.epfl.esl.tidy.utils.Constants.PREVTASK
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.epfl.esl.tidy.tasks.PastTaskClass

class HistoryFragment : Fragment() {
    private lateinit var binding : FragmentHistoryBinding
    private lateinit var viewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history,
            container, false)

        binding.recyclerViewHist.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)

        // Adapter class is initialized and list is passed in the param.
        var histAdapter = HistoryAdapter(context = context,
            tasks = viewModel.task_list,
            users = viewModel.user_list,
            due_dates = viewModel.dueDate_list,
            complete_dates = viewModel.completeDate_list)

        // Get all task histories
        viewModel.spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val space = dataSnapshot.child(viewModel.tempID)
                // Gets the previous tasks
                for (task in space.child(PREVTASK).children) {
                    println(task)
                    val userKey = task.child("user").getValue(String::class.java)
                    println("USER $userKey")
                    var data: PastTaskClass = PastTaskClass()

                    data = task.getValue(PastTaskClass::class.java)!!
                    //println("DATA $data")
                    viewModel.task_list.add(data!!.task)
                    viewModel.user_list.add(data.user)
                    viewModel.dueDate_list.add(data.date_due)
                    viewModel.completeDate_list.add(data.date_complete)
                }

                // Adapter class is initialized and list is passed in the param.
                histAdapter = HistoryAdapter(context = context,
                    tasks = viewModel.task_list,
                    users = viewModel.user_list,
                    due_dates = viewModel.dueDate_list,
                    complete_dates = viewModel.completeDate_list)

                binding.recyclerViewHist.adapter = histAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message) //Don't ignore errors!
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}
