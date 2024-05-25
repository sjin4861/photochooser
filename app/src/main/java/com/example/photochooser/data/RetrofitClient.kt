package com.example.photochooser.data

import com.example.photochooser.utils.Constants.BASE_URL
import com.example.photochooser.data.api.GptAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val instance: GptAPI by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(GptAPI::class.java)
    }
}
