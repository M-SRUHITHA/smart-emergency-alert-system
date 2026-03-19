package com.example.smartemergencyalert

import android.os.Bundle
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import android.widget.Toast
import android.telephony.SmsManager
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.content.Intent
import android.net.Uri


class MainActivity : ComponentActivity() {

    lateinit var shakeDetector: ShakeDetector
    var isEmergencyActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sosButton = findViewById<Button>(R.id.sosButton)


        sosButton.setOnClickListener {
            startEmergencyProcess()
        }
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        cancelButton.setOnClickListener {
            cancelEmergency()
        }


        // Initialize shake detector
        shakeDetector = ShakeDetector(this) {
            startEmergencyProcess()
        }
        if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS), 1)
        }

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }

    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stop()
    }

    // MAIN FUNCTION (USED EVERYWHERE)
    fun startEmergencyProcess() {
        if (isEmergencyActive) return

        isEmergencyActive = true

        Toast.makeText(this, "Emergency will start in 40 sec", Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).postDelayed({

            if (isEmergencyActive) {
                sendAlert()
            }

        }, 40000)
    }

    fun cancelEmergency() {
        isEmergencyActive = false
        Toast.makeText(this, "Emergency Cancelled", Toast.LENGTH_SHORT).show()
    }

    fun sendAlert() {

        LocationHelper.getLocation(this) { locationLink ->

            val message = "Emergency! I need help.\nMy location:\n$locationLink"
            val phoneNumber = "YOUR_NUMBER"

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("sms:$phoneNumber")
            intent.putExtra("sms_body", message)

            startActivity(intent)
        }
    }
}
