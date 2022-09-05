package com.zjdx.point.work

import android.content.Context
import android.util.Log
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
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.event.UpdateMsgEvent
import com.zjdx.point.utils.DateUtil
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONObject
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.*

class UploadLocationsWork(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    val database by lazy { MyDataBase.getDatabase(context) }

    val repository = DataBaseRepository(database.travelRecordDao(), database.locationDao(),database.tagRecordDao())

    var location: Location? = null

    var index = 0

    override fun doWork(): Result {

        // Do the work here--in this case, upload the images.

        LogUtils.i("开始uploadwork")
        sendMsgEvent("开始上传任务")
//        backTravel()
        checkTravel()
//        checkTravelTime()

        findAndUpload()

        checkTravelHasNOTUpload()
        sendMsgEvent("结束上传任务")

        return Result.success()
    }


    private fun backTravel() {
        sendMsgEvent("开始恢复数据")
        val locationList = repository.getLocationListGroupByTId()
        var index = 1
        for (loca in locationList) {

            val t_id = loca.tId
            var travelRecord = repository.getTravelRecordById(t_id)
            if (travelRecord == null) {
                sendMsgEvent("正在恢复第${index}条数据")

                val firstLocation = repository.getFirsttLocationById(t_id)
                val lastLocation = repository.getLastLocationById(t_id)
                travelRecord = TravelRecord(
                    id = t_id,
                    createTime = firstLocation.creatTime,
                    travelUser = SPUtils.getInstance().getString(NameSpace.UID),
                    startTime = DateUtil.dateFormat.parse(firstLocation.creatTime).time,
                    endTime = DateUtil.dateFormat.parse(lastLocation.creatTime).time
                )
                val locations = repository.getLocationListById(t_id)
                for (temp in locations) {
                    temp.isUpload = 0
                }
                repository.updateLocations(locations)
                repository.insertTravelRecord(travelRecord)
                index++
            }
        }
        sendMsgEvent("恢复数据完成")

    }

    private fun checkTravel() {
        val travelRecords = repository.getTravelRecordHasEmptyStart()
        if (travelRecords != null && travelRecords.isNotEmpty()) {
            for (item in travelRecords) {
                if (item.startTime == 0L) {
                    val loca = repository.getFirsttLocationById(item.id)
                    if (loca == null) {
                        repository.deteleTravel(item)
                        continue
                    } else {
                        item.startTime = DateUtil.dateFormat.parse(loca.creatTime).time
                    }
                }
                if (item.endTime == 0L) {
                    val loca = repository.getLastLocationById(item.id)
                    if (loca == null) {
                        repository.deteleTravel(item)
                        continue
                    } else {
                        item.endTime = DateUtil.dateFormat.parse(loca.creatTime).time
                    }
                }
                if (item.endTime - item.startTime > 60 * 1000) {
                    repository.updateTravelRecord(item)
                } else {
                    repository.deteleTravel(item)
                }
            }
        }
    }

    private fun checkTravelTime() {
        val travelRecords =
            repository.getAllTravel(SPUtils.getInstance().getString(NameSpace.UID), 0L, Date().time)
        for (record in travelRecords) {
            if (record.endTime - record.startTime < 60 * 1000) {
                val locations = repository.getLocationListById(record.id)
                for (loca in locations) {
                    loca.isUpload = 1
                }
                repository.updateLocations(locations)
                repository.deteleTravel(record)
                sendMsgEvent("发现记录时间少于60秒数据，已删除")
            }
        }
    }


    private fun checkTravelHasNOTUpload() {
        val travelRecords =
            repository.getTravelRecordHasNotUpload(SPUtils.getInstance().getString(NameSpace.UID))

        if (travelRecords != null && travelRecords.isNotEmpty()) {
            for (record in travelRecords) {
                val locationList = repository.getAllListHasNotUploadByTid(record.id)
                if (locationList != null) {
                    if (locationList.isEmpty()) {
                        record.isUpload = 1
                    }
                } else {
                    record.isUpload = 1
                }
                repository.updateTravelRecord(record)
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
                locationObj.put("travelid", loca.tId)
                locationObj.put("travelposition", loca.address)
                locationObj.put("collecttime", loca.creatTime)
                locationObj.put("mcc", loca.mcc)
                locationObj.put("mnc", loca.mnc)
                locationObj.put("lac", loca.lac)
                locationObj.put("cid", loca.cid)
                locationObj.put("bsss", loca.bsss)
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
        travelObj.put(
            "createtime",
            travelRecord!!.createTime
        )
        jsonObject.put("historicalTrack", paramArray)
        jsonObject.put("travelInfo", travelObj)

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

            val client = OkHttpClient.Builder().proxy(
                Proxy(Proxy.Type.HTTP, InetSocketAddress("192.168.0.164", 9090))
            ).build()

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

}