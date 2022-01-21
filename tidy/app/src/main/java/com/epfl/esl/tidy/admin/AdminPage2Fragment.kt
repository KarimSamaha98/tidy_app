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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminPage2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminPage2Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

//    private lateinit var viewModel: AdminPageViewModel
    private val viewModel : AdminPageViewModel by activityViewModels()
    private lateinit var binding: FragmentAdminPage2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminPage2Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminPage2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}