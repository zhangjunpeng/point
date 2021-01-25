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
import com.zjdx.point.data.repository.EditRepository
import com.zjdx.point.ui.base.BaseListViewModel
import com.zjdx.point.ui.history.HistoryLocationViewModel
import com.zjdx.point.ui.history.HistoryTravelViewModel
import com.zjdx.point.ui.main.MainViewModel
import kotlinx.coroutines.launch

class EditUserInfoViewModel(val repository: EditRepository) : ViewModel() {

    val sysUserLiveData = MutableLiveData<SysUser>()
    val errorBack = MutableLiveData<SubmitBackModel>()

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
        minsalary: String?,
        maxsalary: String?
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
                minsalary,
                maxsalary
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

class EditUserInfoViewModelFactory(val repository: EditRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditUserInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditUserInfoViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}