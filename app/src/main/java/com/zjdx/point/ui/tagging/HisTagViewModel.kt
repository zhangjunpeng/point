package com.zjdx.point.ui.tagging

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.data.bean.Back
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.db.model.TagRecord
import com.zjdx.point.utils.DateUtil
import kotlinx.coroutines.launch
import java.util.*

class HisTagViewModel(private val repository: DataBaseRepository) : ViewModel() {

    val allTagLiveData = MutableLiveData<Map<String, List<TagRecord>>>()

    val delRest = MutableLiveData<Boolean>()


    fun getTagRecordIsUpload() {

        viewModelScope.launch {
            val userCode = SPUtils.getInstance().getString(NameSpace.UID)

            val paramMap = HashMap<String, String>().apply {
//                this["startTime"] = mStartTime
//                this["endTime"] = mEndTime
                this["usercode"] = userCode
                this["limit"] = "1000"
                this["page"] = "0"
            }
            val templist = repository.queryUploadBytime(paramMap)
            val map = HashMap<String, ArrayList<TagRecord>>()
            templist?.forEach {
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


    var mStartTime: String = DateUtil.dateFormat.format(Date().time - (30L * 24 * 60 * 60 * 1000))
    var mEndTime: String = DateUtil.dateFormat.format(Date())


    fun getTagRecordIsUploadByTime(startTime: String, endTime: String) {
        mStartTime = startTime
        mEndTime = endTime

        viewModelScope.launch {
            val userCode = SPUtils.getInstance().getString(NameSpace.UID)

            val paramMap = HashMap<String, String>().apply {
                this["startTime"] = mStartTime
                this["endTime"] = mEndTime
                this["usercode"] = userCode
                this["limit"] = "1000"
                this["page"] = "0"
            }
            val templist = repository.queryUploadBytime(paramMap)
            val map = HashMap<String, ArrayList<TagRecord>>()
            templist?.forEach {
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
            val sortedMap =  map.toSortedMap(compareByDescending { convertDate(it) })
            allTagLiveData.postValue(sortedMap)
        }
    }

    fun convertDate(d: String): String {
        val array = d.split("-")
        return array[2] + array[1] + array[0]
    }


    fun deleteTag(tagid: Int) {
        viewModelScope.launch {

            val userCode = SPUtils.getInstance().getString(NameSpace.UID)
            val back = repository.delTag(userCode, tagid.toString())
            if (back is Back.Success) {
                delRest.postValue(back.data)
            } else {
                delRest.postValue(false)
            }
        }
    }


}
