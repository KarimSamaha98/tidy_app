package com.epfl.esl.tidy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.epfl.esl.tidy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    public var loginInfo = UserDataClass(email="", password="", first_name="", last_name="", key="", space_id="")
    private lateinit var binding: ActivityMainBinding
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = this.findNavController(R.id.mainFragment)
        bottomNavigationView = binding.bottomMenuView
        bottomNavigationView.setupWithNavController(navController)
        setBottomNavigationVisibility(View.GONE)
    }

    public fun setBottomNavigationVisibility (visibility: Int){
        bottomNavigationView.visibility = visibility
    }
}