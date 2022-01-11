package com.epfl.esl.tidy

import android.app.Activity
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import com.epfl.esl.tidy.databinding.FragmentSignUpBinding
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.epfl.esl.tidy.databinding.FragmentSettingsBinding
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class Settings : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val profileRef: DatabaseReference = database.getReference("Profiles")
    lateinit var key: String
    lateinit var email: String
    lateinit var first_name: String
    lateinit var last_name: String
    lateinit var space_id: String
    lateinit var password: String
    lateinit var image: Uri
    var storageRef = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        updateView()

        binding.signOut.setOnClickListener(){
                view: View ->
            clearLogin()
            (activity as MainActivity).bottomNavigationView.visibility = View.GONE
            Navigation.findNavController(view).navigate(R.id.action_settings_to_profileLogin)
        }

        binding.editButton.setOnClickListener(){
            switchToEdit()
        }

        binding.cancelButton.setOnClickListener(){
            switchToDisplay()
        }

        binding.saveButton.setOnClickListener(){
            updateLogin()
            sendDataToFireBase()
            updateView()
            switchToDisplay()
        }

        binding.profilePicButton.setOnClickListener(){
            val imgIntent = Intent(Intent.ACTION_GET_CONTENT)
            imgIntent.setType("image/*")
            resultLauncher.launch(imgIntent)
        }



        return binding.root
    }

    fun sendDataToFireBase(){
        key = (activity as MainActivity).loginInfo.key
        space_id = (activity as MainActivity).loginInfo.space_id
        first_name = (activity as MainActivity).loginInfo.first_name
        last_name = (activity as MainActivity).loginInfo.last_name
        email = (activity as MainActivity).loginInfo.email
        password = (activity as MainActivity).loginInfo.password

        profileRef.child(key).child("Email")
            .setValue(email)
        profileRef.child(key).child("Password")
            .setValue(password)
        profileRef.child( key).child("First_Name")
            .setValue(first_name)
        profileRef.child(key).child("Last_Name")
            .setValue(last_name)
        profileRef.child(key).child("Space_Id")
            .setValue(space_id)
    }

    fun updateView(){
        key = (activity as MainActivity).loginInfo.key
        space_id = (activity as MainActivity).loginInfo.space_id
        first_name = (activity as MainActivity).loginInfo.first_name
        last_name = (activity as MainActivity).loginInfo.last_name
        email = (activity as MainActivity).loginInfo.email
        password = (activity as MainActivity).loginInfo.password
        image = (activity as MainActivity).loginInfo.image ?: Uri.parse("android.resource://com.epfl.esl.tidy/" + R.drawable.user)

        binding.firstName.text = first_name
        binding.lastName.text = last_name
        binding.email.text = email
        binding.password.text = password
        binding.spaceId.text = space_id
        binding.profilePic.setImageURI(image)
        switchToDisplay()
    }

    fun clearLogin(){
        (activity as MainActivity).loginInfo.key = ""
        (activity as MainActivity).loginInfo.email = ""
        (activity as MainActivity).loginInfo.password = ""
        (activity as MainActivity).loginInfo.first_name = ""
        (activity as MainActivity).loginInfo.last_name = ""
        (activity as MainActivity).loginInfo.space_id = ""
        (activity as MainActivity).loginInfo.admin = ""
        (activity as MainActivity).loginInfo.image = Uri.parse("android.resource://com.epfl.esl.tidy/" + R.drawable.user)
    }

    fun updateLogin(){
        if (binding.emailInput.text.toString() != "") {
        (activity as MainActivity).loginInfo.email = binding.emailInput.text.toString()}
        if (binding.passwordInput.text.toString() != "") {
        (activity as MainActivity).loginInfo.password =  binding.passwordInput.text.toString()}
        if (binding.firstNameInput.text.toString() != "") {
        (activity as MainActivity).loginInfo.first_name =  binding.firstNameInput.text.toString()}
        if (binding.lastNameInput.text.toString() != "") {
        (activity as MainActivity).loginInfo.last_name =  binding.lastNameInput.text.toString()}
        if (binding.spaceIdInput.text.toString() != "") {
        (activity as MainActivity).loginInfo.space_id =  binding.spaceIdInput.text.toString()}
    }

    fun switchToEdit(){
        binding.email.visibility = View.INVISIBLE
        binding.password.visibility = View.INVISIBLE
        binding.firstName.visibility = View.INVISIBLE
        binding.lastName.visibility = View.INVISIBLE
        binding.spaceId.visibility = View.INVISIBLE

        binding.emailInput.visibility = View.VISIBLE
        binding.passwordInput.visibility = View.VISIBLE
        binding.firstNameInput.visibility = View.VISIBLE
        binding.lastNameInput.visibility = View.VISIBLE
        binding.spaceIdInput.visibility = View.VISIBLE

        binding.emailInput.hint = email
        binding.passwordInput.hint = password
        binding.firstNameInput.hint = first_name
        binding.lastNameInput.hint = last_name
        binding.spaceIdInput.hint = space_id

        binding.cancelButton.visibility = View.VISIBLE
        binding.saveButton.visibility = View.VISIBLE
        binding.editButton.visibility = View.INVISIBLE
        binding.profilePicButton.visibility = View.VISIBLE
    }

    fun switchToDisplay(){
        binding.email.visibility = View.VISIBLE
        binding.password.visibility = View.VISIBLE
        binding.firstName.visibility = View.VISIBLE
        binding.lastName.visibility = View.VISIBLE
        binding.spaceId.visibility = View.VISIBLE

        binding.emailInput.visibility = View.INVISIBLE
        binding.passwordInput.visibility = View.INVISIBLE
        binding.firstNameInput.visibility = View.INVISIBLE
        binding.lastNameInput.visibility = View.INVISIBLE
        binding.spaceIdInput.visibility = View.INVISIBLE


        binding.cancelButton.visibility = View.INVISIBLE
        binding.saveButton.visibility = View.INVISIBLE
        binding.editButton.visibility = View.VISIBLE
        binding.profilePicButton.visibility = View.INVISIBLE

        if ((activity as MainActivity).loginInfo.admin == "1"){
            binding.admin.visibility = View.VISIBLE
        }
        else {
            binding.admin.visibility = View.INVISIBLE
        }
    }


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                (activity as MainActivity).loginInfo.image = imageUri
                processImage(imageUri)
                binding.profilePic.setImageURI(imageUri)
            }
        }

    fun processImage(image_URI: Uri?){
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

