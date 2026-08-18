package ru.akarakuts.porog.data

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import ru.akarakuts.porog.R
import ru.akarakuts.porog.data.calendar.CalendarRepository
import ru.akarakuts.porog.data.checklist.ChecklistRepository
import ru.akarakuts.porog.data.local.SettingsStore
import ru.akarakuts.porog.data.location.LocationRepository
import ru.akarakuts.porog.data.parking.ParkingRepository
import ru.akarakuts.porog.data.weather.WeatherRepository
import ru.akarakuts.porog.domain.LeaveBrief
import ru.akarakuts.porog.domain.LeaveTimes
import ru.akarakuts.porog.domain.WidgetSnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LeaveBriefRepository(
    private val context: Context,
    private val settings: SettingsStore,
    private val checklist: ChecklistRepository,
    private val parking: ParkingRepository,
    private val weather: WeatherRepository,
    private val calendar: CalendarRepository,
    private val location: LocationRepository,
) {
    suspend fun load(): LeaveBrief {
        val cfg = settings.current()
        val locPerm = location.hasPermission()
        val calPerm = calendar.hasPermission()
        val notifyPerm = NotificationManagerCompat.from(context).areNotificationsEnabled()
        var weatherError: String? = null
        val weatherNow = if (locPerm) {
            try {
                val point = location.current()
                if (point == null) {
                    weatherError = context.getString(R.string.weather_no_location)
                    null
                } else {
                    weather.fetch(point.latitude, point.longitude)
                }
            } catch (_: Exception) {
                weatherError = context.getString(R.string.weather_error)
                null
            }
        } else {
            weatherError = context.getString(R.string.perm_location_rationale)
            null
        }
        val event = if (calPerm) calendar.nextEvent() else null
        val leaveBy = event?.let { LeaveTimes.leaveByMillis(it.startMillis, cfg.commuteMinutes) }
        return LeaveBrief(
            weather = weatherNow,
            weatherError = weatherError,
            nextEvent = event,
            leaveByMillis = leaveBy,
            checklist = checklist.items(),
            parking = parking.latest(),
            commuteMinutes = cfg.commuteMinutes,
            notifyMinutesBefore = cfg.notifyMinutesBefore,
            notificationsEnabled = cfg.notificationsEnabled,
            hasLocationPermission = locPerm,
            hasCalendarPermission = calPerm,
            hasNotificationPermission = notifyPerm,
        )
    }

    suspend fun snapshotOf(brief: LeaveBrief): WidgetSnapshot {
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        val temp = brief.weather?.let {
            context.getString(R.string.widget_temp, it.temperatureC)
        }.orEmpty()
        val leave = brief.leaveByMillis?.let {
            context.getString(R.string.widget_leave, timeFmt.format(Date(it)))
        }.orEmpty()
        val event = brief.nextEvent?.title.orEmpty()
        val hint = brief.weather?.let { context.getString(it.adviceRes) }.orEmpty()
        return WidgetSnapshot(temp, leave, event, hint)
    }
}
