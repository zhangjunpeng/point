package com.zjdx.point.ui.tagging

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zjdx.point.data.repository.DataBaseRepository
import com.zjdx.point.db.model.TagRecord

class HisDetailViewModel(private val repository: DataBaseRepository) : ViewModel() {

    val tagRecordLiveData = MutableLiveData<TagRecord>()


}
