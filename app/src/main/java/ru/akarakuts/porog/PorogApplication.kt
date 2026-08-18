package ru.akarakuts.porog

import android.content.Context
import ru.akarakuts.porog.data.LeaveBriefRepository
import ru.akarakuts.porog.data.calendar.CalendarRepository
import ru.akarakuts.porog.data.checklist.ChecklistRepository
import ru.akarakuts.porog.data.local.SettingsStore
import ru.akarakuts.porog.data.location.LocationRepository
import ru.akarakuts.porog.data.parking.ParkingRepository
import ru.akarakuts.porog.data.weather.WeatherRepository
import ru.akarakuts.porog.notify.LeaveScheduler

class AppContainer(context: Context) {
    private val app = context.applicationContext
    val settings = SettingsStore(app)
    val checklist = ChecklistRepository(app, settings)
    val parking = ParkingRepository(settings)
    val weather = WeatherRepository()
    val calendar = CalendarRepository(app)
    val location = LocationRepository(app)
    val leaveBrief = LeaveBriefRepository(
        context = app,
        settings = settings,
        checklist = checklist,
        parking = parking,
        weather = weather,
        calendar = calendar,
        location = location,
    )
    val scheduler = LeaveScheduler(app)
}

class PorogApplication : android.app.Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
