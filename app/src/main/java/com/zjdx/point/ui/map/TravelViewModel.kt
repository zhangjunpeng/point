package com.zjdx.point.ui.map

import androidx.lifecycle.*
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.repository.TravelRepository
import kotlinx.coroutines.flow.Flow

class TravelViewModel(val repository: TravelRepository, id: String) : ViewModel() {


    val allLication = repository.getLocationById(id).asLiveData()


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