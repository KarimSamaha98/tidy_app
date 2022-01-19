package com.epfl.esl.tidy

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.epfl.esl.tidy.admin.AddRoomsViewModel
import com.epfl.esl.tidy.admin.Room
import com.epfl.esl.tidy.databinding.AddRoomsFragmentBinding
import com.epfl.esl.tidy.overview.RoomAdapter
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

//TODO AddRooms doesn't update once a room is added, have to leave and come back. Maybe need different listener
//TODO also doesn't check for dupliate rooms anymore... stopped working.
//TODO Make Rooms a dropdown to select and add in rather than a free text. Then need to update the Room dataclass
//TODO If you click on an item then you should be able to edits its information.
//TODO move Firebase code to Firebase repository, but it kind of sucks to refactor... keep running into problems.

class AddRoomsFragment : Fragment() {

    companion object {
        fun newInstance() = AddRoomsFragment()
    }
    private val TAG : String = "AddRoomsFragment"
    private lateinit var viewModel: AddRoomsViewModel
    private lateinit var binding: AddRoomsFragmentBinding
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)
    private var storageRef: StorageReference = Firebase.storage.reference

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                viewModel.imageUri = imageUri
                binding.roomImage.setImageURI(imageUri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.add_rooms_fragment,
            container, false
        )
        viewModel = ViewModelProvider(this).get(AddRoomsViewModel::class.java)

        binding.recyclerViewRooms.layoutManager = GridLayoutManager(activity, 3)

        binding.roomImage.setOnClickListener {
            val imgIntent = Intent(Intent.ACTION_GET_CONTENT)
            imgIntent.setType("image/*")
            resultLauncher.launch(imgIntent)
        }

        viewModel.getRoomDetails(object : onGetDataListener {
            override fun onSuccess(response: Response) {
                val roomAdapter = RoomAdapter(
                    context = context,
//                  TODO: have to be careful this will give nullpointer exception if response.objectList doesnt get a value
                    items = response.objectList as List<Room?>,
                )
                binding.recyclerViewRooms.adapter = roomAdapter
                binding.progressCircular.visibility = View.INVISIBLE
            }

            override fun onFailure(response: Response) {
                Log.d(TAG, "Listener Failed with error: ${response.exception.toString()}")
            }
        })

        binding.AddRoomButton.setOnClickListener {
            viewModel.roomName = binding.roomName.text.toString()
            viewModel.roomDescription = binding.roomDescription.text.toString()

            if (viewModel.roomName == "") {
                Toast.makeText(context, "Enter a room name.", Toast.LENGTH_SHORT).show()
            } else if (viewModel.roomDescription == "") {
                Toast.makeText(context, "Enter a room description.", Toast.LENGTH_SHORT).show()
            } else if (viewModel.imageUri == null) {
                Toast.makeText(context, "Pick an image for the room", Toast.LENGTH_SHORT).show()
            } else {
//              TODO: Refactor out of Fragment.
                viewModel.spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        var isRegistered = false

                        loop@ for (space in dataSnapshot.children) {
                            if (space.child("Space_ID")
                                    .getValue(Int::class.java)!! == viewModel.tempID
                            ) {
                                viewModel.tempID_key = space.key.toString()
                                for (rooms in space.child("Rooms").children) {

                                    if (viewModel.roomMapping[viewModel.roomName] == rooms.child("Room_ID")
                                            .getValue(Int::class.java) ?: break@loop
                                    ) {
                                        Toast.makeText(
                                            context,
                                            "This room is already registered to Firebase. Use another room",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        isRegistered = true
                                        break@loop
                                    }
                                }
                            }
                        }
                        if (isRegistered == false) {
                            Toast.makeText(
                                context,
                                "Your room is registered to Firebase",
                                Toast.LENGTH_SHORT
                            ).show()
                            var imageBitmap =
                                MediaStore.Images.Media.getBitmap(context?.contentResolver, viewModel.imageUri)
                            viewModel.sendImagetoFirebase(imageBitmap)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }




        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

//    TODO move this to view model.
}