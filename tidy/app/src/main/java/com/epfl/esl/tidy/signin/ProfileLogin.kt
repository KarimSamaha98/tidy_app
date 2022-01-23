package com.epfl.esl.tidy.signin

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epfl.esl.tidy.databinding.FragmentProfileLoginBinding
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.epfl.esl.tidy.MainActivity
import com.epfl.esl.tidy.R
import com.google.firebase.database.*


class ProfileLogin : Fragment() {
    private val TAG = "ProfileLogin"
    private lateinit var binding: FragmentProfileLoginBinding

    var email: String = ""
    var password: String = ""
    var key: String = ""
    var first_name : String = ""
    var last_name : String = ""
    var space_id : String = ""
    var admin : String = ""
    lateinit var image : Uri
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val profileRef: DatabaseReference = database.getReference("Profiles")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_profile_login, container, false)

        binding.toPassword.setOnClickListener{
            email = binding.email.text.toString()

            //Move to password entry
            binding.email.visibility = View.GONE
            binding.toPassword.visibility = View.GONE
            binding.toTask.visibility = View.VISIBLE
            binding.password.visibility = View.VISIBLE
        }

        binding.toTask.setOnClickListener{ view: View ->
            password = binding.password.text.toString()

            //Switch fragments
            //Check if username and password are compatible
             profileRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var correctUsername = false
                    var correctPassword = false
                    for (user in dataSnapshot.children) {
                        val usernameDatabase = user.child("Email")
                            .getValue(String::class.java)!!
                        if ( email == usernameDatabase) {
                            val passwordDatabase = user.child("Password")
                                .getValue(String::class.java)!!

                            if (password == passwordDatabase) {
                                correctUsername = true
                                correctPassword = true
                                key = user.key.toString()
                                first_name = user.child("First_Name").getValue(String::class.java)!!
                                last_name = user.child("Last_Name").getValue(String::class.java)!!
                                space_id = user.child("Space_Id").getValue(String::class.java)!!
                                admin = user.child("Admin").getValue(String::class.java)!!
                                image = Uri.parse(user.child("photo URL").getValue(String::class.java)!!)
                                break
                            }
                            else {
                                correctUsername = true
                                correctPassword = false
                                Toast.makeText(context,"Wrong password.",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    if (!correctUsername) {
                        Toast.makeText(context,"You should register first.",
                            Toast.LENGTH_LONG).show()
                        //TODO If the username wrong should take them back to fill in username info, otherwise they have no way of correcting a username mistake.
                    }

                    if (correctUsername && correctPassword) {
                        //Store Credential
                        MainActivity.loginInfo = UserDataClass(email=email, password=password, first_name=first_name, last_name=last_name, space_id=space_id, key=key, admin=admin, image=image)
                        //Change Fragments
                        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
                        Navigation.findNavController(view)
                .navigate(R.id.action_profileLogin_to_TasksFragment)}
                }

                 override fun onCancelled(error: DatabaseError) {
                     Log.d(TAG, "Database Error: $error")
                     Toast.makeText(context,"Database Error: $error",
                         Toast.LENGTH_LONG).show()
                 }
             })
        }

        binding.signUp.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_profileLogin_to_signUp)
        }
        return binding.root
    }
}