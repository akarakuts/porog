package ru.akarakuts.porog

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.akarakuts.porog.data.weather.WeatherRepository
import ru.akarakuts.porog.domain.LeaveTimes

class LeaveByTest {
    @Test
    fun leaveBySubtractsCommute() {
        val start = 1_700_000_000_000L
        assertEquals(start - 30 * 60_000L, LeaveTimes.leaveByMillis(start, 30))
    }

    @Test
    fun rainAdviceIsUmbrella() {
        assertEquals(R.string.weather_need_umbrella, WeatherRepository.adviceFor(18, 61))
    }

    @Test
    fun coldAdviceIsJacket() {
        assertEquals(R.string.weather_need_jacket, WeatherRepository.adviceFor(8, 1))
    }
}
