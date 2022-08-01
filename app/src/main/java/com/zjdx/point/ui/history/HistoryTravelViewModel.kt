package com.zjdx.point.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.data.repository.DataBaseRepository
import kotlinx.coroutines.launch

class HistoryTravelViewModel(private val repository: DataBaseRepository) : ViewModel() {

    val allRecordLiveData = MutableLiveData<List<TravelRecord>>().apply {
        this.value = ArrayList<TravelRecord>()
    }

    fun getAllTravelRecord(uid: String, startTime: Long, endTime: Long) {
        viewModelScope.launch {
            allRecordLiveData.value=repository.getAll(uid, startTime, endTime)
        }

    }
}
