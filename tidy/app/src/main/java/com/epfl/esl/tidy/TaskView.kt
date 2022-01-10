package com.epfl.esl.tidy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epfl.esl.tidy.databinding.FragmentSignUpBinding
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.epfl.esl.tidy.databinding.FragmentTaskViewBinding
import com.google.firebase.database.*


class TaskView : Fragment() {

    private lateinit var binding: FragmentTaskViewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_view, container, false)
        binding.email.text = (activity as MainActivity).loginInfo.key

        return binding.root
    }
}


