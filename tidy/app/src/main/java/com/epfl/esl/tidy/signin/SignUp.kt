package com.epfl.esl.tidy.signin

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epfl.esl.tidy.databinding.FragmentSignUpBinding
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.epfl.esl.tidy.MainActivity
import com.epfl.esl.tidy.R
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class SignUp : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val profileRef: DatabaseReference = database.getReference("Profiles")
    var storageRef = Firebase.storage.reference
    val spaceRef: DatabaseReference = database.getReference("Space_IDs")
    var key: String = ""
    var space_key: String = ""
    var email: String = ""
    var password: String = ""
    var first_name : String = ""
    var last_name : String = ""
    var space_id : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        binding.register.setOnClickListener{ view: View ->
            key = profileRef.push().key.toString() //Generate key
            email = binding.email.text.toString()
            password = binding.password.text.toString()
            first_name = binding.firstName.text.toString()
            last_name = binding.lastName.text.toString()
            space_id = binding.spaceId.text.toString()

            spaceRef.child(space_id).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        sendDataToFireBase()
                        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
                        MainActivity.loginInfo = UserDataClass(email=email, password=password,
                            first_name=first_name, last_name=last_name,
                            space_id=space_id, key=key, admin="0")
                        Navigation.findNavController(view).navigate(R.id.action_signUp_to_profileLogin)
                    } else{
                        Toast.makeText(context, "SpaceID doesn't exist", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        return binding.root
    }

    fun sendDataToFireBase(){
        profileRef.child(key).child("Email")
            .setValue(email)
        profileRef.child( key).child("Password")
            .setValue(password)
        profileRef.child( key).child("First_Name")
            .setValue(first_name)
        profileRef.child( key).child("Last_Name")
            .setValue(last_name)
        profileRef.child( key).child("Space_Id")
            .setValue(space_id)
        profileRef.child( key).child("Admin")
            .setValue("0")
        processImage(MainActivity.loginInfo.image)

        //Update space_ids
        space_key = spaceRef.child("Users").push().key.toString()
        spaceRef.child(space_id).child("Users").child(space_key).child("Key").setValue(key)
        spaceRef.child(space_id).child("Users").child(space_key).child("First_Name").setValue(first_name)
        spaceRef.child(space_id).child("Users").child(space_key).child("Last_Name").setValue(last_name)
    }

    fun processImage(image_URI: Uri?){123
        val matrix = Matrix()
        var imageBitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver,
            image_URI)
        val ratio:Float = 13F
        val imageBitmapScaled = Bitmap.createScaledBitmap(imageBitmap,
            (imageBitmap.width / ratio).toInt(), (imageBitmap.height / ratio).toInt(), false)
        imageBitmap = Bitmap.createBitmap(imageBitmapScaled, 0, 0,
            (imageBitmap.width / ratio).toInt(), (imageBitmap.height / ratio).toInt(),
            matrix, true)

        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageByteArray = stream.toByteArray()

        val profileImageRef = storageRef.child("ProfileImages/"+ email +".jpg")
        val uploadProfileImage = profileImageRef.putBytes(imageByteArray)

        uploadProfileImage.addOnFailureListener {
            Toast.makeText(context,"Profile image upload to firebase was failed.", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot -> profileRef.child(key).child("photo URL").setValue((FirebaseStorage.getInstance().getReference()).toString()+"ProfileImages/"+ email +".jpg")
        }
    }
}

