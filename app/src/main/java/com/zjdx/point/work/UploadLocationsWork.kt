package com.zjdx.point.work

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.data.bean.Back
import com.zjdx.point.data.bean.SubmitBackModel
import com.zjdx.point.config.REST
import com.zjdx.point.db.MyDataBase
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.data.repository.TravelRepository
import com.zjdx.point.event.UpdateMsgEvent
import com.zjdx.point.utils.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
) : Worker(context, workerParams) {

    val database by lazy { MyDataBase.getDatabase(context) }

    val repository = TravelRepository(database.travelRecordDao(), database.locationDao())

    var location: Location? = null

    var index = 0

    override fun doWork(): Result {

        // Do the work here--in this case, upload the images.

        LogUtils.i("开始uploadwork")
        sendMsgEvent("开始上传任务")
        checkTravel()
        findAndUpload()
        sendMsgEvent("结束上传任务")

        return Result.success()
    }

    private fun checkTravel() {
        val travelRecords=repository.getTravelRecordHasEmptyStart()
        if (travelRecords!=null&& travelRecords.isNotEmpty()){
            for (item in travelRecords){
                if (item.startTime==0L){
                    val loca=repository.getFirsttLocationById(item.id)
                    if (loca==null){
                        repository.deteleTravel(item)
                        continue
                    }else{
                        item.startTime=DateUtil.dateFormat.parse(loca.creatTime).time
                    }
                }else if (item.endTime==0L){
                    val loca=repository.getLastLocationById(item.id)
                    if (loca==null){
                        repository.deteleTravel(item)
                        continue
                    }else{
                        item.endTime=DateUtil.dateFormat.parse(loca.creatTime).time
                    }
                }
                if (item.endTime-item.startTime>60*1000){
                    repository.updateTravelRecord(item)
                }else{
                    repository.deteleTravel(item)
                }
            }
        }
    }

    @Synchronized
    private fun findAndUpload() {
        location = repository.queryOneHasNotUploadByTid()

        while (location != null) {
            val travelRecord = repository.getTravelRecordById(location!!.tId)

            if (travelRecord == null) {
                sendMsgEvent("发现无效数据，正在清理。。")

                val locations = repository.getLocationListById(location!!.tId)
                for (loca in locations) {
                    loca.isUpload = 1
                }
                repository.updateLocations(locations)
                sendMsgEvent("清理完成")

            } else {
                travelRecord.let {
                    index++
                    sendMsgEvent("开始上传第${index}条出行数据")
                    val allLocations = repository.getAllListHasNotUploadByTid(travelRecord!!.id)
                    sendMsgEvent("本次出行共记录点位${allLocations.size}个")
                    val locations = repository.getLocationsHasNotUpload(travelRecord!!.id)
                    UploadLocation(locations as ArrayList<Location>, travelRecord!!)
                }

            }

            location = repository.queryOneHasNotUploadByTid()
        }
        sendMsgEvent("无更多未上传出行数据！")
    }


    fun UploadLocation(
        locationList: ArrayList<Location>,
        travelRecord: TravelRecord
    ) {

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
            sendMsgEvent("成功：第${index}条出行数据上传成功")
        } else {
            UploadLocation(locations as ArrayList<Location>, travelRecord)

        }


    }

    fun sendMsgEvent(msg: String) {
        val event = UpdateMsgEvent()
        event.msg = msg
        EventBus.getDefault().post(event)
    }


    fun postTravel(travelInfo: String): Back<SubmitBackModel> {
        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()

            val client = OkHttpClient.Builder().build()

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
            e.printStackTrace()
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


    suspend fun checkHasNOEndTime() {
        withContext(Dispatchers.IO) {
            val travelRecord = repository.getTravelRecordById(
                SPUtils.getInstance().getString(NameSpace.RECORDINGID)
            )
            val laction = repository.getLastLocationById(
                SPUtils.getInstance().getString(NameSpace.RECORDINGID)
            )
            if (travelRecord == null) {
                repository.deteleLocation(
                    repository.getLocationListById(
                        SPUtils.getInstance().getString(NameSpace.RECORDINGID)
                    )
                )
            } else {

                travelRecord.endTime = DateUtil.dateFormat.parse(laction.creatTime).time
                if (travelRecord.endTime - travelRecord.startTime < 60 * 1000) {
                    val locations = repository.getLocationListById(travelRecord.id)
                    repository.deteleLocation(locations)
                    repository.deteleTravel(travelRecord)
                    sendMsgEvent("已成功清理无效数据")

                } else {
                    repository.updateTravelRecord(travelRecord)
                    sendMsgEvent("已成功处理上次出行数据")
                }
            }

            SPUtils.getInstance().put(NameSpace.ISRECORDING, false)
            SPUtils.getInstance().put(NameSpace.RECORDINGID, "")

        }

    }
}