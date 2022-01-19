package com.epfl.esl.tidy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epfl.esl.tidy.databinding.FragmentSignUpBinding
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.database.*


class SignUp : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val profileRef: DatabaseReference = database.getReference("Profiles")
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

            sendDataToFireBase()
            (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
            (activity as MainActivity).loginInfo = UserDataClass(email=email, password=password, first_name=first_name, last_name=last_name, space_id=space_id, key=key )
            Navigation.findNavController(view).navigate(R.id.action_signUp_to_taskView)
        }

        return binding.root
    }

    fun sendDataToFireBase(){
        //Update profiles
        profileRef.child( key).child("Email")
            .setValue(email)
        profileRef.child( key).child("Password")
            .setValue(password)
        profileRef.child( key).child("First_Name")
            .setValue(first_name)
        profileRef.child( key).child("Last_Name")
            .setValue(last_name)
        profileRef.child( key).child("Space_Id")
            .setValue(space_id)

        //Update space_ids
        space_key = spaceRef.child("Users").push().key.toString()
        spaceRef.child(space_id).child("Users").child(space_key).child("Key").setValue(key)
        spaceRef.child(space_id).child("Users").child(space_key).child("First_Name").setValue(first_name)
        spaceRef.child(space_id).child("Users").child(space_key).child("Last_Name").setValue(last_name)
    }

}

