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

    val travelDis =
        arrayListOf("上班", "上课", "业务", "回单位", "回学校", "回家", "就餐", "购物", "就医", "休闲娱乐", "接送人", "其他")

    val types = arrayListOf("居住地", "工作单位/学校", "其它")

    val deleList = ArrayList<TagRecord>()

    var startTime: Date? = null
    var endTime: Date? = null

    var addingTag: TagRecord? = null

    val selectLoaction = MutableLiveData<Location>()

    var hasChange=false

    val addTag = MutableLiveData<Boolean>().apply {
        this.value = false
    }

    val tarvelModelList = ArrayList<String>().apply {
        add("步行")
    }


    val backResult = MutableLiveData<Boolean>()
    fun getLocationsByTime() {
        viewModelScope.launch {
            allLication.value = repository.getLocationListByTime(
                startTime = DateUtil.dateFormat.format(startTime!!),
                endTime = DateUtil.dateFormat.format(endTime!!)
            )
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
                    repository.insertTagList(notUpTagRecord.value!!)
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
                    it.uploadDate=DateUtil.dayFormat.format(Date())
                }
                repository.updateTags(notUpTagRecord.value!!)
                getTagRecordIsNotUpload()
            }
        }


    }

}

