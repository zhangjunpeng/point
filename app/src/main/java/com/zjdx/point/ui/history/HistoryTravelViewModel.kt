package com.zjdx.point.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.repository.TravelRepository

class HistoryTravelViewModel(private val repository: TravelRepository) : ViewModel() {

    fun getAllTravelRecord(): List<TravelRecord> {
        return repository.getAll()
    }

}

class HistoryTravelViewModelFactory(private val repository: TravelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryTravelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryTravelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}