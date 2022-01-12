package com.epfl.esl.tidy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.datalayer.FirebaseRepository


class OverviewViewModel : ViewModel() {
    private val repository: FirebaseRepository = FirebaseRepository()
    val tempID: Int = 0
    val mutableLiveData = MutableLiveData<Response>()

    init {
    }

    fun getRoomDetails(onGetDataListener: onGetDataListener) {
        repository.getRoomDetails(tempID, onGetDataListener)
    }

//    private fun setValue(input: Response) {
//        mutableLiveData.postValue(input)
//    }
}