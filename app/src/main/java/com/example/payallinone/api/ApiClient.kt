package com.example.payallinone.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val baseUrl = "https://supay.in/recharge_api/"
    var apiService : ApiServices? = null

    object ApiClient{
        fun getApiService(): ApiServices? {
            if (apiService==null){
                // API response interceptor
                val loggingInterceptor = HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)

                // Client
                val client = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()

                // Retrofit
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                apiService = retrofit.create(ApiServices::class.java)
            }
            return apiService
        }
    }