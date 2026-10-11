package com.example.atrealtimewidget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.atrealtimewidget.ui.theme.ATRealtimeWidgetTheme

// What the screen can be showing. Task 10 will add an error state here.


@Composable
fun DepartureScreen(state: DepartureUiState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(all = 16.dp)) {
        when (state) {
            DepartureUiState.Loading -> Text(text = "Loading...")
            DepartureUiState.NoDepartures -> Text(text = "No departures to show")
            is DepartureUiState.Loaded -> DepartureList(state.board)
        }
    }
}

@Composable
private fun DepartureList(board: DepartureBoard) {
    Text(text = board.stopName, style = MaterialTheme.typography.titleLarge)
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        board.rows.forEach { row ->
            Text(text = "${row.time}   ${row.destination}   ${row.status}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DepartureScreenPreview() {
    ATRealtimeWidgetTheme {
        DepartureScreen(
            state = DepartureUiState.Loaded(
                DepartureBoard(
                    stopName = "Onehunga Train Station",
                    rows = listOf(
                        DepartureRow("14:37", "Henderson via Grafton", "2 min late"),
                        DepartureRow("14:52", "Henderson via Grafton", "on time"),
                        DepartureRow("15:07", "Henderson via Grafton", "scheduled (no live data)")
                    )
                )
            )
        )
    }
}
