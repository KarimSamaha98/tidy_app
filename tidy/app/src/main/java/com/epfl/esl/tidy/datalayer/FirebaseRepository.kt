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


    fun checkSpaceId(snapshot: DataSnapshot, spaceID : Int) : DataSnapshot? {
        for (space in snapshot.children) {
            return if (space.child(Constants.SPACEID).getValue(Int::class.java)!! == spaceID)
                space else null
        }
        return null
    }

    fun getSpaceIdSnapshot(
        spaceID: Int,
        onGetDataListener: onGetDataListener,
        pullData: (response: Response, space: DataSnapshot) -> (Unit)
    ): ValueEventListener {
        val response = Response()

        var roomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                checkSpaceId(snapshot, spaceID)?.let {
                    pullData(response, it)
                    Log.d(TAG, "Correct Space Key: ${it.key}")}
                onGetDataListener.onSuccess(response)
            }

            override fun onCancelled(error: DatabaseError) {
                response.exception = Throwable("DatabaseError: $error")
                onGetDataListener.onFailure(response)
                Log.d(TAG, "Database Error: $error")
            }
        }
//        spaceRef.addListenerForSingleValueEvent(roomListener)
        return spaceRef.addValueEventListener(roomListener)
    }

    fun getRooms(response: Response, space: DataSnapshot): Unit {
        if(space.child(Constants.ROOMS).exists()) {
            response.objectList =
                space.child(Constants.ROOMS).children.map { snapShot ->
                    val room = snapShot.getValue(Room::class.java)
                    room?.key = snapShot.key.toString()
                    room
                }
        } else{
            response.objectList = listOf()
        }
    }

    fun getSupplies(response: Response, space: DataSnapshot): Unit {
        response.objectList =
            space.child(Constants.SUPPLIES).children.map { snapShot ->
                snapShot.getValue(Supply::class.java)
            }
    }

    fun processImage(imageBitmap: Bitmap): ByteArray {
//        val matrix = Matrix()
////        var imageBitmap =
////            MediaStore.Images.Media.getBitmap(context?.contentResolver, viewModel.imageUri)
//        val ratio: Float = 13F
//
//        val imageBitmapScaled = Bitmap.createScaledBitmap(
//            imageBitmap,
//            (imageBitmap.width / ratio).toInt(),
//            (imageBitmap.height / ratio).toInt(),
//            false
//        )
//        val newImageBitmap = Bitmap.createBitmap(
//            imageBitmapScaled,
//            0,
//            0,
//            (imageBitmap.width / ratio).toInt(),
//            (imageBitmap.height / ratio).toInt(),
//            matrix,
//            true
//        )
        val newImageBitmap = imageBitmap

        val stream = ByteArrayOutputStream()
        newImageBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream)
        val imageByteArray = stream.toByteArray()

        return imageByteArray
    }

    fun sendDataToFireBase(imageBitmap: Bitmap,
                           key: String,
                           putData: (uri: Uri, key : String) -> (Unit)) {
        val imageByteArray = processImage(imageBitmap)

        val profileImageRef = storageRef.child("RoomImages/$key.jpg")

        profileImageRef.putBytes(imageByteArray).addOnFailureListener {
//            Toast.makeText(context, "Room image upload to firebase was failed.", Toast.LENGTH_SHORT)
//                .show()

        }.addOnSuccessListener { taskSnapshot ->
//            Get a URL that points to our data and can be used by Picasso to easily load image.
//            Not sure if this is the best way to do this...
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri: Uri ->
//                TODO check if you spam button multiple times what happens.
//                Note: This will only upload data if photo is also loaded. could also change this behavior.
                putData(uri, key)

//                Handler(Looper.getMainLooper()).postDelayed({ binding.progressBar.progress = 0 }, 500)

            }?.addOnFailureListener {
//                Toast.makeText(context, "No Image URL saved.", Toast.LENGTH_SHORT)
//                    .show()
            }

        }
//            .addOnProgressListener { taskSnapshot ->
//            TODO is this working as expected?
//            val progress: Double =
//                (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
//            binding.progressBar.progress = progress.toInt()
//        }

    }
//    fun putRoom(uri: Uri, room: Room, tempID_key: String, childName: String, key : String){
//        room.imageUrl = uri.toString()
//        spaceRef.child(tempID_key)
//            .child(childName)
//            .child(key)
//            .setValue(room)
//    }


    fun removeListener(listener: ValueEventListener) {
        spaceRef.removeEventListener(listener)
    }
}