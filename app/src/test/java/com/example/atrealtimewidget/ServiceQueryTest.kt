package com.example.atrealtimewidget

import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Test

class ServiceQueryTest {

    private fun at(year: Int, month: Int, day: Int, hour: Int, minute: Int = 0): Calendar =
        Calendar.getInstance().apply { clear(); set(year, month, day, hour, minute) }

    @Test
    fun justAfterMidnight_usesPreviousDayAndHour24() {
        val q = serviceQueryFor(at(2026, Calendar.OCTOBER, 4, 0, 26))
        assertEquals(ServiceQuery("2026-10-03", 24), q)
    }

    @Test
    fun threeAm_stillUsesPreviousDay() {
        val q = serviceQueryFor(at(2026, Calendar.OCTOBER, 4, 3, 59))
        assertEquals(ServiceQuery("2026-10-03", 27), q)
    }

    @Test
    fun fourAm_usesToday() {
        val q = serviceQueryFor(at(2026, Calendar.OCTOBER, 4, 4, 0))
        assertEquals(ServiceQuery("2026-10-04", 4), q)
    }

    @Test
    fun midday_usesTodayAndCurrentHour() {
        val q = serviceQueryFor(at(2026, Calendar.OCTOBER, 4, 14, 30))
        assertEquals(ServiceQuery("2026-10-04", 14), q)
    }

    @Test
    fun monthBoundary_goesBackToLastDayOfPreviousMonth() {
        val q = serviceQueryFor(at(2026, Calendar.NOVEMBER, 1, 0, 30))
        assertEquals(ServiceQuery("2026-10-31", 24), q)
    }
}