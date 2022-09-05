package com.zjdx.point.ui.tagging

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.data.bean.Back
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TagRecord
import com.zjdx.point.utils.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
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


    val backResult = MutableLiveData<Boolean>().apply {
        this.value = false
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

    fun startUpload() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userCode = SPUtils.getInstance().getString(NameSpace.UID)

                val paramArray = JSONArray()
                if (notUpTagRecord.value!!.isNotEmpty()) {
                    notUpTagRecord.value!!.forEach { tag ->
                        val ta = JSONObject()
                        ta.put("description", tag.desc)
                        ta.put("destination", tag.destination)
                        ta.put("end_time", tag.endTime)
                        ta.put("end_type", tag.endType)
                        ta.put("start_time", tag.startTime)
                        ta.put("start_type", tag.startType)
                        ta.put("travel_types", tag.travelmodel)
                        ta.put("travel_user", userCode)
                        paramArray.put(ta)
                    }
                }
                val backInfo = repository.uploadTagInfo(paramArray.toString())
                if (backInfo is Back.Success){
                    backResult.postValue(backInfo.data)
                }else{
                    backResult.postValue(false)
                }

            }

        }
    }

    fun changeTagRecordIsNotUpload() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                notUpTagRecord.value!!.forEach {
                    it.isupload=1
                }
                repository.updateTags(notUpTagRecord.value!!)
                getTagRecordIsNotUpload()
            }
        }


    }

}

