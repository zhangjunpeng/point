package com.zjdx.point.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zjdx.point.db.model.Location
import com.zjdx.point.data.repository.TravelRepository
import kotlinx.coroutines.launch

class HistoryLocationViewModel(private val repository: TravelRepository) : ViewModel() {

    val locationListLiveData = MutableLiveData<ArrayList<Location>>().apply {
        this.value = ArrayList()
    }

     fun getLocationByTid(tid: String) {
         viewModelScope.launch {
             locationListLiveData.value= repository.getLocationListById(tid) as ArrayList<Location>
         }
    }
}


