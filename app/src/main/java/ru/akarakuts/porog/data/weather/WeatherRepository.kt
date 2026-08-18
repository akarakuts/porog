package ru.akarakuts.porog.data.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.akarakuts.porog.R
import ru.akarakuts.porog.domain.WeatherSnapshot
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

class WeatherRepository {
    suspend fun fetch(lat: Double, lon: Double): WeatherSnapshot = withContext(Dispatchers.IO) {
        val url = URL(
            "https://api.open-meteo.com/v1/forecast" +
                "?latitude=$lat&longitude=$lon" +
                "&current=temperature_2m,weather_code,precipitation" +
                "&hourly=temperature_2m,precipitation_probability,weather_code" +
                "&forecast_hours=3&timezone=auto",
        )
        val body = url.openConnection().let { conn ->
            (conn as HttpURLConnection).apply {
                connectTimeout = 8_000
                readTimeout = 8_000
                requestMethod = "GET"
            }
            conn.inputStream.bufferedReader().use { it.readText() }
        }
        parse(body)
    }

    internal fun parse(json: String): WeatherSnapshot {
        val root = JSONObject(json)
        val current = root.getJSONObject("current")
        val temp = current.getDouble("temperature_2m").roundToInt()
        val code = current.getInt("weather_code")
        val hourly = root.optJSONObject("hourly")
        val temps = hourly?.optJSONArray("temperature_2m")
        val hint = buildString {
            if (temps != null) {
                val parts = (0 until minOf(3, temps.length())).map { i ->
                    "${temps.getDouble(i).roundToInt()}°"
                }
                append(parts.joinToString(" · "))
            }
        }
        var maxCode = code
        hourly?.optJSONArray("weather_code")?.let { codes ->
            for (i in 0 until minOf(3, codes.length())) {
                maxCode = maxOf(maxCode, codes.getInt(i))
            }
        }
        return WeatherSnapshot(
            temperatureC = temp,
            weatherCode = code,
            hourlyHint = hint,
            adviceRes = adviceFor(temp, maxCode),
        )
    }

    companion object {
        fun adviceFor(tempC: Int, weatherCode: Int): Int = when {
            weatherCode in 71..77 || weatherCode in 85..86 -> R.string.weather_need_snow
            weatherCode in 51..67 || weatherCode in 80..82 || weatherCode in 95..99 ->
                R.string.weather_need_umbrella
            tempC <= 0 -> R.string.weather_need_coat
            tempC <= 10 -> R.string.weather_need_jacket
            else -> R.string.weather_clear
        }
    }
}
