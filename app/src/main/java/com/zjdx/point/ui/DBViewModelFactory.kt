package com.zjdx.point.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.ui.tagging.HisDetailViewModel
import com.zjdx.point.ui.tagging.HisTagViewModel
import com.zjdx.point.ui.tagging.TaggingViewModel
import com.zjdx.point.ui.travel.TravelViewModel

class DBViewModelFactory(private val repository: DataBaseRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TravelViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(TaggingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaggingViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(HisTagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HisTagViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(HisDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HisDetailViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}