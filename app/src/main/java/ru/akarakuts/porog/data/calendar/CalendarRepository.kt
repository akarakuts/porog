package ru.akarakuts.porog.data.calendar

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import ru.akarakuts.porog.domain.CalendarEvent

class CalendarRepository(private val context: Context) {
    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) ==
            PackageManager.PERMISSION_GRANTED

    fun nextEvent(nowMillis: Long = System.currentTimeMillis()): CalendarEvent? {
        if (!hasPermission()) return null
        val end = nowMillis + 36L * 60 * 60 * 1000
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, nowMillis)
        ContentUris.appendId(builder, end)
        val projection = arrayOf(
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
        )
        context.contentResolver.query(
            builder.build(),
            projection,
            "${CalendarContract.Instances.BEGIN} >= ? AND ${CalendarContract.Instances.ALL_DAY} = 0",
            arrayOf(nowMillis.toString()),
            "${CalendarContract.Instances.BEGIN} ASC",
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val title = cursor.getString(0).orEmpty()
                val begin = cursor.getLong(1)
                return CalendarEvent(title = title, startMillis = begin)
            }
        }
        return null
    }
}
