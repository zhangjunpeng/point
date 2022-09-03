package com.zjdx.point.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryTravelViewModel(private val repository: DataBaseRepository) : ViewModel() {

    val allRecordLiveData = MutableLiveData<List<TravelRecord>>().apply {
        this.value = ArrayList<TravelRecord>()
    }


    val hisRecordLiveData = MutableLiveData<ArrayList<TravelRecord>>().apply {
        this.value = ArrayList<TravelRecord>()
    }

    val hisLocationLiveData = MutableLiveData<ArrayList<Location>>().apply {
        this.value = ArrayList<Location>()
    }

    val syncIndex = MutableLiveData<Int>().apply {
        this.value = -1
    }

    val syncLocationCount = MutableLiveData<Int>().apply {
        this.value = 0
    }

    fun getAllTravelRecord(uid: String, startTime: Long, endTime: Long) {
        viewModelScope.launch {
            allRecordLiveData.value = repository.getAll(uid, startTime, endTime)
        }
    }

    fun getTravelList(startTime: String, endTime: String, page: Int = 0) {
        val userCode = SPUtils.getInstance().getString(NameSpace.UID)

        val paramMap = HashMap<String, String>().apply {
            this["startTime"] = startTime
            this["endTime"] = endTime
            this["usercode"] = userCode
            this["limit"] = "100"
            this["page"] = "0"
        }
        viewModelScope.launch {

            val hisTravelModel = repository.getTravelList(paramMap)
            if (hisTravelModel != null) {
                hisRecordLiveData.postValue(hisTravelModel.list)
            }
        }
    }


    var page = 0

    suspend fun getLocationList(travleId: String, isFreash: Boolean = false) {
        val userCode = SPUtils.getInstance().getString(NameSpace.UID)
        if (isFreash) {
            page = 0
        } else {
            page++
        }

        val paramMap = HashMap<String, String>().apply {
            this["usercode"] = userCode
            this["travel_id"] = travleId
            this["limit"] = "1000"
            this["page"] = page.toString()
        }
        val tempModel = repository.getHisLocationListById(paramMap)
        if (tempModel != null) {
            hisLocationLiveData.postValue(tempModel!!.list)
            if (tempModel!!.list.size > 0) {
                repository.insertLocationArray(hisLocationLiveData.value!!)
                getLocationList(travleId)
                syncLocationCount.postValue(syncLocationCount.value!! + tempModel.list.size)
            }

        }
    }


    fun startSync() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                for (index in 0 until hisRecordLiveData.value!!.size) {
                    syncIndex.postValue(index)
                    val model = hisRecordLiveData.value!![index]
                    repository.insertTravelRecord(model)
                    getLocationList(model.id, isFreash = true)
                }
            }
        }

    }
}
