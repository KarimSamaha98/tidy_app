package com.epfl.esl.tidy.overview

import androidx.lifecycle.ViewModel
import com.epfl.esl.tidy.Response
import com.epfl.esl.tidy.admin.Room
import com.epfl.esl.tidy.datalayer.FirebaseRepository
import com.epfl.esl.tidy.onGetDataListener
import com.epfl.esl.tidy.utils.Constants


class OverviewViewModel : ViewModel() {
    private val repository: FirebaseRepository = FirebaseRepository()
    var spaceID: String = ""

    init {
    }

    fun getRoomDetails(onGetDataListener: onGetDataListener) {
        repository.getSpaceIdSnapshot(spaceID, onGetDataListener) { r, d ->
            repository.getRooms(r, d)
        }
    }
}