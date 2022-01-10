package com.epfl.esl.tidy

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.datalayer.FirebaseRepository
import com.epfl.esl.tidy.utils.Constants
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class OverviewViewModel : ViewModel() {
    private val repository: FirebaseRepository = FirebaseRepository()
    val tempID: Int = 0
    val mutableLiveData = MutableLiveData<Response>()

    init {
    }

    fun getRoomDetails() {
        CoroutineScope(IO).launch {
            val response = repository.getRoomDetailsCo(tempID)
            mutableLiveData.postValue(response)
        }
    }

//    private fun setValue(input: Response) {
//        mutableLiveData.postValue(input)
//    }
}