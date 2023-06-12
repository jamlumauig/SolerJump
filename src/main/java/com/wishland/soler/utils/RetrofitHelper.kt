package com.wishland.soler.utils

import com.wishland.soler.data.data_source.AdsService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {

    //var baseUrl = "https://s2024.com/api/"
    var baseUrl = "https://jamdevvv-default-rtdb.firebaseio.com/"

    fun service(): AdsService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHeaders())
            .build()
            .create(AdsService::class.java)
    }

    private fun getHeaders(): OkHttpClient{
        val httpClient = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor { chain ->
            val request: Request =
                chain.request()
                    .newBuilder()
                    .addHeader("Accept", "*/*")
                    .addHeader("Connection", "Keep-Alive")
                    .build()
            chain.proceed(request)
        }
        return httpClient.build()
    }

}