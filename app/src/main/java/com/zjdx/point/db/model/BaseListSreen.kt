package com.zjdx.point.db.model

import java.util.*

data class BaseListSreen(
    var start_time: Long = Date().time - (30L * 24 * 60 * 60 * 1000),
    var end_time: Long = Date().time,
)
