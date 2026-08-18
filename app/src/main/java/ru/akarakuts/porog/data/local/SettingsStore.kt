package ru.akarakuts.porog.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.akarakuts.porog.domain.WidgetSnapshot

private val Context.porogDataStore: DataStore<Preferences> by preferencesDataStore(name = "porog_prefs")

data class PorogSettings(
    val commuteMinutes: Int = 30,
    val notifyMinutesBefore: Int = 10,
    val notificationsEnabled: Boolean = true,
    val lastChecklistEpochDay: Long = -1L,
)

class SettingsStore(private val context: Context) {
    private val store = context.porogDataStore

    val settings: Flow<PorogSettings> = store.data.map { p ->
        PorogSettings(
            commuteMinutes = p[COMMUTE] ?: 30,
            notifyMinutesBefore = p[NOTIFY_BEFORE] ?: 10,
            notificationsEnabled = p[NOTIFY_ENABLED] ?: true,
            lastChecklistEpochDay = p[CHECKLIST_DAY] ?: -1L,
        )
    }

    suspend fun current(): PorogSettings = settings.first()

    suspend fun setCommuteMinutes(value: Int) {
        store.edit { it[COMMUTE] = value.coerceIn(5, 180) }
    }

    suspend fun setNotifyMinutesBefore(value: Int) {
        store.edit { it[NOTIFY_BEFORE] = value.coerceIn(0, 120) }
    }

    suspend fun setNotificationsEnabled(value: Boolean) {
        store.edit { it[NOTIFY_ENABLED] = value }
    }

    suspend fun setChecklistEpochDay(day: Long) {
        store.edit { it[CHECKLIST_DAY] = day }
    }

    suspend fun saveWidgetSnapshot(snapshot: WidgetSnapshot) {
        store.edit {
            it[W_TEMP] = snapshot.temperatureText
            it[W_LEAVE] = snapshot.leaveByText
            it[W_EVENT] = snapshot.eventText
            it[W_HINT] = snapshot.hintText
        }
    }

    suspend fun widgetSnapshot(): WidgetSnapshot {
        val p = store.data.first()
        return WidgetSnapshot(
            temperatureText = p[W_TEMP].orEmpty(),
            leaveByText = p[W_LEAVE].orEmpty(),
            eventText = p[W_EVENT].orEmpty(),
            hintText = p[W_HINT].orEmpty(),
        )
    }

    suspend fun checklistJson(): String = store.data.first()[CHECKLIST_JSON].orEmpty()

    suspend fun setChecklistJson(json: String) {
        store.edit { it[CHECKLIST_JSON] = json }
    }

    suspend fun parkingSpot(): ru.akarakuts.porog.domain.ParkingSpot? {
        val p = store.data.first()
        val lat = p[PARK_LAT] ?: return null
        val lon = p[PARK_LON] ?: return null
        val at = p[PARK_AT] ?: return null
        return ru.akarakuts.porog.domain.ParkingSpot(lat, lon, at)
    }

    suspend fun saveParking(latitude: Double, longitude: Double, atMillis: Long) {
        store.edit {
            it[PARK_LAT] = latitude
            it[PARK_LON] = longitude
            it[PARK_AT] = atMillis
        }
    }

    private companion object {
        val COMMUTE = intPreferencesKey("commute_minutes")
        val NOTIFY_BEFORE = intPreferencesKey("notify_minutes_before")
        val NOTIFY_ENABLED = booleanPreferencesKey("notifications_enabled")
        val CHECKLIST_DAY = longPreferencesKey("checklist_epoch_day")
        val CHECKLIST_JSON = stringPreferencesKey("checklist_json")
        val PARK_LAT = doublePreferencesKey("park_lat")
        val PARK_LON = doublePreferencesKey("park_lon")
        val PARK_AT = longPreferencesKey("park_at")
        val W_TEMP = stringPreferencesKey("widget_temp")
        val W_LEAVE = stringPreferencesKey("widget_leave")
        val W_EVENT = stringPreferencesKey("widget_event")
        val W_HINT = stringPreferencesKey("widget_hint")
    }
}
