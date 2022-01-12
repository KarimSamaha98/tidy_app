package com.epfl.esl.tidy

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.epfl.esl.tidy.databinding.AddRoomsFragmentBinding
import com.epfl.esl.tidy.databinding.AddSuppliesFragmentBinding

open class AddSuppliesFragment : Fragment() {

    companion object {
        fun newInstance() = AddSuppliesFragment()
    }

    private val supplyList = mapOf("Vaccum Cleaner" to 0,
                                    "Broom" to 1,
                                    "Mop" to 2,
                                    "All Purpose Cleaner" to 3,
                                    "Window Cleaner" to 4,
                                    "Bathroom Cleaner" to 5,
                                    "Bleach" to 6,
                                    "Rags" to 7,
                                    "Bucket" to 8,)

    private lateinit var viewModel: AddSuppliesViewModel
    private lateinit var binding: AddSuppliesFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.add_supplies_fragment,
            container, false
        )

        val spinnerSupplies : Spinner = binding.spinnerSupplies
        spinnerSupplies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
                val selectedSupply : String = p0.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.supplies,
            android.R.layout.simple_spinner_item).also{adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSupplies.adapter = adapter
        }


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddSuppliesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
