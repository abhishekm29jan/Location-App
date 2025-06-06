package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationAppTheme {
                // Surface: like a white canvas to draw on
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(viewModel)
                }
            }
        }
    }
}


@Composable
fun MyApp(viewModel: LocationViewModel) {
    val context = LocalContext.current      // gets the current app context
    val locationUtils = LocationUtils(context)
    LocationDisplay(locationUtils = locationUtils, viewModel = viewModel , context = context)   // shows the UI and handles the logic
}

@Composable
fun LocationDisplay(
    locationUtils: LocationUtils,    // shows whether the user already gave the permission
    context: Context,
    viewModel: LocationViewModel
) {

    val location = viewModel.location.value

    val address = location?.let {
        locationUtils.reversegeocoderlocation(location)
    }

    val RequestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // handles the result of user input regarding permission(yes/no)
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                // user granted us to access his location
                locationUtils.requestLocationUpdates(viewModel = viewModel)
            }
            else {
                // if the user not granted us then ask him for permission
                val rationalrequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if (rationalrequired) {
                    Toast.makeText(
                        context, "Location permission is required for this feature",
                        Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(
                        context, "Please enable the permission in your Android Settings",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if (location != null) {
            Text("Address : ${location.latitude} , ${location.longitude} \n ${address}")
        } else {
            Text(text = "Location not found")
        }

        Button(
            onClick = {if (locationUtils.hasLocationPermission(context)) {
                // Permission already granted by the user and modify the location
                locationUtils.requestLocationUpdates(viewModel)
            }
            else {
                // Request the User to permit the location
                RequestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            })
        {
            Text(text = "Get location")
        }
    }
}
