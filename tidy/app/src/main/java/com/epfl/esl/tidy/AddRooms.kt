package com.epfl.esl.tidy

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.epfl.esl.tidy.databinding.AddRoomsFragmentBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.security.KeyStore

class AddRooms : Fragment() {

    companion object {
        fun newInstance() = AddRooms()
    }

    private lateinit var viewModel: AddRoomsViewModel
    private lateinit var binding : AddRoomsFragmentBinding

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

        binding = DataBindingUtil.inflate(inflater, R.layout.add_rooms_fragment,
            container, false)

        binding.roomImage.setOnClickListener {
            val imgIntent = Intent(Intent.ACTION_GET_CONTENT)
            imgIntent.setType("image/*")
            resultLauncher.launch(imgIntent)
        }

        binding.AddRoomButton.setOnClickListener {
            viewModel.roomName = binding.roomName.text.toString()
            viewModel.roomDescription = binding.roomDescription.text.toString()

            if (viewModel.roomName == "") {
                Toast.makeText(context,"Enter a room name.", Toast.LENGTH_SHORT).show()
            }
            else if (viewModel.roomDescription == "") {
                Toast.makeText(context,"Enter a room description.", Toast.LENGTH_SHORT).show()
            }
            else if (viewModel.imageUri == null) {
                Toast.makeText(context,"Pick an image for the room", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        var isRegistered = false

                        loop@ for (space in dataSnapshot.children) {
                            if (space.child("Space_ID")
                                    .getValue(Int::class.java)!! == viewModel.tempID
                            ) {
                                viewModel.tempID_key = space.getKey()
                                for (rooms in space.child("Rooms").children) {
                                    if (viewModel.roomMapping[viewModel.roomName] == rooms.child("Room_ID")
                                            .getValue(Int::class.java)!!
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
        // TODO: Use the ViewModel
    }

    fun sendDataToFireBase(){

        viewModel.tempID_key?.let { viewModel.roomRef.child(it).child("Room").child(viewModel.key).child("Attribute").setValue(viewModel.roomName) }
        viewModel.tempID_key?.let { viewModel.roomRef.child(it).child("Room").child(viewModel.key).child("Description").setValue(viewModel.roomDescription) }
        viewModel.tempID_key?.let { viewModel.roomRef.child(it).child("Room").child(viewModel.key).child("Room_ID").setValue(viewModel.roomMapping[viewModel.roomName]) }

//        viewModel.roomRef.child(viewModel.key).child("Attribute").setValue(viewModel.roomName)
//        viewModel.roomRef.child(viewModel.key).child("Description").setValue(viewModel.roomDescription)
//        viewModel.roomRef.child(viewModel.key).child("Room_ID").setValue(viewModel.roomMapping[viewModel.roomName])
//        viewModel.roomRef.child(viewModel.key).child("photo_URL").setValue(viewModel.tempID.toString() + "_" + viewModel.roomMapping[viewModel.roomName].toString())

        val matrix = Matrix()
        matrix.postRotate(90F)

        var imageBitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, viewModel.imageUri)
        val ratio:Float = 13F

        val imageBitmapScaled = Bitmap.createScaledBitmap(imageBitmap, (imageBitmap.width / ratio).toInt(), (imageBitmap.height / ratio).toInt(), false)
        imageBitmap = Bitmap.createBitmap(imageBitmapScaled, 0, 0, (imageBitmap.width / ratio).toInt(), (imageBitmap.height / ratio).toInt(), matrix, true)

        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageByteArray = stream.toByteArray()

        val profileImageRef = viewModel.storageRef.child("RoomImages/"+ viewModel.tempID.toString() + "_" + viewModel.roomMapping[viewModel.roomName].toString() +".jpg")
        val uploadProfileImage = profileImageRef.putBytes(imageByteArray)

        uploadProfileImage.addOnFailureListener {
            Toast.makeText(context,"Room image upload to firebase was failed.", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
//            viewModel.roomRef.child(viewModel.key).child("photo URL").setValue((FirebaseStorage.getInstance().getReference()).toString()+"ProfileImages/"+ viewModel.tempID.toString() + "_" + viewModel.roomMapping[viewModel.roomName].toString() +".jpg")
            viewModel.tempID_key?.let { viewModel.roomRef.child(it).child("Room").child(viewModel.key).child("photo_URL").setValue((FirebaseStorage.getInstance().getReference()).toString()+"RoomImages/"+viewModel.tempID.toString() + "_" + viewModel.roomMapping[viewModel.roomName].toString()) }
        }
    }

}