package com.radius.weather.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.radius.weather.presentation.theme.RadiusWeatherTheme
import com.radius.weather.presentation.ui.WeatherDetailsMain
import com.radius.weather.presentation.ui.weatherSearch
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        var location : Location? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAskPermission()
        setContent {
            RadiusWeatherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    setupNavController()
                }
            }
        }

    }

    private fun setupAskPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        }else{
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.containsValue(false)){
            Toast.makeText(this, "Permission Denied ", Toast.LENGTH_SHORT).show()
        }
        else{
           getLocation()
        }
    }


    private fun getLocation(){
        val LOCATION_REFRESH_TIME = 15000L // 15 seconds to update
        val LOCATION_REFRESH_DISTANCE = 500F // 500 meters to update

        val mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mLocationManager.requestLocationUpdates(
            LocationManager.FUSED_PROVIDER, LOCATION_REFRESH_TIME,
            LOCATION_REFRESH_DISTANCE
        ) { loc ->
            location = loc
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RadiusWeatherTheme {
        setupNavController()
    }
}

@Composable
private fun setupNavController(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        //Weather Search Screen Route
        composable("home") {
            weatherSearch(navController)
        }

        //Weather Detail Screen Route
        composable(route = "details/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                },
            )
        ) {
            val name = it.arguments?.getString("name")!!
            WeatherDetailsMain(name, "", navController)
        }
        composable(route = "details/{latLng}",
            arguments = listOf(
                navArgument("latLng") {
                    type = NavType.StringType
                },
            )
        ) {
            val latLng = it.arguments?.getString("LatLng")!!
            WeatherDetailsMain("", latLng,  navController)
        }
    }
}
