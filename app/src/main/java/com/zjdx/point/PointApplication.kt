package com.zjdx.point

import android.app.Application
import android.content.Context
import android.os.Process
import android.text.TextUtils
import com.blankj.utilcode.util.Utils
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.zjdx.point.data.repository.LocationRepository
import com.zjdx.point.data.repository.RegisterRepository
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.db.MyDataBase
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


class PointApplication : Application() {


    val database by lazy { MyDataBase.getDatabase(this) }
    val locationRepository by lazy { LocationRepository(database.locationDao()) }
    val travelRepository by lazy {
        DataBaseRepository(
            database.travelRecordDao(), database.locationDao(),database.tagRecordDao()
        )
    }
    val registerRepository by lazy { RegisterRepository() }

    override fun onCreate() {
        super.onCreate()
        initCrash()

        Utils.init(this)
    }

    private fun initCrash() {

        Bugly.init(applicationContext, "90cff47ede", true)

    }


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName: String = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader.close()
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null
    }
}