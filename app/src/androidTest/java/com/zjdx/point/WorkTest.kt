package com.zjdx.point

import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.util.LogUtil
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.zjdx.point.bean.SubmitBackBean
import com.zjdx.point.work.UploadLocationsWork
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkTest {
    private lateinit var context: PointApplication

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<PointApplication>()
        val config = Configuration.Builder()
            // Set log level to Log.DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            // Use a SynchronousExecutor here to make it easier to write tests
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }


    @Test
    @Throws(Exception::class)
    fun testUploadLocationsWork() {
        // Define input data

        // Create request
        val request = OneTimeWorkRequestBuilder<UploadLocationsWork>()
            .build()

        val workManager = WorkManager.getInstance(context)
        // Enqueue and wait for result. This also runs the Worker synchronously
        // because we are using a SynchronousExecutor.
        workManager.enqueue(request).result.get()
        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(request.id).get()
        val outputData = workInfo.outputData
        // Assert
        Log.i("outputData", outputData.getInt("code",-1).toString())

        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))

//        print(outputData.getString("msg"))
    }


}