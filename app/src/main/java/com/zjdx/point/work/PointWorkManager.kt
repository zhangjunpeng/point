package com.zjdx.point.work

import android.content.Context
import android.util.Log
import androidx.work.*
import com.zjdx.point.NameSpace

class PointWorkManager {

    companion object {
        val instance by lazy {
            PointWorkManager()
        }
    }

    fun addUploadWork(context: Context):WorkRequest{
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadLocationsWork>()
            .setConstraints(constraints)
            .build()
//        WorkManager.getInstance(this).enqueue(uploadWorkRequest)

        WorkManager.getInstance(context).enqueueUniqueWork(
            NameSpace.UploadWorkName,
            ExistingWorkPolicy.APPEND,
            uploadWorkRequest
        )
        return uploadWorkRequest

    }

}