package com.zjdx.point

import android.content.Context
import android.os.Process
import android.text.TextUtils
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.Utils
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.zjdx.point.data.repository.LocationRepository
import com.zjdx.point.data.repository.RegisterRepository
import com.zjdx.point.data.repository.TravelRepository
import com.zjdx.point.db.MyDataBase
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


class PointApplication : MultiDexApplication() {


    val database by lazy { MyDataBase.getDatabase(this) }
    val locationRepository by lazy { LocationRepository(database.locationDao()) }
    val travelRepository by lazy {
        TravelRepository(
            database.travelRecordDao(),
            database.locationDao()
        )
    }
    val registerRepository by lazy { RegisterRepository() }

    override fun onCreate() {
        super.onCreate()


        initCrash()

        Utils.init(this)
    }

    private fun initCrash() {

// 获取当前包名
        val packageName = applicationContext.packageName
// 获取当前进程名
        val processName = getProcessName(Process.myPid())
// 设置是否为上报进程
        val strategy = UserStrategy(applicationContext)
        strategy.isUploadProcess = processName == null || processName == packageName

        CrashReport.initCrashReport(applicationContext, "90cff47ede", true, strategy)

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
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