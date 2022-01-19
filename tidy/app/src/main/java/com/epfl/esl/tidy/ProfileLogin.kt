package com.epfl.esl.tidy

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.epfl.esl.tidy.databinding.FragmentProfileLoginBinding
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream


class ProfileLogin : Fragment() {

    private lateinit var binding: FragmentProfileLoginBinding

    private lateinit var dataClient: DataClient


    var email: String = ""
    var password: String = ""
    var key: String = ""
    var first_name : String = ""
    var last_name : String = ""
    var space_id : String = ""

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val profileRef: DatabaseReference = database.getReference("Profiles")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_login, container, false)
        dataClient = Wearable.getDataClient(activity as AppCompatActivity)

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
                    }

                    if (correctUsername && correctPassword) {
                        //Store Credential
                        (activity as MainActivity).loginInfo = UserDataClass(email=email, password=password, first_name=first_name, last_name=last_name, space_id=space_id, key=key )
                        //Change Fragments
                        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
                        Navigation.findNavController(view)
                .navigate(R.id.action_profileLogin_to_taskView)}
                }

                 override fun onCancelled(error: DatabaseError) {
                     TODO("Not yet implemented")
                 }
             })
        }

        binding.signUp.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_profileLogin_to_signUp)
        }
        return binding.root
    }

    private fun sendDataToWear() {

        val matrix = Matrix()
        matrix.postRotate(90F)

        var imageBitmap = MediaStore.Images.Media.getBitmap(
            getActivity()?.applicationContext?.contentResolver,
            imageUri
        )
        var ratio: Float = 13F

        val imageBitmapScaled = Bitmap.createScaledBitmap(
            imageBitmap,
            (imageBitmap.width / ratio).toInt(),
            (imageBitmap.height / ratio).toInt(),
            false
        )
        imageBitmap = Bitmap.createBitmap(
            imageBitmapScaled,
            0,
            0,
            (imageBitmap.width / ratio).toInt(),
            (imageBitmap.height / ratio).toInt(),
            matrix,
            true
        )

        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageByteArray = stream.toByteArray()

        val request: PutDataRequest = PutDataMapRequest.create("/userInfo").run {
            dataMap.putByteArray("profileImage", imageByteArray)
            dataMap.putString("username", username)
            asPutDataRequest()
        }

        request.setUrgent()
        val putTask: Task<DataItem> = dataClient.putDataItem(request)
    }
}

//R.layout.fragment_profile_login