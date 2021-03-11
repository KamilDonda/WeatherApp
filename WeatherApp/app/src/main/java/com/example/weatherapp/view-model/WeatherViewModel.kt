package com.example.weatherapp.`view-model`

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.entity.Data
import com.example.weatherapp.entity.Weather
import com.example.weatherapp.repository.WeatherRepo
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _weather: MutableLiveData<Data> = MutableLiveData()
    val weather: LiveData<Data>
        get() = _weather

    fun getWeather(city: String, appid: String) {
        viewModelScope.launch {
            _weather.value = WeatherRepo.getWeather(city, appid)
        }
    }
}