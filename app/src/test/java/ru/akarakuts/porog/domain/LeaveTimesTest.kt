package ru.akarakuts.porog.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class LeaveTimesTest {
    @Test
    fun leaveBySubtractsCommute() {
        val start = 1_700_000_000_000L
        val result = LeaveTimes.leaveByMillis(start, 30)
        assertEquals(start - 30 * 60_000L, result)
    }

    @Test
    fun notifyAtSubtractsLeadTime() {
        val leave = 1_700_000_000_000L
        val result = LeaveTimes.notifyAtMillis(leave, 10)
        assertEquals(leave - 10 * 60_000L, result)
    }
}
