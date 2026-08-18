package ru.akarakuts.porog.data.parking

import ru.akarakuts.porog.data.local.SettingsStore
import ru.akarakuts.porog.domain.ParkingSpot

class ParkingRepository(private val settings: SettingsStore) {
    suspend fun latest(): ParkingSpot? = settings.parkingSpot()

    suspend fun save(latitude: Double, longitude: Double, atMillis: Long = System.currentTimeMillis()) {
        settings.saveParking(latitude, longitude, atMillis)
    }
}
