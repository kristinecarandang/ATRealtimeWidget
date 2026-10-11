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

// The stop being tracked. Later this will come from the UI instead.
private const val DEFAULT_STOP_ID = "605-b9605c8e"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            loadDepartures(DEFAULT_STOP_ID)
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

private suspend fun loadDepartures(stopId: String) {
    var step = "station lookup"
    try {
        val stop = AtApiClient.service.getStop(
            stopId = stopId,
            apiKey = BuildConfig.AT_API_KEY
        )
        Log.d("AtApi", "Parsed stop: $stop")

        step = "stop trips"
        val query = serviceQueryFor(java.util.Calendar.getInstance())
        val trips = AtApiClient.service.getStopTrips(
            stopId = stopId,
            date = query.date,
            startHour = query.startHour,
            hourRange = 2,
            apiKey = BuildConfig.AT_API_KEY
        )
        val next = nextDepartures(trips.data, 5)

        step = "live trip updates"
        val live = if (next.isEmpty()) emptyMap() else liveStatusByTrip(
            AtApiClient.service.getTripUpdates(
                tripIds = next.joinToString(",") { it.tripId },
                apiKey = BuildConfig.AT_API_KEY
            )
        )
        next.forEach {
            Log.d("AtApi", "Departs ${it.departureTime} to ${it.stopHeadsign}: ${describe(live[it.tripId])}")
        }
    } catch (e: Exception) {
        Log.e("AtApi", "Failed during $step: ${e.message}")
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
