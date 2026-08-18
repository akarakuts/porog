package ru.akarakuts.porog.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_items ORDER BY sortOrder ASC, id ASC")
    fun observeAll(): Flow<List<ChecklistEntity>>

    @Query("SELECT * FROM checklist_items ORDER BY sortOrder ASC, id ASC")
    suspend fun getAll(): List<ChecklistEntity>

    @Query("SELECT COUNT(*) FROM checklist_items")
    suspend fun count(): Int

    @Insert
    suspend fun insert(item: ChecklistEntity): Long

    @Insert
    suspend fun insertAll(items: List<ChecklistEntity>)

    @Query("UPDATE checklist_items SET checked = :checked WHERE id = :id")
    suspend fun setChecked(id: Long, checked: Boolean)

    @Query("UPDATE checklist_items SET checked = 0")
    suspend fun uncheckAll()

    @Query("DELETE FROM checklist_items WHERE id = :id AND customLabel IS NOT NULL")
    suspend fun deleteCustom(id: Long)
}

@Dao
interface ParkingDao {
    @Query("SELECT * FROM parking_spots WHERE id = 1")
    fun observe(): Flow<ParkingEntity?>

    @Query("SELECT * FROM parking_spots WHERE id = 1")
    suspend fun get(): ParkingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(spot: ParkingEntity)
}
