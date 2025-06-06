package com.example.locationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import java.util.Locale
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

// This class checks that whether the user allowing the app to use the location or denying to use it
class LocationUtils(val context: Context) {

    private val _fusedLocationClient : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    // _fusedLocationClient is a private val instance that helps to get the current location
    // FusedLocationProviderClient is gGoogle's recommended way to get the location
    //LocationServices.getFusedLocationProviderClient(context) -> this is a built in function in Google API

    @SuppressLint("MissingPermission")           // ignore the warning about missing location permission
    fun requestLocationUpdates(viewModel: LocationViewModel) {
        val locationcallback = object : LocationCallback() {
            override fun onLocationResult(locationresult: LocationResult) {
                super.onLocationResult(locationresult)
                locationresult.lastLocation?.let {
                    // If the location isn't null, it creates a LocationData object with the current latitude and longitude.
                    // Then it calls your viewModel.updateLocation() function to store the latest location.
                    val location = LocationData( latitude = it.latitude, longitude = it.longitude)
                    viewModel.updateLocation(location)
                }
            }
            
        }

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,1000).build()

        _fusedLocationClient.requestLocationUpdates(locationRequest,locationcallback, Looper.getMainLooper())
    }

    fun hasLocationPermission(context1: Context): Boolean
    {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                // FINE_LOCATION = more accurate, uses GPS
                // User wants to access fine location
                &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        // COARSE_LOCATION = less accurate,uses Wi-Fi or mobile tower
        // user want to access coarse location
    }


    fun reversegeocoderlocation( location: LocationData) : String {  // this function takes a location data(latitude and longitude) and returns a string
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinate = LatLng(location.latitude, location.longitude)  // coordinate object is created from the location data
        val addresses : MutableList<Address>? = geocoder.getFromLocation(coordinate.latitude , coordinate.longitude, 1)
        //getFromLocation -> this gets the real world address from latitude and longitude, 1 means "give me the only best one match"
        return if (addresses?.isNotEmpty() == true) {
            addresses[0].getAddressLine(0)
        }else {
            "Address not found"
        }
    }
}

// Permission is granted by the user to use both the fine and coarse location to allow the location simultaneously