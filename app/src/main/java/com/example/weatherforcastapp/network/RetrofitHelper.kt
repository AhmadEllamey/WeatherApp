package com.example.weatherforcastapp.network

import com.example.weatherforcastapp.model.APIAnswer
import com.example.weatherforcastapp.model.AlertsAnswer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitHelper {

    @GET
    fun getTemp(@Url link : String): Call<APIAnswer>

    @GET
    fun getAlert(@Url link : String): Call<AlertsAnswer>


}