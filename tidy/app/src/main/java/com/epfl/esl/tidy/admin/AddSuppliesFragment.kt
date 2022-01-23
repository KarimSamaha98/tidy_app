package com.epfl.esl.tidy.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.Response
import com.epfl.esl.tidy.databinding.AddSuppliesFragmentBinding
import com.epfl.esl.tidy.onGetDataListener
import com.squareup.picasso.Picasso

class AddSuppliesFragment : Fragment(), SupplyAdapter.OnItemClickListener {

//    private val supplyList = mapOf("Vaccum Cleaner" to 0,
//                                    "Broom" to 1,
//                                    "Mop" to 2,
//                                    "All Purpose Cleaner" to 3,
//                                    "Window Cleaner" to 4,
//                                    "Bathroom Cleaner" to 5,
//                                    "Bleach" to 6,
//                                    "Rags" to 7,
//                                    "Bucket" to 8,)

    private lateinit var viewModel: AddSuppliesViewModel
    private lateinit var binding: AddSuppliesFragmentBinding

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                viewModel.imageUri = imageUri
                binding.supplyImage.setImageURI(imageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddSuppliesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.add_supplies_fragment,
            container, false
        )

        val supplies = resources.getStringArray(R.array.supplies)
        val suppliesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, supplies)
        binding.addSuppliesDropdown.setAdapter(suppliesAdapter)

        val args : AddSuppliesFragmentArgs by navArgs()
        viewModel.spaceID = args.spaceID

        binding.recyclerViewRooms.layoutManager = GridLayoutManager(activity, 3)

        binding.supplyImage.setOnClickListener {
            val imgIntent = Intent(Intent.ACTION_GET_CONTENT)
            imgIntent.setType("image/*")
            resultLauncher.launch(imgIntent)
        }
        binding.spaceIdHolder.text = viewModel.spaceID

        binding.AddSupplyButton.setOnClickListener {
            viewModel.supplyName = binding.addSuppliesDropdown.text.toString()
            viewModel.supplyDescription = binding.supplyDescription.text.toString()

            if (viewModel.supplyName == "") {
                Toast.makeText(context, "Enter a supply name.", Toast.LENGTH_SHORT).show()
            } else if (viewModel.supplyDescription == "") {
                Toast.makeText(context, "Enter a supply description.", Toast.LENGTH_SHORT).show()
            } else if (viewModel.imageUri == null && viewModel.imageUrl == "") {
                Toast.makeText(context, "Pick an image for the supply", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.checkExistingSupplies()
            }

        }

        binding.UpdateSupplyButton.setOnClickListener{
            viewModel.supplyName = binding.addSuppliesDropdown.text.toString()
            viewModel.supplyDescription = binding.supplyDescription.text.toString()

            if (viewModel.supplyName == "") {
                Toast.makeText(context, "Enter a supply name.", Toast.LENGTH_SHORT).show()
            } else if (viewModel.supplyDescription == "") {
                Toast.makeText(context, "Enter a supply description.", Toast.LENGTH_SHORT).show()
            } else if (viewModel.imageUri == null && viewModel.imageUrl == "") {
                Toast.makeText(context, "Pick an image for the supply", Toast.LENGTH_SHORT).show()
            } else {
                //TODO
                viewModel.updateExistingRoom(object : onGetDataListener {
                    override fun onSuccess(response: Response) {
                        clearInfo()
                        binding.UpdateLayout.visibility = View.INVISIBLE
                        binding.AddClearLayout.visibility = View.VISIBLE
                    }
                    override fun onFailure(response: Response) {
                    }
                })
            }

        }

        binding.ClearSupplyButton.setOnClickListener {
            clearInfo()
        }

        viewModel.message.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        })

        return binding.root
    }

    private fun clearInfo() {
        viewModel.imageUrl = ""
        viewModel.supplyName = ""
        viewModel.supplyDescription = ""
        viewModel.imageUri = null
        viewModel.supplyKey = ""

        binding.addSuppliesDropdown.setText(viewModel.supplyName)
        binding.supplyDescription.setText(viewModel.supplyDescription)
//        TODO Not sure why its not just letting me set it.
        binding.supplyImage.setImageDrawable(getResources().getDrawable(R.drawable.pick_image))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getsupplyDetails(object : onGetDataListener {
            override fun onSuccess(response: Response) {
                viewModel.itemList = response.objectList as List<Supply?>?
                viewModel.itemList?.let { updateBinding(it) }
            }
            override fun onFailure(response: Response) {
                response.exception?.let{ Toast.makeText(
                    context,
                    it.toString(),
                    Toast.LENGTH_SHORT
                ).show()}
            }
        })
    }

    fun updateBinding(items: List<Supply?>) {
        val supplyAdapter = SupplyAdapter(
            context = context,
//                 TODO: have to be careful this will give nullpointer exception if response.objectList doesnt get a value
            items = items,
            this
        )
        binding.recyclerViewRooms.adapter = supplyAdapter
        binding.progressCircular.visibility = View.INVISIBLE

    }

    override fun onItemClick(position: Int) {
        val clickedItem = viewModel.itemList?.get(position)

//        TODO not sure why adding !! fixed this error here...
        viewModel.imageUrl = clickedItem!!.imageUrl
        viewModel.supplyName = clickedItem.name
        viewModel.supplyDescription = clickedItem.description
        viewModel.supplyKey = clickedItem.key

        binding.addSuppliesDropdown.setText(viewModel.supplyName)
        binding.supplyDescription.setText(viewModel.supplyDescription)
        Picasso.with(context)
            .load(viewModel.imageUrl)
            .into(binding.supplyImage)

        binding.UpdateLayout.visibility = View.VISIBLE
        binding.AddClearLayout.visibility = View.INVISIBLE

        binding.recyclerViewRooms.adapter
    }

    override fun onStop() {
        super.onStop()
        viewModel.removeListener()
    }

}
