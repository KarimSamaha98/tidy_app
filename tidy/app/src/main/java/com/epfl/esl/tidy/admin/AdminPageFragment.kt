package com.epfl.esl.tidy.admin

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.databinding.AddRoomsFragmentBinding
import com.epfl.esl.tidy.databinding.AdminPageFragmentBinding

class AdminPageFragment : Fragment() {

    companion object {
        fun newInstance() = AdminPageFragment()
    }

    private lateinit var viewModel: AdminPageViewModel
    private lateinit var binding: AdminPageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.admin_page_fragment,
            container, false
        )
        viewModel = ViewModelProvider(this).get(AdminPageViewModel::class.java)

        binding.CreateNewSpaceButton.setOnClickListener{
//            viewModel.createSpace()
//            Move to next page
        }

        binding.EditExistingSpaceButton.setOnClickListener{
            viewModel.spaceID = binding.spaceIdText.text.toString()
//            if(viewModel.checkSpace()){
////                move to next page
//            } else{
//                Toast.makeText(context, "Invalid SpaceID, try again", Toast.LENGTH_SHORT).show()
//            }

        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminPageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}