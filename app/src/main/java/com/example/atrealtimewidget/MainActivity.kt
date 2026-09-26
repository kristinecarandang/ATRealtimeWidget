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
import java.net.URL
import android.util.Log
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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