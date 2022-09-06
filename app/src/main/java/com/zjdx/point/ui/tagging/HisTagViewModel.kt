package com.zjdx.point.ui.tagging

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.db.model.TagRecord
import kotlinx.coroutines.launch

class HisTagViewModel(private val repository: DataBaseRepository) : ViewModel() {

    val allTagLiveData = MutableLiveData<Map<String, List<TagRecord>>>().apply {
        this.value = HashMap<String, List<TagRecord>>()
    }


    fun getTagRecordIsUpload() {
        viewModelScope.launch {
            val templist = repository.getUpload()
            val map = HashMap<String, ArrayList<TagRecord>>()
            templist.forEach {
                val date = it.uploadDate
                if (map.containsKey(date)) {
                    val list = map[date]
                    list?.add(it)
                } else {
                    val list = ArrayList<TagRecord>()
                    list.add(it)
                    map[date] = list
                }
            }
            allTagLiveData.postValue(map)
        }
    }


    var mStartTime: String = "请选择开始时间"
    var mEndTime: String = "请选择结束时间"


    fun getTagRecordIsUploadByTime(startTime: String, endTime: String) {
        mStartTime = startTime
        mEndTime = endTime

        viewModelScope.launch {
            val templist = repository.getUploadByTime(startTime, endTime)
            val map = HashMap<String, ArrayList<TagRecord>>()
            templist.forEach {
                val date = it.uploadDate
                if (map.containsKey(date)) {
                    val list = map[date]
                    list?.add(it)
                } else {
                    val list = ArrayList<TagRecord>()
                    list.add(it)
                    map[date] = list
                }
            }
            allTagLiveData.postValue(map)
        }
    }


}
