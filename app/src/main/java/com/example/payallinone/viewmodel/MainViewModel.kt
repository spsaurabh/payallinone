package com.example.payallinone.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.payallinone.api.ApiClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    var apiObj=MutableLiveData<JsonObject>()
    var isApiFailure= MutableLiveData<Boolean>()
    fun hitHomePageApi(apiEndpoint:String){
        val apiConfig = ApiClient.getApiService()?.hitGetApiWithoutJsonParams(apiEndpoint)
        apiConfig?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    Log.e("MainViewModel","MainApiObject${response.body()}")
                    apiObj.value=response.body()
                }else{
                    isApiFailure.value=true
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                isApiFailure.value=true
            }
        })
    }
}