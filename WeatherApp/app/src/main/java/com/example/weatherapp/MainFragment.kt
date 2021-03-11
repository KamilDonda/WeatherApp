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
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

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
                    viewModel.getWeather(query)
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

        viewModel.fetchWeather(resp.weather.first(), requireActivity())

        val temp = resp.main.temp
        val tempMax = resp.main.temp_max
        val tempMin = resp.main.temp_min
        val press = resp.main.pressure
        val sunriseTime = resp.sys.sunrise
        val sunsetTime = resp.sys.sunset

        temperature.text = "${viewModel.calcTemp(temp)}°C"
        temperature_max.text = "${viewModel.calcTemp(tempMax)}°C"
        temperature_min.text = "${viewModel.calcTemp(tempMin)}°C"
        pressure.text = "$press hPa"
        description.text = viewModel.desc
        main_icon.setImageResource(viewModel.icon!!)

        sunrise.text = viewModel.convertTime(sunriseTime)
        sunset.text = viewModel.convertTime(sunsetTime)
    }
}