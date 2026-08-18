package ru.akarakuts.porog.notify

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.akarakuts.porog.MainActivity
import ru.akarakuts.porog.PorogApplication
import ru.akarakuts.porog.R
import ru.akarakuts.porog.domain.LeaveTimes

class LeaveScheduler(private val context: Context) {
    fun ensureChannel() {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notify_channel_name),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = context.getString(R.string.notify_channel_desc)
            },
        )
    }

    fun schedule(leaveByMillis: Long?, notifyMinutesBefore: Int, enabled: Boolean) {
        ensureChannel()
        val am = context.getSystemService(AlarmManager::class.java)
        val pi = pending()
        am.cancel(pi)
        if (!enabled || leaveByMillis == null) return
        val whenAt = LeaveTimes.notifyAtMillis(leaveByMillis, notifyMinutesBefore)
        if (whenAt <= System.currentTimeMillis() + 15_000) return
        if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenAt, pi)
        } else if (Build.VERSION.SDK_INT >= 23) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenAt, pi)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, whenAt, pi)
        }
    }

    private fun pending(): PendingIntent {
        val intent = Intent(context, LeaveAlarmReceiver::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, 1, intent, flags)
    }

    companion object {
        const val CHANNEL_ID = "porog_leave"
    }
}

class LeaveAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val nm = NotificationManagerCompat.from(context)
        if (!nm.areNotificationsEnabled()) return
        val open = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val n = NotificationCompat.Builder(context, LeaveScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notify)
            .setContentTitle(context.getString(R.string.notify_leave_title))
            .setContentText(context.getString(R.string.notify_leave_plain))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        try {
            nm.notify(1001, n)
        } catch (_: SecurityException) {
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val app = context.applicationContext as? PorogApplication ?: return
        val pending = goAsync()
        Thread {
            try {
                kotlinx.coroutines.runBlocking {
                    val brief = app.container.leaveBrief.load()
                    app.container.scheduler.schedule(
                        brief.leaveByMillis,
                        brief.notifyMinutesBefore,
                        brief.notificationsEnabled && brief.hasNotificationPermission,
                    )
                }
            } finally {
                pending.finish()
            }
        }.start()
    }
}
