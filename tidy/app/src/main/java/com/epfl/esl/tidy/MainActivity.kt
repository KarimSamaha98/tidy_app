package com.epfl.esl.tidy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
class MainActivity : AppCompatActivity() {

    public var loginInfo = UserDataClass(email="", password="", first_name="", last_name="", key="", space_id="")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}