package com.epfl.esl.tidy

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.epfl.esl.tidy.databinding.FragmentOverviewBinding
import com.google.firebase.database.*

class OverviewFragment : Fragment() {

    companion object {
        fun newInstance() = OverviewFragment()
    }

    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding : FragmentOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview,
            container, false)
        viewModel = ViewModelProvider(this)[OverviewViewModel::class.java]

        binding.recyclerViewItems.layoutManager = GridLayoutManager(activity, 2)

        viewModel.getResponse().observe(viewLifecycleOwner) {
            if(it.exception != null) {
                val roomAdapter = RoomAdapter(
                    context = context,
                    items = it.rooms!!,
                )
                binding.recyclerViewItems.adapter = roomAdapter
                binding.progressCircular.visibility = View.INVISIBLE
            }
            else {
//                do something
            }

        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)
//         TODO: Use the ViewModel
    }
}