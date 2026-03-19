import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun getLocation(context: Context, callback: (String) -> Unit) {

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude

                val link = "https://maps.google.com/?q=$lat,$lon"
                callback(link)
            } else {
                callback("Location not available")
            }
        }
    }
}