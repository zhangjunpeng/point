package com.zjdx.point.data.bean


data class AppVersionModel(
    val code: Int,
    val list: List<AppVersion>,
    val msg: String
)

data class AppVersion(
    val downloadurl: String,
    val id: Int,
    val note: String,
    val uploadtime: String,
    val version: Int
)