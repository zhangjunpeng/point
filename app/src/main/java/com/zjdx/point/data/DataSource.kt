package com.zjdx.point.data


import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.zjdx.point.config.REST
import com.zjdx.point.data.bean.*
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.utils.DateUtil
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.InetSocketAddress
import java.net.Proxy


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class DataSource {


    val client = OkHttpClient.Builder().build()


    fun login(username: String, password: String): Back<LoginModel> {
        try {
//            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val formBodyBulider = FormBody.Builder()
            val formBody =
                formBodyBulider.add("userCode", username).add("password", password).build()
            val request: Request = Request.Builder().url(REST.login).post(formBody).build()
            val call: Call = client.newCall(request)
            val response = call.execute()
            return if (response.isSuccessful) {

                val model = GsonUtils.fromJson(response.body!!.string(), LoginModel::class.java)

                Back.Success(model!!)
            } else {
                Back.Error(
                    GsonUtils.fromJson(
                        response.body!!.string(), SubmitBackModel::class.java
                    )!!
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            return getErrorSubmitBack(e)
        }
    }


    fun logout() {
    }


    fun getAppVersion(): Back<AppVersionModel> {
        try {
//            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()

            val request: Request = Request.Builder()
                .url(REST.appVersion)
                .get()
                .build()
            val call: Call = client.newCall(request)
            val response = call.execute()
//            val type = Types.newParameterizedType( AppVersionModel::class.java,List::class.java,AppVersion::class.java)
            return if (response.isSuccessful) {
                val appVersionModel =
                    GsonUtils.fromJson(response.body!!.string(), AppVersionModel::class.java)
                Back.Success(appVersionModel!!)
            } else {

                Back.Error(
                    GsonUtils.fromJson(
                        response.body!!.string(), SubmitBackModel::class.java
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return getErrorSubmitBack(e)
        }
    }

    fun register(
        userCode: String, userName: String?, password: String,
        telphone: String?,
        note: String?,
        sex: Int?,
        age: String?,
        address: String?,
        salary: String?,
        hasBicycle: Boolean ,
        hasCar: Boolean ,
        hasVehicle: Boolean,
    ): Back<SubmitBackModel> {
        try {
//            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val formBodyBulider = FormBody.Builder()
            formBodyBulider
                .add("usercode", userCode)
                .add("password", password)
            if (userName != null && userName.isNotEmpty()) {
                formBodyBulider.add("username", userName)
            }
            if (telphone != null && telphone.isNotEmpty()) {
                formBodyBulider.add("telphone", telphone)
            }
            if (note != null && note.isNotEmpty()) {
                formBodyBulider.add("note", note)
            }
            if (sex != null) {
                formBodyBulider.add("sex", sex.toString())
            }
            if (age != null && age.isNotEmpty()) {
                formBodyBulider.add("age", age.toString())
            }
            if (address != null && address.isNotEmpty()) {
                formBodyBulider.add("address", address)
            }
            if (salary != null && salary.isNotEmpty()) {
                formBodyBulider.add("salary", salary)
            }
            if (hasBicycle) {
                formBodyBulider.add("has_bicycle", "1")
            } else {
                formBodyBulider.add("has_bicycle", "0")
            }
            if (hasCar) {
                formBodyBulider.add("has_car", "1")
            } else {
                formBodyBulider.add("has_car", "0")
            }
            if (hasVehicle) {
                formBodyBulider.add("has_vehicle", "1")
            } else {
                formBodyBulider.add("has_vehicle", "0")
            }
            val request: Request = Request.Builder()
                .url(REST.register)
                .post(formBodyBulider.build())
                .build()
            val call: Call = client.newCall(request)
            val response = call.execute()
//            val type = Types.newParameterizedType( AppVersionModel::class.java,List::class.java,AppVersion::class.java)
            return if (response.isSuccessful) {
                val model =
                    GsonUtils.fromJson(response.body!!.string(), SubmitBackModel::class.java)
                Back.Success(model)
            } else {
                val model =
                    GsonUtils.fromJson(response.body!!.string(), SubmitBackModel::class.java)
                Back.Error(model)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return getErrorSubmitBack(e)
        }

    }


    fun editUserInfo(
        id: String,
        userCode: String, userName: String?,
        telphone: String?,
        note: String?,
        sex: Int?,
        age: String?,
        address: String?,
        salary: String?,
        hasBicycle: Boolean ,
        hasCar: Boolean ,
        hasVehicle: Boolean,
    ): Back<SubmitBackModel> {
        try {
//            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val formBodyBulider = FormBody.Builder()
            formBodyBulider
                .add("id", id)
                .add("usercode", userCode)
            if (userName != null && userName.isNotEmpty()) {
                formBodyBulider.add("username", userName)
            }
            if (telphone != null && telphone.isNotEmpty()) {
                formBodyBulider.add("telphone", telphone)
            }
            if (note != null && note.isNotEmpty()) {
                formBodyBulider.add("note", note)
            }
            if (sex != null) {
                formBodyBulider.add("sex", sex.toString())
            }
            if (age != null && age.isNotEmpty()) {
                formBodyBulider.add("age", age.toString())
            }
            if (address != null && address.isNotEmpty()) {
                formBodyBulider.add("address", address)
            }
            if (salary != null && salary.isNotEmpty()) {
                formBodyBulider.add("salary", salary)
            }
            if (hasBicycle) {
                formBodyBulider.add("has_bicycle", "1")
            } else {
                formBodyBulider.add("has_bicycle", "0")
            }
            if (hasCar) {
                formBodyBulider.add("has_car", "1")
            } else {
                formBodyBulider.add("has_car", "0")
            }
            if (hasVehicle) {
                formBodyBulider.add("has_vehicle", "1")
            } else {
                formBodyBulider.add("has_vehicle", "0")
            }
            val request: Request =
                Request.Builder().url(REST.editUserInfo).post(formBodyBulider.build()).build()
            val call: Call = client.newCall(request)
            val response = call.execute()
//            val type = Types.newParameterizedType( AppVersionModel::class.java,List::class.java,AppVersion::class.java)
            return if (response.isSuccessful) {
                val data = response.body!!.string()
                LogUtils.i(data)
                val model = GsonUtils.fromJson(data, SubmitBackModel::class.java)
                Back.Success(model)
            } else {
                val data = response.body!!.string()
                LogUtils.i(data)
                val model =
                    GsonUtils.fromJson(data, SubmitBackModel::class.java)
                Back.Error(model)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return getErrorSubmitBack(e)
        }

    }

    fun getUserInfo(userCode: String): Back<UserInfoModel> {
        try {
//            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()

            val urlStringBuffer = StringBuffer(REST.userInfo)
            urlStringBuffer.append("?")
            urlStringBuffer.append("userCode=$userCode")

            val requestBuilder =
                Request.Builder()
            requestBuilder.url(urlStringBuffer.toString())

            val request = requestBuilder.get()
                .build()

            val call: Call = client.newCall(request)
            val response = call.execute()
//            val type = Types.newParameterizedType( AppVersionModel::class.java,List::class.java,AppVersion::class.java)
            return if (response.isSuccessful) {
                val data = response.body!!.string()
                LogUtils.i(data)
                val sysUser =
                    GsonUtils.fromJson(data, UserInfoModel::class.java)
                Back.Success(sysUser!!)
            } else {
                Back.Error(
                    GsonUtils.fromJson(
                        response.body!!.string(), SubmitBackModel::class.java
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return getErrorSubmitBack(e)
        }
    }


    private fun getErrorSubmitBack(e: Throwable): Back.Error {
        val submitBackModel = SubmitBackModel(
            code = 600,
            msg = e.toString(),
            time = 0
        )
        return Back.Error(submitBackModel)
    }

    fun getTravelListByTime(paramMap: Map<String, String>): HisTravelModel? {

        try {
            val urlStringBuffer = StringBuffer(REST.hisTravel)
            if (paramMap.keys.isNotEmpty()) {
                urlStringBuffer.append("?")
                paramMap.keys.forEach { t ->
                    urlStringBuffer.append("$t=${paramMap[t]}&")
                }
            }
            val requestBuilder =
                Request.Builder()
            requestBuilder.url(urlStringBuffer.toString())

            val request = requestBuilder.get()
                .build()
            val call: Call = client.newCall(request)
            val response = call.execute()
            val dataStr = response.body!!.string()
            return if (response.isSuccessful) {
                val jsonObject = JSONObject(dataStr)
                val jsonArr = jsonObject.getJSONArray("list")
                val tempmodel = GsonUtils.fromJson(dataStr, HisTravelModel::class.java)

                val travelRecordList = ArrayList<TravelRecord>()
                for (index in 0 until jsonArr.length()) {
                    val jObj = jsonArr.getJSONObject(index)
                    travelRecordList.add(
                        TravelRecord(
                            id = jObj.getString("travelid"),
                            createTime = jObj.getString("traveltime"),
                            startTime = DateUtil.dateFormat.parse(jObj.getString("traveltime")).time,
                            endTime = 0,
                            travelTypes = jObj.getString("traveltypes"),
                            travelUser = jObj.getString("traveluser"),
                            isUpload = 1,
                        )
                    )
                }
                HisTravelModel(
                    code = tempmodel.code,
                    msg = tempmodel.msg,
                    count = tempmodel.count,
                    list = travelRecordList
                )

            } else {
               null
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return  null
        }

    }


    fun getLocationListById(paramMap: Map<String, String>): HisLocationModel? {

        try {
            val urlStringBuffer = StringBuffer(REST.hisLocation)
            if (paramMap.keys.isNotEmpty()) {
                urlStringBuffer.append("?")
                paramMap.keys.forEach { t ->
                    urlStringBuffer.append("$t=${paramMap[t]}&")
                }
            }
            val requestBuilder =
                Request.Builder()
            requestBuilder.url(urlStringBuffer.toString())

            val request = requestBuilder.get()
                .build()
            val call: Call = client.newCall(request)
            val response = call.execute()
            val dataStr = response.body!!.string()
            return if (response.isSuccessful) {
                val jsonObject = JSONObject(dataStr)
                val jsonArr = jsonObject.getJSONArray("list")
                val tempmodel = GsonUtils.fromJson(dataStr, HisTravelModel::class.java)

                val travelLocationList = ArrayList<Location>()
                for (index in 0 until jsonArr.length()) {
                    val jObj = jsonArr.getJSONObject(index)
                    travelLocationList.add(
                        Location(
                            tId = jObj.getString("travelid"),
                            isUpload = 1,
                            lat = when (jObj.getString("latitude") == null) {
                                true -> 0.0
                                false -> jObj.getString("latitude").toDouble()
                            },

                            lng = when (jObj.getString("longitude") == null) {
                                true -> 0.0
                                false -> jObj.getString("longitude").toDouble()
                            },

                            speed = when (jObj.getString("speed") == null) {
                                true -> 0.0f
                                false -> jObj.getString("speed").toFloat()
                            },


                            direction = when (jObj.getString("direction") == null) {
                                true -> ""
                                false -> jObj.getString("direction")
                            },
                            altitude = when (jObj.getString("height") == null) {
                                true -> 0.0
                                false -> jObj.getString("height").toDouble()
                            },

                            accuracy = when (jObj.getString("accuracy") == null) {
                                true -> 0.0f
                                false -> jObj.getString("accuracy").toFloat()
                            },

                            source = when (jObj.getString("source") == null) {
                                true -> ""
                                false -> jObj.getString("source")
                            },


                            address = when (jObj.getString("travelposition").toString() ==  "null") {
                                true -> ""
                                false -> jObj.getString("travelposition")
                            },
                            creatTime = when (jObj.getString("collecttime").toString() ==  "null") {
                                true -> ""
                                false -> jObj.getString("collecttime")
                            },

                            mcc = when (jObj.getString("mcc").toString() == "null") {
                                true -> 0
                                false -> jObj.getString("mcc").toInt()
                            },
                            mnc = when (jObj.getString("mnc").toString() ==  "null") {
                                true -> 0
                                false -> jObj.getString("mnc").toInt()
                            },
                            cid = when (jObj.getString("cid").toString() ==  "null") {
                                true -> 0
                                false -> jObj.getString("cid").toInt()
                            },
                            bsss = when (jObj.getString("bsss").toString() ==  "null") {
                                true -> 0
                                false -> jObj.getString("bsss").toInt()
                            },
                            lac = when (jObj.getString("lac").toString() ==  "null") {
                                true -> 0
                                false -> jObj.getString("lac").toInt()
                            }
                        )
                    )
                }
                HisLocationModel(
                    code = tempmodel.code,
                    msg = tempmodel.msg,
                    count = tempmodel.count,
                    list = travelLocationList
                )
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return null
        }

    }

    fun uploadTagInfo(
        data: String
    ): Back<Boolean> {
        try {
            val request: Request = Request.Builder().url(REST.addTag).post(data.toRequestBody())
                .addHeader("content-type", "application/json").build()
            val call: Call = client.newCall(request)
            val response = call.execute()
//            val type = Types.newParameterizedType( AppVersionModel::class.java,List::class.java,AppVersion::class.java)
            return if (response.isSuccessful) {
                val jsonObject=JSONObject(response.body!!.string())
                if (jsonObject.getInt("code")==0){
                    Back.Success(true)
                }else{
                    Back.Success(false)
                }
            } else {
                Back.Error(
                    GsonUtils.fromJson(
                        response.body!!.string(), SubmitBackModel::class.java
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return getErrorSubmitBack(e)
        }

    }


}