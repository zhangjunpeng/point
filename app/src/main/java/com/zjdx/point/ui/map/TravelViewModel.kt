package com.zjdx.point.ui.map

import android.util.Log
import androidx.lifecycle.*
import androidx.work.ListenableWorker
import com.zjdx.point.bean.Back
import com.zjdx.point.bean.SubmitBackBean
import com.zjdx.point.config.REST
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TravelViewModel(val repository: TravelRepository, id: String) : ViewModel() {


    val allLication = repository.getLocationById(id).asLiveData()


    fun insertLocation(location: Location) {
        repository.insertLocation(location)
    }


    fun insertTravelRecord(travelRecord: TravelRecord) {
        repository.insertTravelRecord(travelRecord)
    }

    val submitBackBeanLiveData = MutableLiveData<SubmitBackBean>()


    fun uploadLocation() {
        Thread {
            var travelRecord: TravelRecord? = repository.findHasNotUpload()
            if (travelRecord != null) {

                val jsonObject = JSONObject()
                val locations = repository.getLocationListById(travelRecord.id)

                val paramArray = JSONArray()
                if (locations.isNotEmpty()) {
                    locations.forEach { loca ->
                        val locationObj = JSONObject()
                        locationObj.put("longitude", loca.lng)
                        locationObj.put("latitude", loca.lat)
                        locationObj.put("speed", loca.speed)
                        locationObj.put("height", loca.altitude)
                        locationObj.put("accuracy", loca.accuracy)
                        locationObj.put("source", loca.source)
                        locationObj.put("travelposition", loca.address)
                        locationObj.put("collecttime", loca.creatTime)
                        paramArray.put(locationObj)
                    }
                }
                val travelObj = JSONObject()
                travelObj.put("traveltypes", travelRecord.travelTypes)
                travelObj.put("traveluser", travelRecord.travelUser)
                travelObj.put(
                    "traveltime",
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(travelRecord.createTime)
                )

                jsonObject.put("param", paramArray)
                jsonObject.put("object", travelObj)

                val back = postTravel(jsonObject.toString())
                if (back is Back.Success) {
                    travelRecord.isUpload = 1
                    repository.insertTravelRecord(travelRecord)
                    submitBackBeanLiveData.postValue(back.data)
                } else if (back is Back.Error) {
                    submitBackBeanLiveData.postValue(back.error)
                }
            }
        }.start()

    }


    fun postTravel(travelInfo: String): Back<SubmitBackBean> {
        try {

            val mediaType = "application/json; charset=utf-8".toMediaType()

            val client = OkHttpClient()

            val requestBody = travelInfo.toRequestBody(mediaType)

            val request: Request = Request.Builder()
                .url(REST.upload)
                .post(requestBody)
                .build()
            val call: Call = client.newCall(request)
            val response = call.execute()
            if (response.isSuccessful) {
                val data = response.body!!.string()
                Log.i("data", data)
                val jsonObject = JSONObject(data)

                val submitBackBean = SubmitBackBean(
                    code = jsonObject.getInt("code"),
                    msg = jsonObject.getString("msg"),
                    time = Date().time
                )
                return Back.Success(submitBackBean)
            } else {
                val data = response.body!!.string()
                Log.i("data", data)
                val jsonObject = JSONObject(data)
                val submitBackBean = SubmitBackBean(
                    code = jsonObject.getInt("code"),
                    msg = jsonObject.getString("msg"),
                    time = Date().time
                )
                return Back.Error(submitBackBean!!)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return Back.Error(SubmitBackBean(code = 600, data = "", msg = "服务器异常", time =  Date().time))
        }
    }

}

class TravelViewModelFactory(private val repository: TravelRepository, val id: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TravelViewModel(repository, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}