package com.zjdx.point.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.ui.base.BaseListViewModel
import com.zjdx.point.ui.edit.EditUserInfoViewModel
import com.zjdx.point.ui.tagging.HisTagViewModel
import com.zjdx.point.ui.history.HistoryLocationViewModel
import com.zjdx.point.ui.history.HistoryTravelViewModel
import com.zjdx.point.ui.main.MainViewModel

class ViewModelFactory(private val repository: DataBaseRepository) :
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
        if (modelClass.isAssignableFrom(BaseListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BaseListViewModel() as T
        }
        if (modelClass.isAssignableFrom(HisTagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HisTagViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(EditUserInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditUserInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}