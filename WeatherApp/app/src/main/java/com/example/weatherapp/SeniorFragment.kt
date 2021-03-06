package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.`view-model`.WeatherViewModel
import com.example.weatherapp.entity.Data
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_senior.*
import kotlinx.android.synthetic.main.fragment_senior.city
import kotlinx.android.synthetic.main.fragment_senior.description
import kotlinx.android.synthetic.main.fragment_senior.loading
import kotlinx.android.synthetic.main.fragment_senior.loading_desc
import kotlinx.android.synthetic.main.fragment_senior.main_icon
import kotlinx.android.synthetic.main.fragment_senior.pressure
import kotlinx.android.synthetic.main.fragment_senior.senior
import kotlinx.android.synthetic.main.fragment_senior.sunrise
import kotlinx.android.synthetic.main.fragment_senior.sunrise_desc
import kotlinx.android.synthetic.main.fragment_senior.sunrise_icon
import kotlinx.android.synthetic.main.fragment_senior.sunset
import kotlinx.android.synthetic.main.fragment_senior.sunset_desc
import kotlinx.android.synthetic.main.fragment_senior.sunset_icon
import kotlinx.android.synthetic.main.fragment_senior.temperature
import kotlinx.android.synthetic.main.fragment_senior.temperature_max
import kotlinx.android.synthetic.main.fragment_senior.temperature_min

class SeniorFragment : Fragment() {

    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)

        return inflater.inflate(R.layout.fragment_senior, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.location.observe(viewLifecycleOwner, Observer {
            val lat = it.latitude.toString()
            val lon = it.longitude.toString()

            val isConnected = (activity as MainActivity).verifyAvailableNetwork()
            if (isConnected)
                viewModel.setWeather(lat, lon)
            else {
                Snackbar.make(
                    requireActivity().findViewById(R.id.root),
                    getString(R.string.internet_error),
                    Snackbar.LENGTH_LONG
                ).show()
                val imm: InputMethodManager =
                    requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        })

        viewModel.weather.observe(viewLifecycleOwner, Observer {
            fetchData(viewModel.weather)
            if (city.query.isNullOrEmpty())
                city.setQuery(viewModel.weather.value?.name, false)
        })

        city.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    val isConnected = (activity as MainActivity).verifyAvailableNetwork()
                    if (isConnected) {
                        viewModel.setWeather(query)
                        viewModel.setQuery(query)
                    } else {
                        Snackbar.make(
                            requireActivity().findViewById(R.id.root),
                            getString(R.string.internet_error),
                            Snackbar.LENGTH_LONG
                        ).show()
                        val imm: InputMethodManager =
                            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.isNullOrBlank())
                    if (!query[0].isUpperCase())
                        city.setQuery(query.capitalize(), false)
                return false
            }
        })

        senior.setOnClickListener {
            pager.currentItem = 0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun fetchData(response: LiveData<Data>) {
        val resp = response.value

        if (resp == null) {
            Snackbar.make(
                requireActivity().findViewById(R.id.root),
                getString(R.string.city_error),
                Snackbar.LENGTH_LONG
            ).show()
            val imm: InputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)

            city.setQuery("", false)
        } else {
            viewModel.fetchWeather(resp.weather.first(), requireActivity())

            val temp = resp.main.temp
            val tempMax = resp.main.temp_max
            val tempMin = resp.main.temp_min
            val press = resp.main.pressure
            val sunriseTime = resp.sys.sunrise
            val sunsetTime = resp.sys.sunset

            val t = viewModel.calcTemp(temp)
            temperature.text = "$t??C"
            temperature_max.text = "${viewModel.calcTemp(tempMax)}??C"
            temperature_min.text = "${viewModel.calcTemp(tempMin)}??C"
            pressure.text = "$press hPa"
            description.text = viewModel.desc
            main_icon.setImageResource(viewModel.icon!!)

            sunrise.text = viewModel.convertTime(sunriseTime)
            sunset.text = viewModel.convertTime(sunsetTime)

            city.setQuery(viewModel.query.value, false)

            loading.visibility = View.GONE
            loading_desc.visibility = View.GONE
            temperature.visibility = View.VISIBLE
            temperature_max.visibility = View.VISIBLE
            temperature_min.visibility = View.VISIBLE
            pressure.visibility = View.VISIBLE
            description.visibility = View.VISIBLE
            main_icon.visibility = View.VISIBLE
            sunrise.visibility = View.VISIBLE
            sunrise_desc.visibility = View.VISIBLE
            sunrise_icon.visibility = View.VISIBLE
            sunset.visibility = View.VISIBLE
            sunset_desc.visibility = View.VISIBLE
            sunset_icon.visibility = View.VISIBLE

            root.background = viewModel.getBackground(requireActivity(), t, true)
        }
    }
}