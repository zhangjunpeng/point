package com.zjdx.point.ui.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.data.bean.Back
import com.zjdx.point.data.bean.SubmitBackModel
import com.zjdx.point.data.bean.SysUser
import com.zjdx.point.data.repository.DataBaseRepository
import kotlinx.coroutines.launch

class EditUserInfoViewModel(val repository: DataBaseRepository) : ViewModel() {

    val sysUserLiveData = MutableLiveData<SysUser>()
    val errorBack = MutableLiveData<SubmitBackModel>()


    val ageList=arrayListOf( "0-17","18-35","36-60","60以上")
    val salaryList= arrayListOf( "0-2000","2000-5000","5000-10000","10000+")
    val hasCarList= arrayListOf("是","否")
    val hasVeList= arrayListOf("是","否")
    val hasBicList= arrayListOf("是","否")


    fun getUserInfo() {
        viewModelScope.launch {
            val userCode = SPUtils.getInstance().getString(NameSpace.UID)
            val back = repository.getUserInfo(userCode)
            if (back is Back.Success) {
                sysUserLiveData.postValue(back.data.list)
            } else if (back is Back.Error) {
                errorBack.postValue(back.error)
            }
        }
    }

    fun editUserInfo(
        id:String,
        userCode: String,
        userName: String?,
        telphone: String?,
        note: String?,
        sex: Int?,
        age: String?,
        address: String?,
        salary: String?,
        hasBicycle: Boolean ,
        hasCar: Boolean ,
        hasVehicle: Boolean,
    ) {
        viewModelScope.launch {
            val result = repository.editUserInfo(
                id,
                userCode,
                userName,
                telphone,
                note,
                sex,
                age,
                address,
                salary,
                hasBicycle, hasCar, hasVehicle

            )
            if (result is Back.Success) {
                errorBack.postValue(result.data)
            } else if (
                result is Back.Error
            ) {
                errorBack.postValue(result.error)
            }
        }
    }

}

