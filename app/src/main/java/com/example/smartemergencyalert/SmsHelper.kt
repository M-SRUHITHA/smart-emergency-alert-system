package com.example.smartemergencyalert
import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast

class SmsHelper(private val context:Context) {
    fun sendSms(phone: String, message: String) {
        try {
            val smsManager =SmsManager.getDefault()
            smsManager.sendTextMessage(
                phone,
                null,
                message,
                null,
                null
            )
            Toast.makeText(context, "SMS Sent!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "SMS Failed!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}


