package com.example.photochooser.data.api

import com.example.photochooser.utils.Constants.API_KEY
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GptAPI {

    @Headers("Authorization: Bearer ${API_KEY}",
        "Content-Type: application/json")
    @POST("v1/chat/completions")
    fun getRecommendation(@Body body: JsonObject): Call<JsonObject>
}