package com.zjdx.point.ui.main

import android.app.Application
import android.content.Context
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amap.api.location.AMapLocationClient
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.data.bean.AppVersionModel
import com.zjdx.point.data.bean.Back
import com.zjdx.point.data.bean.SubmitBackModel
import com.zjdx.point.data.repository.TravelRepository
import com.zjdx.point.event.UpdateMsgEvent
import com.zjdx.point.utils.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(val repository: TravelRepository) : ViewModel() {


    val travelCountNum = MutableLiveData<Int>()
    val travelNotUploadNum = MutableLiveData<Int>()

    val appVersionModelLiveData = MutableLiveData<AppVersionModel>()
    val submitBackModelLiveData = MutableLiveData<SubmitBackModel>()

    val uploadMsgLiveData = MutableLiveData<ArrayList<String>>().apply {
        this.value = ArrayList()
    }

    fun findTravelNum() {
        travelCountNum.value = repository.getCount()
        travelNotUploadNum.value = repository.getCountNotUpload()
    }


    @WorkerThread
    fun getAppVersion() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val back = repository.getAppVersion()
                if (back is Back.Success) {
                    appVersionModelLiveData.postValue(back.data)
                } else if (back is Back.Error) {
                    submitBackModelLiveData.postValue(back.error)
                }
            }
        }
    }





    fun sendMsgEvent(msg: String) {
        val event = UpdateMsgEvent()
        event.msg = msg
        EventBus.getDefault().post(event)
    }


}