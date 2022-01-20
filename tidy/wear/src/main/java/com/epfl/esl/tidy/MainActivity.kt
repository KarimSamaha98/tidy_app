package com.epfl.esl.tidy

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.epfl.esl.tidy.databinding.ActivityMainBinding
import com.google.android.gms.wearable.*

class MainActivity : Activity(), DataClient.OnDataChangedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }


    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)

    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents
            .filter { it.type == DataEvent.TYPE_CHANGED &&
                    it.dataItem.uri.path == "/userInfo" }
            .forEach { event ->
                val receivedImage: ByteArray = DataMapItem.fromDataItem(event.dataItem).dataMap.getByteArray("profileImage")
                val receivedFirstName: String = DataMapItem.fromDataItem(event.dataItem).dataMap.getString("firstName")
                val receivedUsernameBitmap = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage.size)
                binding.logoView.setImageBitmap(receivedUsernameBitmap)
                binding.myText.setText("Welcome back $receivedFirstName!")}
    }
}