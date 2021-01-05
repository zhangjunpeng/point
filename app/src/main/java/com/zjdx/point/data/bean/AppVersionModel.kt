package com.zjdx.point.data.bean

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppVersionModel(
    val code: Int,
    val list: List<AppVersion>,
    val msg: String
)

@JsonClass(generateAdapter = true)
data class AppVersion(
    val downloadurl: String,
    val id: Int,
    val note: String,
    val uploadtime: String,
    val version: Int
)