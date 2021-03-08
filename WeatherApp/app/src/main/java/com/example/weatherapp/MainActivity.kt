package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.api.WeatherAPI
import com.example.weatherapp.entity.Data
import com.example.weatherapp.entity.Weather
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val CITY = "Sosnowiec,pl"
    private val API_KEY = "cd6733212b58fc4b2fd1a255c17d09c6"

    private var icon: Int? = null
    private var desc: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWeather()
    }

    private fun getWeather() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherAPI::class.java)

        val call = service.getWeather(CITY, API_KEY)

        call.enqueue(object : Callback<Data> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                fetchData(response)
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Snackbar.make(root, t.message.toString(), Snackbar.LENGTH_SHORT)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun fetchData(response: Response<Data>) {
        val resp = response.body()!!

        fetchWeather(resp.weather.first())

        val temp = resp.main.temp
        val press = resp.main.pressure
        val sunrise = resp.sys.sunrise
        val sunset = resp.sys.sunset

        Log.v("XD", "$desc\n$temp\n$pressure\n$sunrise\n$sunset")

        val t = (temp - 273.15).roundToInt()
        temperature.text = "$t°C"
        pressure.text = "$press hPa"
        description.text = desc
        city.setQuery(CITY, false)
        main_icon.setImageResource(icon!!)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun fetchWeather(weather: Weather) {
        when(weather.icon) {
            "01d" -> {
                icon = R.drawable.ic_day_clear
                desc = getString(R.string.clear_sky)
            }
            "01n" -> {
                icon = R.drawable.ic_night_clear
                desc = getString(R.string.clear_sky)
            }

            "02d" -> {
                icon = R.drawable.ic_day_partial_cloud
                desc = getString(R.string.few_clouds)
            }
            "02n" -> {
                icon = R.drawable.ic_night_partial_cloud
                desc = getString(R.string.few_clouds)
            }

            "03d","03n" -> {
                icon = R.drawable.ic_cloudy
                desc = getString(R.string.scattered_clouds)
            }
            "04d","04n" -> {
                icon = R.drawable.ic_overcast
                desc = getString(R.string.broken_clouds)
            }
            "09d","09n" -> {
                icon = R.drawable.ic_rain
                desc = getString(R.string.shower_rain)
            }

            "10d" -> {
                icon = R.drawable.ic_day_rain
                desc = getString(R.string.rain)
            }
            "10n" -> {
                icon = R.drawable.ic_night_rain
                desc = getString(R.string.rain)
            }

            "11d","11n" -> {
                icon = R.drawable.ic_rain_thunder
                desc = getString(R.string.thunderstorm)
            }
            "13d","13n" -> {
                icon = R.drawable.ic_snow
                desc = getString(R.string.snow)
            }
            "50d","50n" -> {
                icon = R.drawable.ic_mist
                desc = getString(R.string.mist)
            }
        }
    }
}