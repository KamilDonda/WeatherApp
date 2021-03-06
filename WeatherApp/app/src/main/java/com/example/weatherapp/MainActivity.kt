package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.api.WeatherAPI
import com.example.weatherapp.entity.Data
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val CITY = "Gliwice,pl"
    private val API_KEY = "cd6733212b58fc4b2fd1a255c17d09c6"

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
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                fetchData(response)
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Snackbar.make(root, t.message.toString(), Snackbar.LENGTH_SHORT)
            }
        })
    }

    private fun fetchData(response: Response<Data>) {
        val resp = response.body()!!

        val desc = resp.weather.first().description
        val temp = resp.main.temp
        val pressure = resp.main.pressure
        val sunrise = resp.sys.sunrise
        val sunset = resp.sys.sunset

        Log.v("XD", "$desc\n$temp\n$pressure\n$sunrise\n$sunset")
    }
}