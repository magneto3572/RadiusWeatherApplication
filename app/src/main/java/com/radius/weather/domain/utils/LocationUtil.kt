package com.radius.weather.domain.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest


class LocationUtil(
    private val activity: Activity,
    private val isLocationChangeRequired: Boolean = false
)
{
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    private lateinit var mGetLocationCallback: GetLocationCallback
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            onLocationChanged(p0.lastLocation)
        }
    }

    fun fetchLastLocation(getLocationCallback: GetLocationCallback) {
        mGetLocationCallback = getLocationCallback
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationChanged(location)
                } else {
                    createLocationRequest(mGetLocationCallback)
                }
            }
            .addOnFailureListener {
                mGetLocationCallback.onResponse(null)
                if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                   // CustomDialogUtil.openPermissionSetting(activity)
                } else {

                }
            }
    }

    @SuppressLint("MissingPermission")
    fun createLocationRequest(
        getLocationCallback: GetLocationCallback
    ) {
        mGetLocationCallback = getLocationCallback
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1 * 1000
            isWaitForAccurateLocation = true
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        task.addOnFailureListener { exception ->
            mGetLocationCallback.onResponse(null)
            if (exception is ResolvableApiException) {
                try {
                    //CustomDialogUtil.openGpsSettings(activity)  //Call open gps here
                } catch (sendEx: IntentSender.SendIntentException) {
                   // activity.toastS("Unable to get GPS Please Check Settings.")
                    // Ignore the error.
                }
            }
        }
    }

    private fun onLocationChanged(location: android.location.Location?) {
        mGetLocationCallback.onResponse(location)
        if (!isLocationChangeRequired)
            fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun interface GetLocationCallback {
        fun onResponse(location: android.location.Location?)
    }
}
