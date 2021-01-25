package com.zjdx.point.data.repository

import com.zjdx.point.data.DataSource
import com.zjdx.point.data.bean.Back
import com.zjdx.point.data.bean.SubmitBackModel
import com.zjdx.point.data.bean.SysUser
import com.zjdx.point.data.bean.UserInfoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditRepository() {
    val dataSource = DataSource()
    suspend fun getUserInfo(userCode: String): Back<UserInfoModel> {
        return withContext(Dispatchers.IO) {
            dataSource.getUserInfo(userCode)
        }
    }

    suspend fun editUserInfo(
        id: String,
        userCode: String,
        userName: String?,
        telphone: String?,
        note: String?,
        sex: Int?,
        age: String?,
        address: String?,
        minsalary: String?,
        maxsalary: String?
    ): Back<SubmitBackModel> {
        return withContext(Dispatchers.IO) {
            dataSource.editUserInfo(
                id,
                userCode,
                userName,
                telphone,
                note,
                sex,
                age,
                address,
                minsalary,
                maxsalary
            )
        }
    }
}