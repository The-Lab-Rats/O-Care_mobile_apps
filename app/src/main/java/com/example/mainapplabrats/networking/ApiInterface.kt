package com.example.mainapplabrats.networking

import com.example.mainapplabrats.model.ModelDiagnosa
import com.example.mainapplabrats.model.ModelNews
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiInterface {
    @GET("top-headlines")
    fun getHealth(
            @Query("country") country: String?,
            @Query("category") category: String?,
            @Query("apiKey") apiKey: String?
    ): Call<ModelNews>

    @GET("DiagnosisAPI/db.json")
    suspend fun getEmployees(): Response<List<ModelDiagnosa>>

}