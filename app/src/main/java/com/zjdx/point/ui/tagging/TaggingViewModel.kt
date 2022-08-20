package com.zjdx.point.ui.tagging

import androidx.lifecycle.*
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.data.repository.DataBaseRepository
import kotlinx.coroutines.launch

class TaggingViewModel(val repository: DataBaseRepository) : ViewModel() {

    val allLication = MutableLiveData<MutableList<Location>>()

    fun getLocationsByTime(startTime: String,endTime: String) {
        viewModelScope.launch {
//            allLication.value = repository.getLocationListById(tid)
        }
    }


}

