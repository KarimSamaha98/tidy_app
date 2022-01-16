package com.epfl.esl.tidy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.epfl.esl.tidy.databinding.ActivityMainBinding
import com.epfl.esl.tidy.signin.UserDataClass
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    public var loginInfo = UserDataClass(email="", password="", first_name="", last_name="", key="", space_id="", admin="")

    private lateinit var binding: ActivityMainBinding
    public lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = this.findNavController(R.id.mainFragment)
        bottomNavigationView = binding.bottomMenuView
        bottomNavigationView.setupWithNavController(navController)
        setBottomNavigationVisibility(View.GONE)

        myPeriodicWorker()
    }

    public fun setBottomNavigationVisibility (visibility: Int){
        bottomNavigationView.visibility = visibility
    }

    private fun myPeriodicWorker(){
        val dueDate = Calendar.getInstance()
        val currentDate = Calendar.getInstance()

        // Set execution time to be 03:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 13)
        dueDate.set(Calendar.MINUTE,30)
        dueDate.set(Calendar.SECOND,0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .build()

        val myRequest = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            24,
            TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag("Assign new tasks")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "my_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            myRequest
        )
    }
}