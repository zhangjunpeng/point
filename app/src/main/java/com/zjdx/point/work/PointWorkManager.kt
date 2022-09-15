package com.zjdx.point.work

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import com.zjdx.point.NameSpace
import java.time.Duration

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
                NameSpace.UploadWorkName, ExistingWorkPolicy.REPLACE, uploadWorkRequest
            )
            return uploadWorkRequest
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun addPeriodicWork(context: Context): WorkRequest? {
        try {
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val duration = Duration.ofMinutes(15)
            val uploadWorkRequest =
                PeriodicWorkRequestBuilder<UploadLocationsWork>(duration).setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context).enqueue(
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
            val constraints = Constraints.Builder().build()
            val rollBackWorkRequest =
                OneTimeWorkRequestBuilder<RollbackDataWork>().setConstraints(constraints).build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                NameSpace.BackDatadWorkName, ExistingWorkPolicy.REPLACE, rollBackWorkRequest
            )
            return rollBackWorkRequest
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null


    }

}