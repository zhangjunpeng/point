package com.zjdx.point.ui.travel

import android.util.Log
import androidx.lifecycle.*
import com.zjdx.point.bean.Back
import com.zjdx.point.bean.SubmitBackBean
import com.zjdx.point.config.REST
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.repository.TravelRepository
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class TravelViewModel(val repository: TravelRepository, val id: String) : ViewModel() {


    val allLication = MutableLiveData<MutableList<Location>>().apply {
        this.value = repository.getLocationListById(id)
    }


    fun getLocationsById(tid:String){
        allLication.value= repository.getLocationListById(tid)
    }


    fun insertLocation(location: Location) {
        repository.insertLocation(location)
    }


    fun insertTravelRecord(travelRecord: TravelRecord) {
        repository.insertTravelRecord(travelRecord)
    }


}

class TravelViewModelFactory(private val repository: TravelRepository, val id: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TravelViewModel(repository, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}