package com.epfl.esl.tidy

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    public lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    companion object{
        var loginInfo = UserDataClass(email="", password="", first_name="", last_name="", key="", space_id="", admin="")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = this.findNavController(R.id.mainFragment)
        bottomNavigationView = binding.bottomMenuView
        bottomNavigationView.setupWithNavController(navController)
        setBottomNavigationVisibility(View.INVISIBLE)

        setAlarm()
    }

    public fun setBottomNavigationVisibility (visibility: Int){
        bottomNavigationView.visibility = visibility
    }


    private fun setAlarm(){
        calendar = Calendar.getInstance()
        // Set execution time to be 01:00:00 AM
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 15)
        calendar.set(Calendar.SECOND,0)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.setInexactRepeating( // use bc exact time isn't important
            AlarmManager.RTC, calendar.timeInMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent
        )
        Toast.makeText(this, "New tasks will be assigned!", Toast.LENGTH_SHORT).show()
    }
}