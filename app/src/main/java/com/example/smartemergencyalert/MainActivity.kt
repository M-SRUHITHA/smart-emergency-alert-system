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
import android.provider.ContactsContract

class MainActivity : ComponentActivity() {


    lateinit var shakeDetector: ShakeDetector
    var isEmergencyActive = false

    // ✅ moved inside class (IMPORTANT FIX)
    var selectedContacts = mutableListOf<String>()
    lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences("contacts_prefs", MODE_PRIVATE)

        val saved = prefs.getStringSet("numbers", mutableSetOf())
        selectedContacts = saved?.toMutableList() ?: mutableListOf()

        val sosButton = findViewById<Button>(R.id.sosButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val selectButton = findViewById<Button>(R.id.selectContactButton)

        sosButton.setOnClickListener {
            startEmergencyProcess()
        }

        cancelButton.setOnClickListener {
            cancelEmergency()
        }

        // ✅ Contact select button
        selectButton.setOnClickListener {

            if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), 3)
            } else {
                openContacts()
            }
        }

        // Shake detector
        shakeDetector = ShakeDetector(this) {
            startEmergencyProcess()
        }

        // Permissions
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

    // 🚀 Emergency process
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

    // 📞 Open contacts
    fun openContacts() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(intent, 100)
    }

    // 📥 Get selected contact
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {

            val contactUri = data?.data
            val cursor = contentResolver.query(contactUri!!, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {

                val numberIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )

                val number = cursor.getString(numberIndex)

                // ✅ ADD HERE (correct place)
                selectedContacts.add(number)

                // ✅ SAVE HERE
                val editor = prefs.edit()
                editor.putStringSet("numbers", selectedContacts.toSet())
                editor.apply()

                Toast.makeText(this, "Added: $number", Toast.LENGTH_SHORT).show()

                cursor.close()
            }
        }
    }


    // 📩 Send alert
    fun sendAlert() {
        Toast.makeText(this, "Fetching location...", Toast.LENGTH_SHORT).show()
        LocationHelper.getLocation(this) { locationLink ->

            val message = "Emergency! I need help.\nMy location:\n$locationLink"

            val smsManager = SmsManager.getDefault()


            // ✅ use selected contacts
            for (number in selectedContacts) {
                smsManager.sendTextMessage(number, null, message, null, null)
            }

            Toast.makeText(this, "Alert Sent!", Toast.LENGTH_LONG).show()
            isEmergencyActive = false
        }
    }
}