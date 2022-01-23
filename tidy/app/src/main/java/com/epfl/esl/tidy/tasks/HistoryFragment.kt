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
import com.epfl.esl.tidy.MainActivity
import com.epfl.esl.tidy.databinding.FragmentHistoryBinding
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.utils.Constants.PREVTASK
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class HistoryFragment : Fragment() {
    private lateinit var binding : FragmentHistoryBinding
    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        viewModel.spaceID = MainActivity.loginInfo.space_id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history,
            container, false)

        binding.recyclerViewHist.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)
        //viewModel.spaceID = (activity as MainActivity).loginInfo.space_id

        // Get all task histories
        viewModel.spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val space = dataSnapshot.child(viewModel.spaceID)
                viewModel.displayTasksList = ArrayList<PastTaskClass>()

                // Gets the previous tasks
                for (task in space.child(PREVTASK).children) {
                    viewModel.displayTask = PastTaskClass()

                    println(task)
                    val userKey = task.child("user").getValue(String::class.java)
                    println("USER $userKey")
                    val data = task.getValue(PastTaskClass::class.java)!!
                    viewModel.displayTask.task = data.task
                    viewModel.displayTask.user = data.user
                    viewModel.displayTask.date_due = data.date_due
                    viewModel.displayTask.date_complete = data.date_complete

                    viewModel.displayTasksList.add(viewModel.displayTask)
                }

                viewModel.displayTasksList.reverse()
                // Adapter class is initialized and list is passed in the param.
                val histAdapter = HistoryAdapter(context = context,
                    viewModel.displayTasksList.asReversed())

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
