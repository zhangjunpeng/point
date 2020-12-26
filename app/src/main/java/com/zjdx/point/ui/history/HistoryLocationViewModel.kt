package com.zjdx.point.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zjdx.point.db.model.Location
import com.zjdx.point.repository.TravelRepository

class HistoryLocationViewModel(private val repository: TravelRepository) : ViewModel() {

    val locationListLiveData = MutableLiveData<ArrayList<Location>>().apply {
        this.value = ArrayList()
    }

    fun getLocationByTid(tid: String) {
        locationListLiveData.value= repository.getLocationListById(tid) as ArrayList<Location>
    }
}


