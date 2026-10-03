package com.example.atrealtimewidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.atrealtimewidget.ui.theme.ATRealtimeWidgetTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //605-b9605c8e
        lifecycleScope.launch (Dispatchers.IO) {
            try {
                val stop = AtApiClient.service.getStop(
                    stopId = "605-b9605c8e", // TODO: replace with a real AT stop ID
                    apiKey = BuildConfig.AT_API_KEY
                )
                Log.d("AtApi", "Parsed stop: $stop")
                val now = java.util.Calendar.getInstance()
                val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(now.time)
                val hour = now.get(java.util.Calendar.HOUR_OF_DAY)

                val trips = AtApiClient.service.getStopTrips(
                    stopId = "9503-bcf68071", // Onehunga platform, from the sample response
                    date = today,
                    startHour = hour,
                    hourRange = 2,
                    apiKey = BuildConfig.AT_API_KEY
                )

                nextDepartures(trips.data, 5).forEach {
                    Log.d("AtApi", "Departs ${it.departureTime} to ${it.stopHeadsign} (trip ${it.tripId})")
                }
            } catch (e: Exception) {
                Log.e("AtApi", "Failed: ${e.message}")
            }
        }
        enableEdgeToEdge()
        setContent {
            ATRealtimeWidgetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ATRealtimeWidgetTheme {
        Greeting("Android")
    }
}