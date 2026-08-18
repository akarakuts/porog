package ru.akarakuts.porog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChecklistEntity::class, ParkingEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class PorogDatabase : RoomDatabase() {
    abstract fun checklistDao(): ChecklistDao
    abstract fun parkingDao(): ParkingDao
}
