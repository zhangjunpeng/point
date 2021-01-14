package com.zjdx.point.db.model

import java.util.*

data class BaseListSreen(
    var start_time: Long =GregorianCalendar(Calendar.YEAR,Calendar.MONTH-1,Calendar.DATE).time.time,
    var end_time:Long= Date().time,
)
