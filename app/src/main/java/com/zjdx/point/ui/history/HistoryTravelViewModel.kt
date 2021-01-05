package com.zjdx.point.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.data.repository.TravelRepository

class HistoryTravelViewModel(private val repository: TravelRepository) : ViewModel() {

    val allRecordLiveData = MutableLiveData<List<TravelRecord>>().apply {
        this.value = ArrayList<TravelRecord>()
    }

    fun getAllTravelRecord(): List<TravelRecord> {
        return repository.getAll()
    }
}
