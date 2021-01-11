package com.zjdx.point.data.repository

import com.zjdx.point.data.DataSource
import com.zjdx.point.data.bean.Back
import com.zjdx.point.data.bean.SubmitBackModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RegisterRepository() {
    val dataSource = DataSource()
    suspend fun register(
        userCode: String, userName: String?, password: String,
        telphone: String?,
        note: String?,
        sex: Int?,
        age: String?,
        address: String?,
        minsalary: String?,
        maxsalary: String?
    ):Back<SubmitBackModel> {
      return  withContext(Dispatchers.IO) {
            dataSource.register(
                userCode,
                userName,
                password,
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