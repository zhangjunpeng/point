package com.zjdx.point.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zjdx.point.db.model.BaseListSreen

open class BaseListViewModel() : ViewModel() {

    var qualityListSreenLiveData = MutableLiveData<BaseListSreen>().apply {
        this.value=BaseListSreen()
    }

}