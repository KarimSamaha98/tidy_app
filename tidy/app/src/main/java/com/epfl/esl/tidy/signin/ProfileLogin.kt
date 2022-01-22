package com.epfl.esl.tidy.signin

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.epfl.esl.tidy.MainActivity
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.databinding.FragmentProfileLoginBinding
import com.epfl.esl.tidy.tasks.PastTaskClass
import com.epfl.esl.tidy.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.ArrayList


class ProfileLogin : Fragment() {
    private val TAG = "ProfileLogin"
    private lateinit var binding: FragmentProfileLoginBinding
    private lateinit var dataClient: DataClient
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
    val spaceRef: DatabaseReference = database.getReference("Space_IDs")
    val user_taks_id: String = "098327sdf0912" //Remove

    var task_keys = mutableListOf<String>()
    var task_dates = mutableListOf<String>()
    var task_names = mutableListOf<String>()
    var task_places = mutableListOf<String>()

    lateinit var task_dates_list : Array<String>
    lateinit var task_names_list : Array<String>
    lateinit var task_places_list : Array<String>
    lateinit var task_keys_list : Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile_login, container, false
        )
        dataClient = Wearable.getDataClient(activity as AppCompatActivity)


        binding.toPassword.setOnClickListener {
            email = binding.email.text.toString()

            //Move to password entry
            binding.email.visibility = View.INVISIBLE
            binding.toPassword.visibility = View.INVISIBLE
            binding.toTask.visibility = View.VISIBLE
            binding.password.visibility = View.VISIBLE
        }

        binding.toTask.setOnClickListener { view: View ->
            password = binding.password.text.toString()

            //Switch fragments
            //Check if username and password are compatible
            profileRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var correctUsername = false
                    var correctPassword = false
                    for (user in dataSnapshot.children) {
                        val usernameDatabase = user.child("Email").getValue(String::class.java)!!
                        if (email == usernameDatabase) {
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
                            } else {
                                correctUsername = true
                                correctPassword = false
                                Toast.makeText(
                                    context, "Wrong password.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    if (!correctUsername) {
                        Toast.makeText(
                            context, "You should register first.",
                            Toast.LENGTH_LONG
                        ).show()
                        //TODO If the username wrong should take them back to fill in username info, otherwise they have no way of correcting a username mistake.
                    }

                    if (correctUsername && correctPassword) {
                        //Store Credential
                        (activity as MainActivity).loginInfo = UserDataClass(email=email, password=password, first_name=first_name, last_name=last_name, space_id=space_id, key=key, admin=admin, image=image)
                        //Change Fragments
                        Handler().postDelayed({
                            (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
                            Navigation.findNavController(view)
                                .navigate(R.id.action_profileLogin_to_TasksFragment)
                        }, 5000)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "DatabaseError: $error")
                    Toast.makeText(
                        context, "Database Error: $error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        binding.signUp.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_profileLogin_to_signUp)
        }
        return binding.root
    }

    //Wear API fun
    private fun sendDataToWear() {

        var imageUri: Uri? = Uri.parse("android.resource://com.epfl.esl.tidy/" + R.drawable.user)
        var imageBitmap = MediaStore.Images.Media.getBitmap(getActivity()?.
        applicationContext?.contentResolver, imageUri)

        var ratio: Float = 1F
        val imageBitmapScaled = Bitmap.createScaledBitmap(
            imageBitmap,
            (imageBitmap.width / ratio).toInt(), (imageBitmap.height / ratio).toInt(), false
        )

        val matrix = Matrix()
        matrix.postRotate(0F)
        imageBitmap = Bitmap.createBitmap(
            imageBitmapScaled, 0, 0,
            (imageBitmap.width / ratio).toInt(),
            (imageBitmap.height / ratio).toInt(), matrix, true
        )

        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageByteArray = stream.toByteArray()

        //Cast to list
        task_dates_list = task_dates.toTypedArray()
        task_places_list = task_places.toTypedArray()
        task_names_list = task_names.toTypedArray()
        task_keys_list = task_keys.toTypedArray()


        val request: PutDataRequest = PutDataMapRequest.create("/userInfo").run {
            dataMap.putByteArray("profileImage", imageByteArray!!)
            dataMap.putString("username", first_name)
            dataMap.putStringArray("taskname", task_names_list)
            dataMap.putStringArray("taskplace", task_places_list)
            dataMap.putStringArray("taskdate", task_dates_list)
            dataMap.putStringArray("taskkey", task_keys_list)
            asPutDataRequest()
        }
        request.setUrgent()
        dataClient.putDataItem(request)
    }

    fun getUserTasks(){
        spaceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val space = dataSnapshot.child(space_id)
                //get task keys
                for (task in space.child("Current tasks").children) {
                    if (task.child("user_key").getValue(String::class.java)!! == user_taks_id) {
                        task_keys += task.child("task_key").getValue(String::class.java)!!
                        task_dates += task.child("due").getValue(String::class.java)!!
                    }}

                val tasks = dataSnapshot.child(space_id).child("Tasks")
                for (task in tasks.children) {
                    for (task_key in task_keys) {
                        if (task.key.toString() == task_key) {
                            task_names += task.child("Name").getValue(String::class.java)!!
                            task_places += task.child("Room").getValue(String::class.java)!!
                        }
                    }}
                }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

//R.layout.fragment_profile_login