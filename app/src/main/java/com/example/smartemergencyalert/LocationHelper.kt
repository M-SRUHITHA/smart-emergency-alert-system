package com.example.smartemergencyalert
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat

class LocationHelper(private val context:Context) {
    fun getLocation(callback:(String)->Unit) {
        val locationManager=context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(ActivityCompat.checkSelfPermission (context,Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED) {
            callback("Permission not granted")
            return
        }
        val location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        location?.let  {
            val latitude = it.latitude
            val longitude = it.longitude
            val locationLink = "https://maps.google.com/?q=$latitude,$longitude"
            callback(locationLink)
        } ?:run {
            callback("Location not found")



        }

    }
}
