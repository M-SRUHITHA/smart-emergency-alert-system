package com.example.smartemergencyalert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.content.pm.PackageManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        if(checkSelfPermission(android.Manifest.permission.SEND_SMS)!=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS),1)
        }

        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    // Step 1: Use state to track which screen to show
    val showContacts = remember { mutableStateOf(false) }

    if (showContacts.value) {
        // Show Contact Screen
        val sampleContacts = listOf(
            Contact("John Doe", "1234567890"),
            Contact("Jane Smith", "9876543210"),
            Contact("Mom", "1112223333")
        )

        ContactScreen(contacts = sampleContacts) { contact ->
            println("Clicked: ${contact.name}")
        }
    } else {
        // Show SOS Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "SMART EMERGENCY ALERT",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(60.dp))

            // SOS Button
            Button(
                onClick = {
                    val locationHelper = LocationHelper(context)
                    locationHelper.getLocation { locationLink ->
                        if (locationLink == "Permission not granted" || locationLink == "location not found") {
                            Toast.makeText(context,locationLink,Toast.LENGTH_SHORT).show()
                        } else {
                            val smsHelper = SmsHelper(context)
                            smsHelper.sendSms("1234567890", "Helpme! My location:$locationLink")
                        }
                    }
                    showContacts.value = true
                },// switch to ContactScreen
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .width(200.dp)
                    .height(80.dp)
            ) {
                Text(
                    text = "SOS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}