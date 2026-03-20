package com.example.smartemergencyalert
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.location.LocationListener
import android.location.Location
import android.os.Looper



class LocationHelper(private val context:Context) {
    fun getLocation(callback: (String) -> Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            callback("Permission not granted")
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            0L,
            0f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val locationLink = "https://maps.google.com/?q=$latitude,$longitude"
                    callback(locationLink)
                    locationManager.removeUpdates(this)
                }

                override fun onProviderDisabled(provider: String) {
                    callback("GPS is disabled")

                }

            },
            Looper.getMainLooper()
        )
    }
}
