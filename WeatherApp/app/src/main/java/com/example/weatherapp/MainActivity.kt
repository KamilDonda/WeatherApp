package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.`view-model`.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

lateinit var pager: ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager

    private var canSetLocation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pager = findViewById(R.id.viewPager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        pager.adapter = pagerAdapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        getLastKnownLocation()
    }

    // Pobieranie ostatniej znanej lokalizacji
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if ((ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED)
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    2
                )
            }
            return
        }
        // Nasłuchiwanie lokalizacji, gdy nie jest nullem, jest przypisywana do zmiennej
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null && canSetLocation) {
                    viewModel.setLocation(location)
                    canSetLocation = false
                }
            }
    }

    // Sprawdzanie, czy urządzenie jest połączone z internetem
    fun verifyAvailableNetwork(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Wyświetlenie dialogu z przyznaniem uprawnień do używania GPS-a
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(root, getString(R.string.permission_granted), Snackbar.LENGTH_SHORT)
                    .show()
                getLastKnownLocation()
            } else {
                Snackbar.make(root, getString(R.string.permission_denied), Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Nawigacja pomiędzy fragmentami za pomocą ViewPager2
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) MainFragment()
            else SeniorFragment()
        }
    }
}