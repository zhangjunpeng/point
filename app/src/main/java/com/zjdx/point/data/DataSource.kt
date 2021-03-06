package com.zjdx.point.data


import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.zjdx.point.config.REST
import com.zjdx.point.data.bean.*
import okhttp3.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class DataSource {


    inner class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val t1 = System.currentTimeMillis()

            val response = chain.proceed(request)
            val t2 = System.currentTimeMillis()

            val difference = t2 - t1
//            LogUtils.i("startTime=${t1}\nendtime=${t2}\nfuncationName=${funName}\ndifference=${difference}")

            return response
        }
    }

    fun login(username: String, password: String): Back<LoginModel> {
        try {
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val formBodyBulider = FormBody.Builder()
            val formBody = formBodyBulider
                .add("userCode", username)
                .add("password", password)
                .build()
            val request: Request = Request.Builder()
                .url(REST.login)
                .post(formBody)
                .build()
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
        // TODO: revoke authentication
    }


    fun getAppVersion(): Back<AppVersionModel> {
        try {
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val formBodyBulider = FormBody.Builder()
            val formBody = formBodyBulider.build()
            val request: Request = Request.Builder()
                .url(REST.appVersion)
                .post(formBody)
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
        minsalary: String?,
        maxsalary: String?
    ): Back<SubmitBackModel> {
        try {
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
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
            if (minsalary != null && minsalary.isNotEmpty()) {
                formBodyBulider.add("minsalary", minsalary)
            }
            if (maxsalary != null && maxsalary.isNotEmpty()) {
                formBodyBulider.add("maxsalary", maxsalary)
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
        minsalary: String?,
        maxsalary: String?
    ): Back<SubmitBackModel> {
        try {
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
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
            if (minsalary != null && minsalary.isNotEmpty()) {
                formBodyBulider.add("minsalary", minsalary)
            }
            if (maxsalary != null && maxsalary.isNotEmpty()) {
                formBodyBulider.add("maxsalary", maxsalary)
            }
            val request: Request = Request.Builder()
                .url(REST.editUserInfo)
                .post(formBodyBulider.build())
                .build()
            val call: Call = client.newCall(request)
            val response = call.execute()
//            val type = Types.newParameterizedType( AppVersionModel::class.java,List::class.java,AppVersion::class.java)
            return if (response.isSuccessful) {
                val data = response.body!!.string()
                LogUtils.i(data)
                val model =
                    GsonUtils.fromJson(data, SubmitBackModel::class.java)
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
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val formBodyBulider = FormBody.Builder().add("userCode", userCode)
            val formBody = formBodyBulider.build()
            val request: Request = Request.Builder()
                .url(REST.userInfo)
                .post(formBody)
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


}