package com.epfl.esl.tidy.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.datalayer.FirebaseRepository
import com.epfl.esl.tidy.onGetDataListener


class OverviewViewModel : ViewModel() {
    private val repository: FirebaseRepository = FirebaseRepository()
    val tempID: Int = 0

    init {
    }

    fun getRoomDetails(onGetDataListener: onGetDataListener) {
        repository.getRoomDetails(tempID, onGetDataListener)
    }

//    private fun setValue(input: Response) {
//        mutableLiveData.postValue(input)
//    }
}