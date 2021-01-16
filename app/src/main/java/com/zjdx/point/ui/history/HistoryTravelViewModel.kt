package com.zjdx.point.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.data.repository.TravelRepository

class HistoryTravelViewModel(private val repository: TravelRepository) : ViewModel() {

    val allRecordLiveData = MutableLiveData<List<TravelRecord>>().apply {
        this.value = ArrayList<TravelRecord>()
    }

    fun getAllTravelRecord(uid:String,startTime:Long,endTime:Long): List<TravelRecord> {
        return repository.getAll(uid, startTime, endTime)
    }
}
