package com.example.atrealtimewidget

// What the screen can be showing. Task 10 will add an error state here.
sealed interface DepartureUiState {
    data object Loading : DepartureUiState
    data object NoDepartures : DepartureUiState
    data class Loaded(val board: DepartureBoard) : DepartureUiState
}

fun toUiState(board: DepartureBoard?): DepartureUiState =
    if (board == null || board.rows.isEmpty()) {
        DepartureUiState.NoDepartures
    } else {
        DepartureUiState.Loaded(board)
    }
