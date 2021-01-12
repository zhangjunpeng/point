package com.zjdx.point.work

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blankj.utilcode.util.LogUtils
import com.zjdx.point.data.bean.Back
import com.zjdx.point.data.bean.SubmitBackModel
import com.zjdx.point.config.REST
import com.zjdx.point.db.MyDataBase
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.data.repository.TravelRepository
import com.zjdx.point.event.UpdateMsgEvent
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class UploadLocationsWork(
    val context: Context,
    workerParams: WorkerParameters
) :
    Worker(context, workerParams) {

    val database by lazy { MyDataBase.getDatabase(context) }

    val repository = TravelRepository(database.travelRecordDao(), database.locationDao())

    var travelRecord: TravelRecord? = null

    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {

        // Do the work here--in this case, upload the images.
        LogUtils.i("开始uploadwork")
        sendMsgEvent("开始上传任务")
        findAndUpload()
        sendMsgEvent("结束上传任务")
        return Result.Success()
    }

    private fun findAndUpload() {
        travelRecord = repository.findHasNotUpload()

        while (travelRecord != null) {
            travelRecord?.let {
                val locations = repository.getLocationsHasNotUpload(travelRecord!!.id)
                UploadLocation(locations as ArrayList<Location>, travelRecord!!)
            }
            travelRecord = repository.findHasNotUpload()
        }
        sendMsgEvent("无更多未上传出行数据！")
    }


    private fun UploadLocation(
        locationList: ArrayList<Location>,
        travelRecord: TravelRecord
    ) {
        sendMsgEvent("开始上传travelid=${travelRecord.id}")

        val jsonObject = JSONObject()
        val paramArray = JSONArray()
        if (locationList.isNotEmpty()) {
            locationList.forEach { loca ->
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
        travelObj.put("traveluser", travelRecord!!.travelUser)
        travelObj.put("travelid", travelRecord!!.id)
        travelObj.put(
            "traveltime",
            travelRecord!!.createTime
        )

        jsonObject.put("param", paramArray)
        jsonObject.put("object", travelObj)

        val back = postTravel(jsonObject.toString())
        if (back is Back.Success) {
           sendMsgEvent("成功：上传点位${locationList.size}个")
            for (loca in locationList) {
                loca.isUpload = 1
            }
            repository.updateLocations(locationList)
        } else if (back is Back.Error) {
            LogUtils.i("上传结果异常：${back.error.msg},结束上传work")
            sendMsgEvent("失败：上传结果异常：${back.error.msg},结束上传")

            WorkManager.getInstance(context).cancelWorkById(this.id)

        }


        val locations = repository.getLocationsHasNotUpload(travelRecord.id)
        if (locations.isNullOrEmpty()) {
            travelRecord.isUpload = 1
            repository.insertTravelRecord(travelRecord)
            sendMsgEvent("成功：id=${travelRecord.id}上传成功")
        } else {
            UploadLocation(locations as ArrayList<Location>, travelRecord)

        }


    }

    fun sendMsgEvent(msg:String){
        val event=UpdateMsgEvent()
        event.msg=msg
        EventBus.getDefault().post(event)
    }


    fun postTravel(travelInfo: String): Back<SubmitBackModel> {
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
                val submitBackBean = SubmitBackModel(
                    code = jsonObject.getInt("code"),
                    msg = jsonObject.getString("msg"),
                    time = Date().time
                )
                return Back.Success(submitBackBean)
            } else {
                val data = response.body!!.string()
                Log.i("data", data)
                val jsonObject = JSONObject(data)
                val submitBackBean = SubmitBackModel(
                    code = jsonObject.getInt("code"),
                    msg = jsonObject.getString("msg"),
                    time = Date().time
                )
                return Back.Error(submitBackBean)
            }
        } catch (e: java.lang.Exception) {
            return Back.Error(
                SubmitBackModel(
                    code = 600,
                    data = "",
                    msg = "服务器异常",
                    time = Date().time
                )
            )
        }
    }
}