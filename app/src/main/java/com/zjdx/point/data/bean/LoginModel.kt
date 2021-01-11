package com.zjdx.point.data.bean

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginModel (
    val msg: String,
    val code: Long,
    val sysUser: SysUser
)

data class SysUser (
    val id: Long,
    val usercode: String,
    val username: String,
    val password: String,
    val telphone: String,
    val createtime: String,
    val note: String,
    val sex: String,
    val age: Long,
    val address: String,
    val minsalary: String,
    val maxsalary: String
)
