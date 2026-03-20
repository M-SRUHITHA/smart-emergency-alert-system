package com.example.smartemergencyalert

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun getLocation(context: Context, callback: (String) -> Unit) {

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // 🔹 First try last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

            if (location != null) {
                val link = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                callback(link)
            } else {
                // 🔥 If null → request fresh location
                requestNewLocation(fusedLocationClient, callback)
            }
        }.addOnFailureListener {
            requestNewLocation(fusedLocationClient, callback)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation(
        fusedLocationClient: FusedLocationProviderClient,
        callback: (String) -> Unit
    ) {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000
        ).setMaxUpdates(1).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation

                if (location != null) {
                    val link = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                    callback(link)
                } else {
                    callback("Location still not available")
                }

                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }
}