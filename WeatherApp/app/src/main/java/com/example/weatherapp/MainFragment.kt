package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.`view-model`.WeatherViewModel
import com.example.weatherapp.entity.Data
import com.example.weatherapp.entity.Weather
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainFragment : Fragment() {

    private val API_KEY = "cd6733212b58fc4b2fd1a255c17d09c6"

    private var icon: Int? = null
    private var desc: String? = null

    private lateinit var viewModel: WeatherViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)

        viewModel.weather.observe(viewLifecycleOwner, Observer {
            fetchData(viewModel.weather)
        })

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        city.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.getWeather(query, API_KEY)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if(!query.isNullOrBlank())
                    if(!query[0].isUpperCase())
                        city.setQuery(query.capitalize(), false)
                return false
            }
        })

        senior.setOnClickListener {
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun fetchData(response: LiveData<Data>) {
        val resp = response.value!!

        fetchWeather(resp.weather.first())

        val temp = resp.main.temp
        val tempMax = resp.main.temp_max
        val tempMin = resp.main.temp_min
        val press = resp.main.pressure
        val sunriseTime = resp.sys.sunrise
        val sunsetTime = resp.sys.sunset

        temperature.text = "${calcTemp(temp)}°C"
        temperature_max.text = "${calcTemp(tempMax)}°C"
        temperature_min.text = "${calcTemp(tempMin)}°C"
        pressure.text = "$press hPa"
        description.text = desc
        main_icon.setImageResource(icon!!)

        sunrise.text = convertTime(sunriseTime)
        sunset.text = convertTime(sunsetTime)
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

    @SuppressLint("SimpleDateFormat")
    private fun convertTime(epoc: Long): String? {
        return try {
            val sdf = SimpleDateFormat("H:mm")
            val netDate = Date(epoc*1000)
            sdf.format(netDate).toString()
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun calcTemp(t: Double) = (t - 273.15).roundToInt()
}