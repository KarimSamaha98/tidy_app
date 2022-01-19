package com.epfl.esl.tidy.datalayer

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.epfl.esl.tidy.Response
import com.epfl.esl.tidy.admin.Room
import com.epfl.esl.tidy.admin.Supply
import com.epfl.esl.tidy.onGetDataListener
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class FirebaseRepository {
    private val TAG = "FirebaseRepository"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val profileRef: DatabaseReference = database.getReference(Constants.PROFILES)
    private val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)
    private var storageRef: StorageReference = Firebase.storage.reference


    fun registerUser() {
    }

    fun getSpaceIdSnapshot(
        spaceID: Int,
        onGetDataListener: onGetDataListener,
        function: (response: Response, space: DataSnapshot) -> (Unit)
    ) {
        val response = Response()

        var roomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (space in snapshot.children) {
                    if (space.child(Constants.SPACEID).getValue(Int::class.java)!! == spaceID) {
//                        response.objectList =
//                            space.child(Constants.ROOMS).children.map { snapShot ->
//                                snapShot.getValue(Room::class.java)
//                            }
                        function(response, space)
                    }
                }
                onGetDataListener.onSuccess(response)
            }

            override fun onCancelled(error: DatabaseError) {
                response.exception = Throwable(error.toString())
                onGetDataListener.onFailure(response)
                Log.d(TAG, "Database Error: ${error.toString()}")
            }
        }
        spaceRef.addListenerForSingleValueEvent(roomListener)
    }

    fun getRooms(response: Response, space: DataSnapshot): Unit {
        response.objectList =
            space.child(Constants.ROOMS).children.map { snapShot ->
                snapShot.getValue(Room::class.java)
            }
    }

    fun getSupplies(response: Response, space: DataSnapshot): Unit {
        response.objectList =space.child(Constants.SUPPLIES).children.map { snapShot ->
            snapShot.getValue(Supply::class.java)

            }
    }

    fun processImage(imageBitmap: Bitmap): ByteArray {
        val matrix = Matrix()
//        var imageBitmap =
//            MediaStore.Images.Media.getBitmap(context?.contentResolver, viewModel.imageUri)
        val ratio: Float = 13F

        val imageBitmapScaled = Bitmap.createScaledBitmap(
            imageBitmap,
            (imageBitmap.width / ratio).toInt(),
            (imageBitmap.height / ratio).toInt(),
            false
        )
        val newImageBitmap = Bitmap.createBitmap(
            imageBitmapScaled,
            0,
            0,
            (imageBitmap.width / ratio).toInt(),
            (imageBitmap.height / ratio).toInt(),
            matrix,
            true
        )

        val stream = ByteArrayOutputStream()
        newImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageByteArray = stream.toByteArray()

        return imageByteArray
    }

    //TODO Problem here with dataclass. I waant to make this function multi puprose but it is failing a bit because you have to specify a dataclass type. If I just use any then it breaks because I need to use information in the class to add it to the dataclass
    //TODO also how to deal with propogating the toast errors through to the UI
    fun sendDataToFireBase(imageBitmap: Bitmap,
                           saveString: String,
                           childName: String,
                           tempID_key: String,
                           dataClass: Room,
                           //TODO here specify dataaClass as Room, not general at all.
                           function: (uri: Uri, dataClass: Room, tempID_key: String, childName: String) -> (Unit)) {
        val imageByteArray = processImage(imageBitmap)

        val profileImageRef = storageRef.child("RoomImages/$saveString.jpg")
//            storageRef.child("RoomImages/" + viewModel.tempID.toString() + "_" + viewModel.roomMapping[viewModel.roomName].toString() + ".jpg")

        profileImageRef.putBytes(imageByteArray).addOnFailureListener {
//            Toast.makeText(context, "Room image upload to firebase was failed.", Toast.LENGTH_SHORT)
//                .show()

        }.addOnSuccessListener { taskSnapshot ->
//            Get a URL that points to our data and can be used by Picasso to easily load image.
//            Not sure if this is the best way to do this...
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri: Uri ->
//                TODO check if you spam button multiple times what happens.
//                Note: This will only upload data if photo is also loaded. could also change this behavior.
                function(uri, dataClass, tempID_key, childName)

//                Handler(Looper.getMainLooper()).postDelayed({ binding.progressBar.progress = 0 }, 500)

            }?.addOnFailureListener {
//                Toast.makeText(context, "No Image URL saved.", Toast.LENGTH_SHORT)
//                    .show()
            }

        }.addOnProgressListener { taskSnapshot ->
//            TODO is this working as expected?
//            val progress: Double =
//                (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
//            binding.progressBar.progress = progress.toInt()
        }

    }
    fun putRoom(uri: Uri, dataClass: Room, tempID_key: String, childName: String){
        //TODO this is where it brakes. If I want to add dataCLass : Supply it won't work with the declaration above.
        dataClass.imageUrl = uri.toString()
        val key = spaceRef.push().key.toString()
        spaceRef.child(tempID_key)
            .child(childName)
            .child(key)
            .setValue(dataClass)
    }
}