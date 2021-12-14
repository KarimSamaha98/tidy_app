package com.epfl.esl.tidy

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.epfl.esl.tidy.databinding.FragmentOverviewBinding
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class OverviewFragment : Fragment() {

    companion object {
        fun newInstance() = OverviewFragment()
    }

    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding : FragmentOverviewBinding

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val roomRef: DatabaseReference = database.getReference("Space_IDs")
    var roomList : ArrayList<String> = arrayListOf()
    var roomImageList : ArrayList<Drawable> = arrayListOf()

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
                            val room = rooms.child("Attribute").getValue(String::class.java)!!
                            roomList.add(room)
                            var roomName : String = rooms.child("photo_URL").getValue(String::class.java)!!
                            var roomImage = viewModel.storageRef.child("RoomImages/"+roomName+".jpg")
//                            var bytes = roomImage.getBytes(Long.MAX_VALUE)
//                            val image: Drawable = BitmapDrawable(
//                                resources,
//                                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//                            )
                            roomImage.getBytes(Long.MAX_VALUE).addOnSuccessListener { byteArray ->
                                val image: Drawable = BitmapDrawable(
                                    resources,
                                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                                )
                                //                                binding.welcomeImage.setImageDrawable(image)
                                roomImageList.add(image)
                            }.addOnFailureListener {
                                val image : Drawable = getResources().getDrawable(R.drawable.living_room)
                                roomImageList.add(image)
                                Toast.makeText(context,"Image read was unsuccessful.",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                val roomAdapter = context?.let{ RoomAdapter(context=it, items = roomList, items_2 = roomImageList)}

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
//        viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)
        // TODO: Use the ViewModel
    }

}