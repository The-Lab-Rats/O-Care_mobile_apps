package com.example.mainapplabrats.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiEndpoint {

    const val BASE_URL = "https://newsapi.org/v2/"


    fun getApiClient(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
    // Create Retrofit
    fun getApiJson(): Retrofit{
       return Retrofit.Builder()
            .baseUrl("https://agusfahmi.github.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
