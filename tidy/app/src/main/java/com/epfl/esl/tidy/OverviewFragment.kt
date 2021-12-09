package com.epfl.esl.tidy

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.epfl.esl.tidy.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    companion object {
        fun newInstance() = OverviewFragment()
    }

    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding : FragmentOverviewBinding
    private val roomList : ArrayList<String> = arrayListOf("Living Room", "Dining Room", "Kitchen", "Bathroom")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview,
            container, false)

        binding.recyclerViewItems.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val roomAdapter = context?.let{ RoomAdapter(context=it, items = roomList)}

        binding.recyclerViewItems.adapter = roomAdapter

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)
        // TODO: Use the ViewModel
    }

}