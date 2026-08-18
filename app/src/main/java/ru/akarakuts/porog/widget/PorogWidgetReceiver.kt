package ru.akarakuts.porog.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PorogWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = PorogWidget()
}

fun refreshPorogWidget(context: Context) {
    CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
        PorogWidget().updateAll(context.applicationContext)
    }
}
