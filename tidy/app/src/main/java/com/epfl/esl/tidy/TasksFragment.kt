package com.epfl.esl.tidy

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.epfl.esl.tidy.databinding.FragmentTasksBinding
import com.google.firebase.database.*

class TasksFragment : Fragment() {
    private lateinit var viewModel: TasksViewModel
    private lateinit var binding : FragmentTasksBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasks,
            container, false)

        binding.recyclerViewTasks.layoutManager = GridLayoutManager(activity, 2)
        return binding.root
    }
}