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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
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

//TODO Make Rooms a dropdown to select and add in rather than a free text. Then need to update the Room dataclass
//TODO If you click on an item then you should be able to edits its information.

class AddRoomsFragment : Fragment() {

    companion object {
        fun newInstance() = AddRoomsFragment()
    }
    private val TAG : String = "AddRoomsFragment"
    private lateinit var viewModel : AddRoomsViewModel
    private lateinit var binding: AddRoomsFragmentBinding

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
                viewModel.checkExistingRooms()
            }
        }

        viewModel.message.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getRoomDetails(object : onGetDataListener {
            override fun onSuccess(response: Response) {
                response.objectList?.let { updateBinding(it as List<Room?>) }
            }
            override fun onFailure(response: Response) {
                response.exception?.let{ Toast.makeText(
                    context,
                    it.toString(),
                    Toast.LENGTH_SHORT
                ).show()}
            }
        })
    }

    fun updateBinding(items: List<Room?>) {
        val roomAdapter = RoomAdapter(
            context = context,
//                 TODO: have to be careful this will give nullpointer exception if response.objectList doesnt get a value
            items = items,
        )
        binding.recyclerViewRooms.adapter = roomAdapter
        binding.progressCircular.visibility = View.INVISIBLE
    }

    override fun onStop() {
        super.onStop()
        viewModel.removeListener()
    }
}