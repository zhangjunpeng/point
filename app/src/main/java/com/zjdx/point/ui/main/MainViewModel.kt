package com.zjdx.point.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zjdx.point.repository.TravelRepository

class MainViewModel(val repository: TravelRepository) : ViewModel() {


    val travelCountNum = MutableLiveData<Int>()
    val travelNotUploadNum = MutableLiveData<Int>()

    fun findTravelNum(){
        travelCountNum.value=repository.getCount()
        travelNotUploadNum.value=repository.getCountNotUpload()
    }

}