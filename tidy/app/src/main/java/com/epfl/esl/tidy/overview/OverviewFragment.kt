package com.epfl.esl.tidy.overview

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.Room
import com.epfl.esl.tidy.databinding.FragmentOverviewBinding
import com.google.firebase.database.*

class OverviewFragment : Fragment() {

    companion object {
        fun newInstance() = OverviewFragment()
    }

    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding : FragmentOverviewBinding

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val roomRef: DatabaseReference = database.getReference("Space_IDs")
    var roomList : ArrayList<Room> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview,
            container, false)
        viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)

        binding.recyclerViewItems.layoutManager = GridLayoutManager(activity, 2)

//        TODO move to view model
        val roomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(space in snapshot.children) {
                    if (space.child("Space_ID").getValue(Int::class.java)!! == viewModel.tempID) {
                        for(rooms in space.child("Rooms").children) {
//                            TODO is this correct way with dealing with this. I force the value to not be null with !!, but could also add in ? everywhere.
                            val room : Room = rooms.getValue(Room::class.java)!!
                            roomList.add(room)
                        }
                    }
                }
                val roomAdapter = context?.let{ RoomAdapter(context=it, items = roomList) }

                binding.recyclerViewItems.adapter = roomAdapter
                binding.progressCircular.visibility = View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show()
                binding.progressCircular.visibility = View.INVISIBLE
            }

        }
        roomRef.addListenerForSingleValueEvent(roomListener)


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)
        // TODO: Use the ViewModel
    }

}