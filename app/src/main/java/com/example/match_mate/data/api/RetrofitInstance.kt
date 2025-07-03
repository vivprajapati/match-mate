package com.example.match_mate.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    const val BASE_URL = "https://randomuser.me/"
    val apiService: MatchMateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Optional if you want Gson converter
            .client(OkHttpClient())
            .build()
            .create(MatchMateApi::class.java)

    }
}