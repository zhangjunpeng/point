package com.zjdx.point.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.db.model.TravelRecord
import kotlinx.coroutines.launch

class HistoryTravelViewModel(private val repository: DataBaseRepository) : ViewModel() {

    val allRecordLiveData = MutableLiveData<List<TravelRecord>>().apply {
        this.value = ArrayList<TravelRecord>()
    }


    val hisRecordLiveData = MutableLiveData<List<TravelRecord>>().apply {
        this.value = ArrayList<TravelRecord>()
    }

    val syncIndex = MutableLiveData<Int>().apply {
        this.value = -1
    }

    fun getAllTravelRecord(uid: String, startTime: Long, endTime: Long) {
        viewModelScope.launch {
            allRecordLiveData.value = repository.getAll(uid, startTime, endTime)
        }
    }

    fun getTravelList(startTime: String, endTime: String, page: Int = 0) {
        val userCode = SPUtils.getInstance().getString(NameSpace.UID)

        val paramMap = HashMap<String, String>().apply {
            this["start_time"] = startTime
            this["end_time"] = endTime
            this["usercode"] = userCode
            this["limit"] = "100"
            this["page"] = "0"
        }
        viewModelScope.launch {
            hisRecordLiveData.value = repository.getTravelList(paramMap)
        }
    }


    fun startSync() {
        for (index in 0 until hisRecordLiveData.value!!.size) {
            syncIndex.value = index


        }
    }
}
