package com.epfl.esl.tidy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.epfl.esl.tidy.admin.AddRoomsViewModel
import com.epfl.esl.tidy.admin.Room
import com.epfl.esl.tidy.databinding.AddRoomsFragmentBinding
import com.epfl.esl.tidy.overview.RoomAdapter
import com.squareup.picasso.Picasso

//TODO Make Rooms a dropdown to select and add in rather than a free text. Then need to update the Room dataclass
//TODO Figure out clearing informaiton and updating rooms and information
//TODO Sort recycler
// view by most recent added


class AddRoomsFragment : Fragment(), RoomAdapter.OnItemClickListener {

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

    companion object{
        var allRooms : ArrayList<String> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.add_rooms_fragment,
            container, false
        )

        val rooms = resources.getStringArray(R.array.rooms).toMutableList()
        val roomsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, rooms)
        binding.addRoomsDropdown.setAdapter(roomsAdapter)

        viewModel = ViewModelProvider(this).get(AddRoomsViewModel::class.java)
        val args : AddRoomsFragmentArgs by navArgs()
        viewModel.spaceID = args.spaceID

        binding.recyclerViewRooms.layoutManager = GridLayoutManager(activity, 3)

        binding.roomImage.setOnClickListener {
            val imgIntent = Intent(Intent.ACTION_GET_CONTENT)
            imgIntent.setType("image/*")
            resultLauncher.launch(imgIntent)
        }
        binding.spaceIdHolder.text = viewModel.spaceID

        binding.AddRoomButton.setOnClickListener {
            viewModel.roomName = binding.addRoomsDropdown.text.toString()
            viewModel.roomDescription = binding.roomDescription.text.toString()

            if (viewModel.roomName == "") {
                Toast.makeText(context, "Enter a room name.", Toast.LENGTH_SHORT).show()
            }
            //else if (viewModel.roomDescription == "") {
             //   Toast.makeText(context, "Enter a room description.", Toast.LENGTH_SHORT).show()
           // }
            else if (viewModel.imageUri == null && viewModel.imageUrl == "") {
                Toast.makeText(context, "Pick an image for the room", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.checkExistingRooms()
                allRooms.add(viewModel.roomName)
            }
        }

        binding.UpdateRoomButton.setOnClickListener{
            viewModel.roomName = binding.addRoomsDropdown.text.toString()
            viewModel.roomDescription = binding.roomDescription.text.toString()

            if (viewModel.roomName == "") {
                Toast.makeText(context, "Enter a room name.", Toast.LENGTH_SHORT).show()
            }
            //else if (viewModel.roomDescription == "") {
            //    Toast.makeText(context, "Enter a room description.", Toast.LENGTH_SHORT).show() }
            else if (viewModel.imageUri == null && viewModel.imageUrl == "") {
                Toast.makeText(context, "Pick an image for the room", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateExistingRoom(object : onGetDataListener {
                    override fun onSuccess(response: Response) {
                        clearInfo()
                        binding.UpdateLayout.visibility = View.INVISIBLE
                        binding.AddClearLayout.visibility = View.VISIBLE
                    }
                    override fun onFailure(response: Response) {
                    }
                })
            }

        }

        binding.ClearRoomButton.setOnClickListener {
            clearInfo()
        }

        viewModel.message.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        })

        return binding.root
    }

    private fun clearInfo() {
        viewModel.imageUrl = ""
        viewModel.roomName = ""
        viewModel.roomDescription = ""
        viewModel.imageUri = null
        viewModel.roomKey = ""

        binding.addRoomsDropdown.setText(viewModel.roomName)
        binding.roomDescription.setText(viewModel.roomDescription)
//        TODO Not sure why its not just letting me set it.
        binding.roomImage.setImageDrawable(getResources().getDrawable(R.drawable.pick_image))
    }

    override fun onStart() {
        super.onStart()
        viewModel.getRoomDetails(object : onGetDataListener {
            override fun onSuccess(response: Response) {
                viewModel.itemList = response.objectList as List<Room?>?
                viewModel.itemList?.let { updateBinding(it) }
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
            false,
            this
        )
        binding.recyclerViewRooms.adapter = roomAdapter
        binding.progressCircular.visibility = View.INVISIBLE

    }

    override fun onItemClick(position: Int) {
        val clickedItem = viewModel.itemList?.get(position)

//        TODO not sure why adding !! fixed this error here...
        viewModel.imageUrl = clickedItem!!.imageUrl
        viewModel.roomName = clickedItem.room
        viewModel.roomDescription = clickedItem.description
        viewModel.roomKey = clickedItem.key

        binding.addRoomsDropdown.setText(viewModel.roomName)
        binding.roomDescription.setText(viewModel.roomDescription)
        Picasso.with(context)
            .load(viewModel.imageUrl)
            .into(binding.roomImage)

        binding.UpdateLayout.visibility = View.VISIBLE
        binding.AddClearLayout.visibility = View.INVISIBLE

        binding.recyclerViewRooms.adapter
    }

    override fun onStop() {
        super.onStop()
        viewModel.removeListener()
    }
}