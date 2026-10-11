package com.example.atrealtimewidget

import org.junit.Assert.assertEquals
import org.junit.Test

class DepartureUiStateTest {
    private val row = DepartureRow("14:37", "Henderson via Grafton", "on time")

    @Test
    fun noBoard_meansNoDepartures() {
        assertEquals(DepartureUiState.NoDepartures, toUiState(null))
    }

    @Test
    fun boardWithNoRows_meansNoDepartures() {
        val board = DepartureBoard("Onehunga Train Station", emptyList())
        assertEquals(DepartureUiState.NoDepartures, toUiState(board))
    }

    @Test
    fun boardWithRows_isLoaded() {
        val board = DepartureBoard("Onehunga Train Station", listOf(row))
        assertEquals(DepartureUiState.Loaded(board), toUiState(board))
    }
}
