package com.zjdx.point.data.bean


/**
 * Authentication result : success (user details) or error message.
 */
data class LoginModel (
    val msg: String,
    val code: Int,
    val sysUser: SysUser
)

data class SysUser(
    var address: String?,
    val age: Int,
    val createtime: String,
    val id: Int,
    var maxsalary: String?,
    var minsalary: String?,
    var note: String?,
    val password: String,
    var sex: String?,
    var telphone: String?,
    val usercode: String,
    var username: String?
)