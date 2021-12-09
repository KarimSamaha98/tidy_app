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
import com.epfl.esl.tidy.databinding.FragmentOverviewBinding
import com.google.firebase.database.*

class OverviewFragment : Fragment() {

    companion object {
        fun newInstance() = OverviewFragment()
    }

    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding : FragmentOverviewBinding
    val tempID = 0

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val roomRef: DatabaseReference = database.getReference("Space_IDs")
    var roomList2 : ArrayList<String> = arrayListOf()

    private val roomList : ArrayList<String> = arrayListOf("Living Room", "Dining Room", "Kitchen", "Bathroom")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview,
            container, false)

        binding.recyclerViewItems.layoutManager = GridLayoutManager(activity, 2)

//        TODO move to view model
        val roomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(space in snapshot.children) {
                    if (space.child("Space_ID").getValue(Int::class.java)!! == tempID) {
                        for(rooms in space.child("Rooms").children) {
                            val room = rooms.child("Attribute").getValue(String::class.java)!!
                            roomList2.add(room)
                        }
                    }
                }
                val roomAdapter = context?.let{ RoomAdapter(context=it, items = roomList2)}

                binding.recyclerViewItems.adapter = roomAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        roomRef.addListenerForSingleValueEvent(roomListener)


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)
        // TODO: Use the ViewModel
    }

}