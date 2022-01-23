package com.epfl.esl.tidy

import android.app.Activity
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import com.epfl.esl.tidy.databinding.ActivityMainBinding
import com.google.android.gms.wearable.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : Activity(), DataClient.OnDataChangedListener {

    private lateinit var binding: ActivityMainBinding
    var firstName : String = ""

    //Default
    var receivedTaskname: ArrayList<String> = arrayListOf()
    var receivedTaskplace: ArrayList<String> = arrayListOf()
    var receivedTaskdate: ArrayList<String> = arrayListOf()
    var receivedTaskkey: ArrayList<String> = arrayListOf()
    var receivedUsernameBitmap: Bitmap? = null

    //Completed
    lateinit var completedTaskkey: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.recyclerLauncherView.visibility = View.GONE
        binding.myText.visibility = View.VISIBLE
        binding.logoView.visibility = View.VISIBLE
        binding.recyclerLauncherView.layoutManager = WearableLinearLayoutManager(this, CustomScrollingLayoutCallback())


    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents
            .filter {it.dataItem.uri.path == "/userInfo" }
            .forEach { event ->
                val receivedImage: ByteArray = DataMapItem.fromDataItem(event.dataItem).dataMap.getByteArray("profileImage")
                firstName  = DataMapItem.fromDataItem(event.dataItem).dataMap.getString("username")

                receivedTaskname += DataMapItem.fromDataItem(event.dataItem).dataMap.getStringArray("taskname")
                receivedTaskdate += DataMapItem.fromDataItem(event.dataItem).dataMap.getStringArray("taskdate")
                receivedTaskplace += DataMapItem.fromDataItem(event.dataItem).dataMap.getStringArray("taskplace")
                receivedTaskkey += DataMapItem.fromDataItem(event.dataItem).dataMap.getStringArray("taskkey")
                val receiveRandom = DataMapItem.fromDataItem(event.dataItem).dataMap.getString("random")

                receivedUsernameBitmap = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage.size)

                binding.logoView.setImageBitmap(receivedUsernameBitmap)
                binding.myText.setText("Welcome back $firstName!")

                val itemAdapter = this.let { ItemAdapter(context = it,
                    task_name = receivedTaskname,
                    task_date = receivedTaskdate,
                    task_place = receivedTaskplace)}

                binding.recyclerLauncherView.adapter = itemAdapter

                val swipeGesture = object : SwipeGesture(){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        val taskID = receivedTaskkey[position]
                        val taskName = receivedTaskname[position]
                        val taskDate = receivedTaskdate[position]
                        val taskPlace = receivedTaskplace[position]
                        val taskUser = firstName

                        sendDatatoMobile(taskID, taskName, taskDate, taskUser, taskPlace)

                        receivedTaskdate.removeAt(position)
                        receivedTaskname.removeAt(position)
                        receivedTaskplace.removeAt(position)
                        receivedTaskkey.removeAt(position)

                        binding.recyclerLauncherView.adapter?.notifyItemRemoved(position)
                    }
                }

                val itemTouchHelper = ItemTouchHelper(swipeGesture)
                itemTouchHelper.attachToRecyclerView(binding.recyclerLauncherView)
                Handler().postDelayed({movetoTasks()}, 3000)
            }
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "On Resume Listener Attached")
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "On Pause Listener Removed")
        Wearable.getDataClient(this).removeListener(this)
    }

    fun movetoTasks() {
        binding.myText.visibility = View.GONE
        binding.logoView.visibility = View.GONE
        binding.recyclerLauncherView.visibility = View.VISIBLE
    }

    private fun sendDatatoMobile(taskID : String, taskName : String, taskDate : String, taskUser : String, taskPlace: String){
        val dataClient : DataClient = Wearable.getDataClient(this)
        val putDataReq : PutDataRequest = PutDataMapRequest.create("/task_to_history").run {
            dataMap.putString("taskID", taskID)
            dataMap.putString("taskname", taskName)
            dataMap.putString("taskdate", taskDate)
            dataMap.putString("taskuser", taskUser)
            dataMap.putString("taskplace", taskPlace)
            asPutDataRequest()
        }
        dataClient.putDataItem(putDataReq)
    }
}