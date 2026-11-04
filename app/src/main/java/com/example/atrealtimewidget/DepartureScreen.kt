package com.example.atrealtimewidget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = departureText(row),
                    modifier = Modifier.weight(1f)
                )
                // Fixed width, even when empty, so the platforms line up.
                Text(
                    text = platformLabel(row.platform),
                    modifier = Modifier.width(56.dp),
                    textAlign = TextAlign.End
                )
            }
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
                        DepartureRow("14:37", "Henderson via Grafton", "2 min late", "1"),
                        DepartureRow("14:52", "Henderson via Grafton", "on time", "1"),
                        DepartureRow("15:07", "Henderson via Grafton", "scheduled (no live data)")
                    )
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DepartureScreenRoutePreview() {
    ATRealtimeWidgetTheme {
        DepartureScreen(
            state = DepartureUiState.Loaded(
                DepartureBoard(
                    stopName = "Mangere College",
                    rows = listOf(
                        DepartureRow("08:14", "ONEHUNGA", "on time", route = "38"),
                        DepartureRow("08:29", "ONEHUNGA", "2 min late", route = "38"),
                        DepartureRow("08:44", "ONEHUNGA", "scheduled (no live data)", route = "38")
                    )
                )
            )
        )
    }
}
