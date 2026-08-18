package ru.akarakuts.porog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_items")
data class ChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val labelResName: String?,
    val customLabel: String?,
    val checked: Boolean,
    val sortOrder: Int,
)

@Entity(tableName = "parking_spots")
data class ParkingEntity(
    @PrimaryKey val id: Int = 1,
    val latitude: Double,
    val longitude: Double,
    val savedAtMillis: Long,
)
