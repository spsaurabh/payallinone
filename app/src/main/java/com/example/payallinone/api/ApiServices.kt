package com.example.payallinone.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiServices {
    @GET
    fun hitGetApiWithoutJsonParams(@Url url:String): Call<JsonObject>
}