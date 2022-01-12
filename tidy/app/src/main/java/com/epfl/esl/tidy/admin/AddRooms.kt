package com.epfl.esl.tidy.admin

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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.databinding.AddRoomsFragmentBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream

class AddRooms : Fragment() {

    companion object {
        fun newInstance() = AddRooms()
    }

    private lateinit var viewModel: AddRoomsViewModel
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
                viewModel.roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
                            sendDataToFireBase()
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
        viewModel = ViewModelProvider(this).get(AddRoomsViewModel::class.java)
    }

//    TODO move this to view model.
    fun sendDataToFireBase() {
        val matrix = Matrix()
        matrix.postRotate(90F)

        var imageBitmap =
            MediaStore.Images.Media.getBitmap(context?.contentResolver, viewModel.imageUri)
        val ratio: Float = 13F

        val imageBitmapScaled = Bitmap.createScaledBitmap(
            imageBitmap,
            (imageBitmap.width / ratio).toInt(),
            (imageBitmap.height / ratio).toInt(),
            false
        )
        imageBitmap = Bitmap.createBitmap(
            imageBitmapScaled,
            0,
            0,
            (imageBitmap.width / ratio).toInt(),
            (imageBitmap.height / ratio).toInt(),
            matrix,
            true
        )

        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageByteArray = stream.toByteArray()

//        TODO put in timestamp so this is unique.
        val profileImageRef =
            viewModel.storageRef.child("RoomImages/" + viewModel.tempID.toString() + "_" + viewModel.roomMapping[viewModel.roomName].toString() + ".jpg")

        profileImageRef.putBytes(imageByteArray).addOnFailureListener {
            Toast.makeText(context, "Room image upload to firebase was failed.", Toast.LENGTH_SHORT)
                .show()

        }.addOnSuccessListener { taskSnapshot ->
//            Get a URL that points to our data and can be used by Picasso to easily load image.
//            Not sure if this is the best way to do this...
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri: Uri ->
//                TODO check if you spam button multiple times what happens.
//                Note: This will only upload data if photo is also loaded. could also change this behavior.
                viewModel.imageUrl = uri.toString()
                val room = Room(viewModel.roomName, viewModel.roomDescription, viewModel.imageUrl)
                val key = viewModel.roomRef.push().key.toString()
                viewModel.roomRef.child(viewModel.tempID_key)
                    .child("Rooms")
                    .child(key)
                    .setValue(room)

                Handler(Looper.getMainLooper()).postDelayed({ binding.progressBar.progress = 0 }, 500)

            }?.addOnFailureListener {
                Toast.makeText(context, "No Image URL saved.", Toast.LENGTH_SHORT)
                    .show()
            }

        }.addOnProgressListener { taskSnapshot ->
//            TODO is this working as expected?
            val progress: Double =
                (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            binding.progressBar.progress = progress.toInt()
        }

    }
}