package com.zjdx.point.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zjdx.point.data.repository.TravelRepository
import com.zjdx.point.ui.history.HistoryLocationViewModel
import com.zjdx.point.ui.history.HistoryTravelViewModel
import com.zjdx.point.ui.main.MainViewModel

class ViewModelFactory(private val repository: TravelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryTravelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryTravelViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(HistoryLocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryLocationViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}