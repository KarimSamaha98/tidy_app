package com.epfl.esl.tidy

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.datalayer.FirebaseRepository
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class OverviewViewModel : ViewModel() {
    private val repository : FirebaseRepository = FirebaseRepository()
    val tempID: Int = 0

    init {
    }
    fun getResponse() : LiveData<Response> {
        return repository.getRoomDetailsCo(tempID)
    }
}