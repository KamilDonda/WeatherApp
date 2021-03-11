package com.example.weatherapp.repository

import com.example.weatherapp.api.WeatherService
import com.example.weatherapp.entity.Data
import retrofit2.awaitResponse

class WeatherRepo {
    companion object {
        suspend fun getWeather(city: String, appid: String): Data =
            WeatherService.api.getWeather(city, appid).awaitResponse().body()!!

        suspend fun getWeather(lat: String, lon: String, appid: String): Data =
            WeatherService.api.getWeather(lat, lon, appid).awaitResponse().body()!!
    }
}