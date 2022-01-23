package com.epfl.esl.tidy.admin

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.epfl.esl.tidy.Event
import com.epfl.esl.tidy.Response
import com.epfl.esl.tidy.datalayer.FirebaseRepository
import com.epfl.esl.tidy.onGetDataListener
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.*

class AddSuppliesViewModel(application: Application) : AndroidViewModel(application) {
    val repository = FirebaseRepository()
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)

    var supplyKey : String
    var imageUri: Uri?
    var supplyName: String
    var supplyDescription : String
    var imageUrl : String
    var spaceID : String = "12325345345erst22"
    var itemList : List<Supply?>? = null

    lateinit var listener : ValueEventListener
    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
        get() = statusMessage

    init{
        imageUri = null
        supplyName = ""
        supplyDescription = ""
        imageUrl = ""
        supplyKey = ""
    }

    fun getsupplyDetails(onGetDataListener: onGetDataListener) {
        listener = repository.getSpaceIdSnapshot(spaceID, onGetDataListener) { r, d ->
            repository.getSupplies(r, d)
        }
    }

    fun removeListener(){
        repository.removeListener(listener)
    }

    fun checkExistingSupplies(){
        spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                var isRegistered = false
//                loop@ for (rooms in dataSnapshot.child(spaceID).child(Constants.ROOMS).children) {
//                    if (roomMapping[roomName] == rooms.child(Constants.ROOMID)
//                            .getValue(Int::class.java) ?: break@loop
//                    ) {
//                        statusMessage.value = Event("This room is already registered to Firebase. Use another room")
//                        isRegistered = true
//                        break@loop
//                    }
//                }
//                if (!isRegistered) {
                statusMessage.value = Event("Your supply is registered to Firebase")
                val imageBitmap =
                    MediaStore.Images.Media.getBitmap(
                        getApplication<Application>().contentResolver,
                        imageUri
                    )
                sendImagetoFirebase(imageBitmap)
//                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun updateExistingRoom(onGetDataListener: onGetDataListener) {
        if(imageUri != null) {
            val imageBitmap =
                MediaStore.Images.Media.getBitmap(
                    getApplication<Application>().contentResolver,
                    imageUri
                )
            repository.sendDataToFireBase(imageBitmap, supplyKey) { uri, key ->
                spaceRef.child(spaceID)
                    .child(Constants.SUPPLIES)
                    .child(supplyKey)
                    .setValue(Supply(supplyName, 1,supplyDescription, uri.toString()))
                    .addOnSuccessListener {
                        statusMessage.value = Event("Supply Update Successful")
                        onGetDataListener.onSuccess(Response())
                    }.addOnFailureListener{
                        statusMessage.value = Event("Supply Update Failed")
                    }
            }
        }else {
            val updates : MutableMap<String, Any> = HashMap()
            updates["name"] = supplyName
            updates["description"] = supplyDescription
            spaceRef.child(spaceID)
                .child(Constants.ROOMS)
                .child(supplyKey).updateChildren(updates).addOnSuccessListener {
                    statusMessage.value = Event("Room Update Successful")
                    onGetDataListener.onSuccess(Response())
                }.addOnFailureListener{
                    statusMessage.value = Event("Room Update Failed")
                }
        }

    }

    private fun sendImagetoFirebase(imageBitmap: Bitmap) {
        val supply = Supply(supplyName, 1 ,supplyDescription,)
        val key = spaceRef.push().key.toString()
        repository.sendDataToFireBase(imageBitmap, key){uri, k ->
//            repository.putRoom(uri, dC as Room, temp, name, key)
            supply.imageUrl = uri.toString()
            spaceRef.child(spaceID)
                .child(Constants.SUPPLIES)
                .child(k)
                .setValue(supply)
        }
    }
}