package com.epfl.esl.tidy.overview

import android.provider.ContactsContract
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
        repository.getSpaceIdSnapshot(spaceID, onGetDataListener) { response, space ->
            var roomList : List<DataModel?> = listOf(DataModel.Header(title = "ROOMS"))
            var supplyList : List<DataModel?> = listOf(DataModel.Header(title = "SUPPLIES"))
            if(space.child(Constants.ROOMS).exists()) {
                val list =
                    space.child(Constants.ROOMS).children.map { snapShot ->
                        val room = snapShot.getValue(DataModel.Room::class.java)
                        room?.key = snapShot.key.toString()
                        room
                    }
                roomList = roomList + list
            }
            if(space.child(Constants.SUPPLIES).exists()) {
                val list =
                    space.child(Constants.SUPPLIES).children.map { snapShot ->
                        val supply = snapShot.getValue(DataModel.Supply::class.java)
                        supply?.key = snapShot.key.toString()
                        supply
                    }
                supplyList = supplyList + list
            }
            response.objectList = roomList + supplyList
        }
    }
}