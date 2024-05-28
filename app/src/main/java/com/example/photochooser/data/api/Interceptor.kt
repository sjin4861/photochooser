package com.example.photochooser.data.api

import com.example.photochooser.utils.Constants
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Authorization", "Bearer ${Constants.API_KEY}")
            .header("Content-Type", "application/json")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
