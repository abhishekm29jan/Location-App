package com.example.locationapp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private val _location = mutableStateOf<LocationData?>(null) // if Locationdata is there then return it or return null
    val location : State<LocationData?> = _location

    fun updateLocation (newLocation : LocationData) {    // this function updates the location with the newLocation as an input
        _location.value = newLocation                    // the current location is updated with the new location

    }
}