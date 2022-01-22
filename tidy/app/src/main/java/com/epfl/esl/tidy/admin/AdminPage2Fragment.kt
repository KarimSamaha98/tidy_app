package com.epfl.esl.tidy.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.databinding.AdminPageFragmentBinding
import com.epfl.esl.tidy.databinding.FragmentAdminPage2Binding


/**
 * A simple [Fragment] subclass.
 * Use the [AdminPage2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminPage2Fragment : Fragment() {

//    private lateinit var viewModel: AdminPageViewModel
    private val viewModel : AdminPageViewModel by activityViewModels()
    private lateinit var binding: FragmentAdminPage2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_admin_page2,
            container, false
        )
//        viewModel = ViewModelProvider(this).get(AdminPageViewModel::class.java)
        binding.spaceIdHolder.text = viewModel.spaceID


        binding.addRoomsButton.setOnClickListener{
            val direction = AdminPage2FragmentDirections.actionAdminPage2FragmentToAddRoomsFragment(viewModel.spaceID)
            findNavController().navigate(direction)
        }

        binding.addSuppliesButton.setOnClickListener{
            val direction = AdminPage2FragmentDirections.actionAdminPage2FragmentToAddSuppliesFragment(viewModel.spaceID)
            findNavController().navigate(direction)
        }

        binding.addTasksButton.setOnClickListener{
            val direction = AdminPage2FragmentDirections.actionAdminPage2FragmentToAddTasksFragment(viewModel.spaceID)
            findNavController().navigate(direction)
        }

        return binding.root
    }
}