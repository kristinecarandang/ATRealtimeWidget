package com.example.atrealtimewidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.atrealtimewidget.ui.theme.ATRealtimeWidgetTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// The stop being tracked. Later this will come from the UI instead.
private const val DEFAULT_STOP_ID = "605-b9605c8e"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ATRealtimeWidgetTheme {
                var state by remember { mutableStateOf<DepartureUiState>(DepartureUiState.Loading) }
                LaunchedEffect(Unit) {
                    val board = withContext(Dispatchers.IO) { loadDepartureBoard(DEFAULT_STOP_ID) }
                    state = toUiState(board)
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DepartureScreen(state = state, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
