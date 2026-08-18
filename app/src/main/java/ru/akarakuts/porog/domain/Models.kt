package ru.akarakuts.porog.domain

data class WeatherSnapshot(
    val temperatureC: Int,
    val weatherCode: Int,
    val hourlyHint: String,
    val adviceRes: Int,
)

data class CalendarEvent(
    val title: String,
    val startMillis: Long,
)

data class ChecklistItem(
    val id: Long,
    val title: String,
    val checked: Boolean,
    val builtIn: Boolean,
    val key: String? = null,
    val icon: String? = null,
)

data class ParkingSpot(
    val latitude: Double,
    val longitude: Double,
    val savedAtMillis: Long,
)

data class LeaveBrief(
    val weather: WeatherSnapshot?,
    val weatherError: String?,
    val nextEvent: CalendarEvent?,
    val leaveByMillis: Long?,
    val checklist: List<ChecklistItem>,
    val parking: ParkingSpot?,
    val commuteMinutes: Int,
    val notifyMinutesBefore: Int,
    val notificationsEnabled: Boolean,
    val hasLocationPermission: Boolean,
    val hasCalendarPermission: Boolean,
    val hasNotificationPermission: Boolean,
)

data class WidgetSnapshot(
    val temperatureText: String,
    val leaveByText: String,
    val eventText: String,
    val hintText: String,
)

object LeaveTimes {
    fun leaveByMillis(eventStartMillis: Long, commuteMinutes: Int): Long =
        eventStartMillis - commuteMinutes.coerceAtLeast(0) * 60_000L

    fun notifyAtMillis(leaveByMillis: Long, notifyMinutesBefore: Int): Long =
        leaveByMillis - notifyMinutesBefore.coerceAtLeast(0) * 60_000L
}
