package com.zjdx.point.db.model

import androidx.annotation.InspectableProperty
import androidx.room.Embedded
import androidx.room.Relation

data class TravelRecordWithLocation(
    @Embedded val tarvelRecord: TravelRecord,
    @Relation(
        parentColumn = "id",
        entityColumn = "t_id"
    )
    val locationList: List<Location>

)