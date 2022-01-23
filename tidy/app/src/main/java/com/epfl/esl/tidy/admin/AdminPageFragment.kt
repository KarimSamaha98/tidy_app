package com.epfl.esl.tidy.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.epfl.esl.tidy.MainActivity
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.databinding.AdminPageFragmentBinding
import com.google.firebase.database.*

class AdminPageFragment : Fragment() {
    companion object {
        fun newInstance() = AdminPageFragment()
    }

//    private lateinit var viewModel: AdminPageViewModel
    private val viewModel : AdminPageViewModel by activityViewModels()
    private lateinit var binding: AdminPageFragmentBinding
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference("Space_IDs")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.admin_page_fragment,
            container, false
        )
//        viewModel = ViewModelProvider(this).get(AdminPageViewModel::class.java)

        binding.CreateNewSpaceButton.setOnClickListener{
            viewModel.spaceID = spaceRef.push().key.toString()
            Navigation.findNavController(it).navigate(R.id.action_admin_page_fragment_to_adminPage2Fragment)
        }

        binding.spaceIdText.setText(MainActivity.loginInfo.space_id)
        binding.EditExistingSpaceButton.setOnClickListener{
            if(binding.spaceIdText.text.toString() != "") {
                viewModel.spaceID = binding.spaceIdText.text.toString()

                spaceRef.child(viewModel.spaceID)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                Navigation.findNavController(it)
                                    .navigate(R.id.action_admin_page_fragment_to_adminPage2Fragment)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Invalid SpaceID, try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            }else{
                Toast.makeText(
                    context,
                    "SpaceID can't be blank.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(AdminPageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}