package com.example.weatherapp.api

import com.example.weatherapp.entity.Data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("weather?")
    fun getWeather(
        @Query("q") city: String,
        @Query("appid") appid: String
    ): Call<Data>
}