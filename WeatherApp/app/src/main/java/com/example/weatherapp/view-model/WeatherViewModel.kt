package com.example.weatherapp.`view-model`

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.entity.Data
import com.example.weatherapp.entity.Weather
import com.example.weatherapp.repository.WeatherRepo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val API_KEY = "cd6733212b58fc4b2fd1a255c17d09c6"

    private val _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location>
        get() = _location
    fun setLocation(location: Location) {
        _location.postValue(location)
    }

    private val _weather: MutableLiveData<Data> = MutableLiveData()
    val weather: LiveData<Data>
        get() = _weather
    fun setWeather(city: String) {
        viewModelScope.launch {
            _weather.value = WeatherRepo.getWeather(city, API_KEY)
        }
    }
    fun setWeather(lat: String, lon: String) {
        viewModelScope.launch {
            _weather.value = WeatherRepo.getWeather(lat, lon, API_KEY)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTime(epoc: Long): String? {
        return try {
            val sdf = SimpleDateFormat("H:mm")
            val netDate = Date(epoc*1000)
            sdf.format(netDate).toString()
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun calcTemp(t: Double) = (t - 273.15).roundToInt()

    var icon: Int? = null
    var desc: String? = null
    @SuppressLint("UseCompatLoadingForDrawables")
    fun fetchWeather(weather: Weather, activity: FragmentActivity) {
        when(weather.icon) {
            "01d" -> {
                icon = R.drawable.ic_day_clear
                desc = activity.getString(R.string.clear_sky)
            }
            "01n" -> {
                icon = R.drawable.ic_night_clear
                desc = activity.getString(R.string.clear_sky)
            }

            "02d" -> {
                icon = R.drawable.ic_day_partial_cloud
                desc = activity.getString(R.string.few_clouds)
            }
            "02n" -> {
                icon = R.drawable.ic_night_partial_cloud
                desc = activity.getString(R.string.few_clouds)
            }

            "03d","03n" -> {
                icon = R.drawable.ic_cloudy
                desc = activity.getString(R.string.scattered_clouds)
            }
            "04d","04n" -> {
                icon = R.drawable.ic_overcast
                desc = activity.getString(R.string.broken_clouds)
            }
            "09d","09n" -> {
                icon = R.drawable.ic_rain
                desc = activity.getString(R.string.shower_rain)
            }

            "10d" -> {
                icon = R.drawable.ic_day_rain
                desc = activity.getString(R.string.rain)
            }
            "10n" -> {
                icon = R.drawable.ic_night_rain
                desc = activity.getString(R.string.rain)
            }

            "11d","11n" -> {
                icon = R.drawable.ic_rain_thunder
                desc = activity.getString(R.string.thunderstorm)
            }
            "13d","13n" -> {
                icon = R.drawable.ic_snow
                desc = activity.getString(R.string.snow)
            }
            "50d","50n" -> {
                icon = R.drawable.ic_mist
                desc = activity.getString(R.string.mist)
            }
        }
    }
}