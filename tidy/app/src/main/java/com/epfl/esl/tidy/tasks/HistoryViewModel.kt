package com.epfl.esl.tidy.tasks

import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class HistoryViewModel : ViewModel() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val spaceRef: DatabaseReference = database.getReference(Constants.SPACEIDS)
    val tempID : String = "12325345345erst22"

    var displayTasksList = ArrayList<PastTaskClass>()
    var displayTask = PastTaskClass()
}