package com.epfl.esl.tidy.overview

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.epfl.esl.tidy.MainActivity
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.Response
import com.epfl.esl.tidy.admin.Room
import com.epfl.esl.tidy.databinding.FragmentOverviewBinding
import com.epfl.esl.tidy.onGetDataListener

//TODO get supplies recycler view working
//TODO when you click on Tile it opens up the room page. Need onclick listeners for Recycler view


class OverviewFragment : Fragment(), RoomAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = OverviewFragment()
    }
    private val TAG = "OverviewFragment"
    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding : FragmentOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview,
            container, false)
        viewModel = ViewModelProvider(this)[OverviewViewModel::class.java]
        viewModel.spaceID = (activity as MainActivity).loginInfo.space_id

        binding.recyclerViewRooms.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)

        viewModel.getRoomDetails(object : onGetDataListener {
            override fun onSuccess(response: Response) {
                val roomAdapter = OverviewAdapter(context = context)
                roomAdapter.setData(response.objectList as List<DataModel>)


                binding.recyclerViewRooms.adapter = roomAdapter
                binding.progressCircular.visibility = View.INVISIBLE
            }

            override fun onFailure(response: Response) {
                Log.d(TAG, "Listener Failed with error: ${response.exception.toString()}")
            }
        })

        return binding.root
    }

    override fun onItemClick(position: Int) {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)
//         TODO: Use the ViewModel
    }
}