package com.zjdx.point.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.data.repository.TravelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryTravelViewModel(private val repository: TravelRepository) : ViewModel() {

    val allRecordLiveData = MutableLiveData<List<TravelRecord>>().apply {
        this.value = ArrayList<TravelRecord>()
    }

    fun getAllTravelRecord(uid: String, startTime: Long, endTime: Long) {
        viewModelScope.launch {
            allRecordLiveData.value=repository.getAll(uid, startTime, endTime)
        }

    }
}
