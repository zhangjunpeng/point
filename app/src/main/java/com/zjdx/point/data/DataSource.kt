package com.zjdx.point.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.zjdx.point.config.REST
import com.zjdx.point.data.bean.*
import okhttp3.*
import java.util.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class DataSource {

    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()


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

    fun login(username: String, password: String): Back<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(UUID.randomUUID().toString(), "Jane Doe")
            return Back.Success(fakeUser)
        } catch (e: Throwable) {
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
            val jsonAdapter = moshi.adapter<AppVersionModel>(AppVersionModel::class.java)
            return if (response.isSuccessful) {
                val appVersionModel = jsonAdapter.fromJson(response.body!!.string())
                Back.Success(appVersionModel!!)
            } else {
                val smJsonAdapter = moshi.adapter(SubmitBackModel::class.java)

                Back.Error(
                    smJsonAdapter.fromJson(
                        response.body!!.string()
                    )!!
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