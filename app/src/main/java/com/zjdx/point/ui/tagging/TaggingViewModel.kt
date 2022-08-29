package com.zjdx.point.ui.tagging

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TagRecord
import com.zjdx.point.utils.DateUtil
import kotlinx.coroutines.launch
import java.util.*

class TaggingViewModel(val repository: DataBaseRepository) : ViewModel() {

    val allLication = MutableLiveData<MutableList<Location>>()

    val notUpTagRecord = MutableLiveData<MutableList<TagRecord>>()


    val startTime = MutableLiveData<Date>()
    val endTime = MutableLiveData<Date>()

    val selectLoaction = MutableLiveData<Location>()

    val addTag = MutableLiveData<Boolean>().apply {
        this.value = false
    }

    val tarvelModelList = ArrayList<String>().apply {
        add("步行")
    }

    fun getLocationsByTime() {
        viewModelScope.launch {
            allLication.value = repository.getLocationListByTime(
                startTime = DateUtil.dateFormat.format(startTime.value!!),
                endTime = DateUtil.dateFormat.format(endTime.value!!)
            )
        }
    }

    fun insertTagRecord(tagRecord: TagRecord) {
        viewModelScope.launch {
            repository.insertTag(tagRecord)
            notUpTagRecord.value = repository.getNotUpload()
        }
    }


    fun getTagRecordIsNotUpload() {
        viewModelScope.launch {
            notUpTagRecord.value = repository.getNotUpload()
        }
    }

}

