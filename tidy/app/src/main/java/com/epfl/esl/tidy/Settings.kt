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
import com.epfl.esl.tidy.databinding.FragmentSettingsBinding
import com.google.firebase.database.*


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        updateView()
        return binding.root
    }

    fun sendDataToFireBase(){
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

        binding.firstName.text = first_name
        binding.lastName.text = last_name
        binding.email.text = email
        binding.password.text = password
        binding.spaceId.text = space_id
    }

}

