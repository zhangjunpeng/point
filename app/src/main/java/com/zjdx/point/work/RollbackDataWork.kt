package com.zjdx.point.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.db.MyDataBase
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.event.UpdateMsgEvent
import com.zjdx.point.utils.DateUtil
import org.greenrobot.eventbus.EventBus

class RollbackDataWork(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    val database by lazy { MyDataBase.getDatabase(context) }

    val repository = DataBaseRepository(database.travelRecordDao(), database.locationDao(),database.tagRecordDao())

    override fun doWork(): Result {
        backTravel()
        return Result.success()
    }


    private fun backTravel() {
        sendMsgEvent("开始恢复数据")
        val locationList = repository.getLocationListGroupByTId()
        var index = 1
        for (loca in locationList) {
            val t_id = loca.tId
            var travelRecord = repository.getTravelRecordById(t_id)
            if (travelRecord != null) {
                continue
            }
            val firstLocation = repository.getFirsttLocationById(t_id)
            val lastLocation = repository.getLastLocationById(t_id)
            val startTime = DateUtil.dateFormat.parse(firstLocation.creatTime).time
            val endTime = DateUtil.dateFormat.parse(lastLocation.creatTime).time
            if (endTime - startTime < 60 * 1000) {
                continue
            }
            sendMsgEvent("正在恢复第${index}条数据")
            travelRecord = TravelRecord(
                id = t_id,
                createTime = firstLocation.creatTime,
                travelUser = SPUtils.getInstance().getString(NameSpace.UID),
                startTime = startTime,
                endTime = endTime
            )
            val locations = repository.getLocationListById(t_id)
            for (temp in locations) {
                temp.isUpload = 0
            }
            repository.updateLocations(locations)
            repository.insertTravelRecord(travelRecord)
            index++
        }
        sendMsgEvent("恢复数据完成")

    }

    fun sendMsgEvent(msg: String) {
        val event = UpdateMsgEvent()
        event.msg = msg
        EventBus.getDefault().post(event)
    }
}