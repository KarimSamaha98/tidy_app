package com.epfl.esl.tidy

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.epfl.esl.tidy.databinding.ActivityMainBinding
import com.epfl.esl.tidy.services.AlarmReceiver
import com.epfl.esl.tidy.signin.UserDataClass
import com.epfl.esl.tidy.tasks.PastTaskClass
import com.epfl.esl.tidy.utils.Constants
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener {

    public var loginInfo = UserDataClass(email="", password="", first_name="", last_name="", key="", space_id="", admin="")

    private lateinit var binding: ActivityMainBinding
    public lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = this.findNavController(R.id.mainFragment)
        bottomNavigationView = binding.bottomMenuView
        bottomNavigationView.setupWithNavController(navController)
        setBottomNavigationVisibility(View.INVISIBLE)

        setAlarm(loginInfo.space_id)
    }

    public fun setBottomNavigationVisibility (visibility: Int){
        bottomNavigationView.visibility = visibility
    }

    private fun setAlarm(spaceID : String){
        calendar = Calendar.getInstance()
        // Set execution time to be 01:00:00 AM
        calendar.set(Calendar.HOUR_OF_DAY, 15)
        calendar.set(Calendar.MINUTE,45)
        calendar.set(Calendar.SECOND,0)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("spaceID", spaceID)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.setInexactRepeating( // use bc exact time isn't important
            AlarmManager.RTC, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

        Toast.makeText(this, "New tasks will be assigned!", Toast.LENGTH_SHORT).show()
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents
            .filter {it.dataItem.uri.path == "/task_to_history" }
            .forEach { event ->
                val taskID: String = DataMapItem.fromDataItem(event.dataItem).dataMap.getString("taskID")
                val taskName = DataMapItem.fromDataItem(event.dataItem).dataMap.getString("taskname")
                val taskDate = DataMapItem.fromDataItem(event.dataItem).dataMap.getString("taskdate")
                val taskUser = DataMapItem.fromDataItem(event.dataItem).dataMap.getString("taskuser")
                val taskPlace = DataMapItem.fromDataItem(event.dataItem).dataMap.getString("taskplace")

                completeTask(taskID, taskName, taskDate, taskUser, taskPlace)
            }
    }

    fun completeTask(taskID : String, taskName : String, taskDate : String, taskUser : String, taskPlace: String){
        // add completed task to history in Firebase
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("dd-MM-yyyy")

        val cal: Calendar = Calendar.getInstance()
        val todayDate = sdf.format(cal.time)

        val completedTask = PastTaskClass(taskName.plus(" in ").plus(taskPlace.lowercase()),
            taskUser,
            todayDate,
            taskDate)

        val key = spaceRef.push().key.toString()
        spaceRef.child(loginInfo.space_id).child(Constants.PREVTASK).child(key).setValue(completedTask)

        // remove from current task list in Firebase
        spaceRef.child(loginInfo.space_id).child(Constants.CURRTASK).child(taskID).removeValue()
    }

    override fun onResume(){
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }
}