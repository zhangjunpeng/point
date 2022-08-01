package com.zjdx.point.ui.travel

import androidx.lifecycle.*
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.data.repository.DataBaseRepository
import kotlinx.coroutines.launch

class TravelViewModel(val repository: DataBaseRepository) : ViewModel() {


    val allLication = MutableLiveData<MutableList<Location>>()


    fun getLocationsById(tid: String) {
        viewModelScope.launch {
            allLication.value = repository.getLocationListById(tid)
        }
    }

    fun getTravelRecordById(tid: String): TravelRecord {
        return repository.getTravelRecordById(tid)
    }


    fun insertLocation(location: Location) {
        repository.insertLocation(location)
    }


    fun insertTravelRecord(travelRecord: TravelRecord) {
        repository.insertTravelRecord(travelRecord)
    }

    fun deleteTravelRecord(travelRecord: TravelRecord) {
        repository.deteleTravel(travelRecord)
    }


}

class TravelViewModelFactory(private val repository: DataBaseRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TravelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}