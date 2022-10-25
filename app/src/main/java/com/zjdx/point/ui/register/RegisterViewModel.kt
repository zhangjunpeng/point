package com.zjdx.point.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zjdx.point.data.bean.Back
import com.zjdx.point.data.bean.SubmitBackModel
import com.zjdx.point.data.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel(val repository: RegisterRepository) : ViewModel() {

    val registerModel = MutableLiveData<SubmitBackModel>()


    val ageList=arrayListOf( "0-17","18-35","36-60","60以上")
    val salaryList= arrayListOf( "0-2000","2000-5000","5000-10000","10000+")
    val hasCarList= arrayListOf("是","否")
    val hasVeList= arrayListOf("是","否")
    val hasBicList= arrayListOf("是","否")
    val yysList= arrayListOf("移动","联通","电信","其他")


    @Synchronized
    fun registerUser(
        userCode: String, userName: String?, password: String,
        telphone: String?,
        note: String?,
        sex: Int?,
        age: String?,
        address: String?,
        salary: String?,
        hasBicycle: Boolean,
        hasCar: Boolean,
        hasVehicle: Boolean,
        yys: String,
        gzdz: String,
    ) {
        viewModelScope.launch {
            val result = repository.register(
                userCode,
                userName,
                password,
                telphone,
                note,
                sex,
                age,
                address,
                salary, hasBicycle, hasCar, hasVehicle, yys, gzdz,
            )
            if (result is Back.Success) {
                registerModel.postValue(result.data)
            } else if (
                result is Back.Error
            ) {
                registerModel.postValue(result.error)
            }
        }
    }
}

class RegisterViewModelFactory(private val repository: RegisterRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}