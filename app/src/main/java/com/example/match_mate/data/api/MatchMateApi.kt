package com.example.match_mate.data.api

import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface MatchMateApi {
    @GET("api/")
    suspend fun fetchUsers(
        @Query("results") results: Int = 10
    ): Response<JsonObject>

    @GET("api/")
    suspend fun fetchMe(
        @Query("results") results: Int = 1
    ): Response<JsonObject>
}