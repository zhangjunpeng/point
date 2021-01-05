package com.zjdx.point.ui.travel

import androidx.lifecycle.*
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.data.repository.TravelRepository

class TravelViewModel(val repository: TravelRepository, val id: String) : ViewModel() {


    val allLication = MutableLiveData<MutableList<Location>>().apply {
        this.value = repository.getLocationListById(id)
    }


    fun getLocationsById(tid:String){
        allLication.value= repository.getLocationListById(tid)
    }


    fun insertLocation(location: Location) {
        repository.insertLocation(location)
    }


    fun insertTravelRecord(travelRecord: TravelRecord) {
        repository.insertTravelRecord(travelRecord)
    }


}

class TravelViewModelFactory(private val repository: TravelRepository, val id: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TravelViewModel(repository, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}