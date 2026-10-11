package com.example.atrealtimewidget

import org.junit.Assert.assertEquals
import org.junit.Test

class LiveStatusTest {
    @Test
    fun noLiveData_saysScheduled() {
        assertEquals("scheduled (no live data)", describe(null))
    }

    @Test
    fun cancelled_saysCancelled() {
        assertEquals("CANCELLED", describe(LiveStatus(cancelled = true, delaySeconds = null)))
    }

    @Test
    fun underOneMinuteLate_saysOnTime() {
        assertEquals("on time", describe(LiveStatus(cancelled = false, delaySeconds = 59)))
    }

    @Test
    fun exactlyOneMinuteLate_saysOneMinLate() {
        assertEquals("1 min late", describe(LiveStatus(cancelled = false, delaySeconds = 60)))
    }

    @Test
    fun twoAndABitMinutesLate_roundsDown() {
        assertEquals("2 min late", describe(LiveStatus(cancelled = false, delaySeconds = 125)))
    }

    @Test
    fun exactlyOneMinuteEarly_saysOneMinEarly() {
        assertEquals("1 min early", describe(LiveStatus(cancelled = false, delaySeconds = -60)))
    }
}
