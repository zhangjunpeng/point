package com.zjdx.point.work

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.zjdx.point.bean.Back
import com.zjdx.point.bean.SubmitBackBean
import com.zjdx.point.config.REST
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.repository.TravelRepository
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class UploadLocationsWork(
    val context: Context,
    val repository: TravelRepository,
    workerParams: WorkerParameters
) :
    Worker(context, workerParams) {


    override fun doWork(): Result {

        // Do the work here--in this case, upload the images.

        // Indicate whether the work finished successfully with the Result
        return UploadLocation()
    }


    private fun UploadLocation(): Result {
        var travelRecord: TravelRecord? = repository.findHasNotUpload()
        if (travelRecord != null) {

            val jsonObject = JSONObject()
            val locations = repository.getLocationListById(travelRecord.id)

            val paramArray = JSONArray()
            if (locations.isNotEmpty()) {
                locations.forEach { loca ->
                    val locationObj = JSONObject()
                    locationObj.put("longitude", loca.lng)
                    locationObj.put("latitude", loca.lng)
                    locationObj.put("speed", loca.lng)
                    locationObj.put("height", loca.lng)
                    locationObj.put("accuracy", loca.lng)
                    locationObj.put("source", loca.lng)
                    locationObj.put("travelposition", loca.lng)
                    locationObj.put("collecttime", loca.lng)
                    paramArray.put(locationObj)
                }
            }
            val travelObj = JSONObject()
            travelObj.put("traveltypes", travelRecord.travelTypes)
            travelObj.put("traveluser", travelRecord.travelUser)
            travelObj.put("travelid", travelRecord.id)
            travelObj.put(
                "traveltime",
                travelRecord.createTime
            )

            jsonObject.put("param", paramArray)
            jsonObject.put("object", travelObj)

            val back = postTravel(jsonObject.toString())
            if (back is Back.Success) {
                travelRecord.isUpload = 1
                repository.insertTravelRecord(travelRecord)
                return Result.success()
            } else {

                return Result.retry()
            }
        }
        return Result.retry()
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
                return Back.Error(submitBackBean)
            }
        } catch (e: java.lang.Exception) {
            return Back.Error(
                SubmitBackBean(
                    code = 600,
                    data = "",
                    msg = "服务器异常",
                    time = Date().time
                )
            )
        }
    }
}