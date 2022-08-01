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

    fun addUploadWork(context: Context): WorkRequest? {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadLocationsWork>()
                .setConstraints(constraints)
                .build()
//        WorkManager.getInstance(this).enqueue(uploadWorkRequest)

            WorkManager.getInstance(context).enqueueUniqueWork(
                NameSpace.UploadWorkName,
                ExistingWorkPolicy.REPLACE,
                uploadWorkRequest
            )
            return uploadWorkRequest
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null


    }

    fun addBackDataWork(context: Context): WorkRequest? {
        try {
            val constraints = Constraints.Builder()
                .build()
            val rollBackWorkRequest = OneTimeWorkRequestBuilder<RollbackDataWork>()
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                NameSpace.BackDatadWorkName,
                ExistingWorkPolicy.REPLACE,
                rollBackWorkRequest
            )
            return rollBackWorkRequest
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null


    }

}