package com.example.photochooser.data.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GptAPI {
    @POST("v1/chat/completions")
    fun getRecommendation(@Body body: JsonObject): Call<JsonObject>
}