/** PorogWidget — температура и время выхода на рабочем столе, в цветах двери. */
package ru.akarakuts.porog.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider as UnitColorProvider
import ru.akarakuts.porog.MainActivity
import ru.akarakuts.porog.PorogApplication
import ru.akarakuts.porog.R

private val Teal = Color(0xFF0E3B3B)
private val Cream = Color(0xFFF4EDE3)
private val Amber = Color(0xFFE8B84A)
private val CreamDim = Color(0xFFD4C9BB)

class PorogWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snap = (context.applicationContext as? PorogApplication)
            ?.container?.settings?.widgetSnapshot()
        provideContent {
            WidgetContent(
                context = context,
                temp = snap?.temperatureText.orEmpty(),
                leave = snap?.leaveByText.orEmpty(),
                event = snap?.eventText.orEmpty(),
                hint = snap?.hintText.orEmpty(),
            )
        }
    }

    companion object {
        suspend fun updateAll(context: Context) {
            PorogWidget().updateAll(context)
        }
    }
}

@Composable
private fun WidgetContent(
    context: Context,
    temp: String,
    leave: String,
    event: String,
    hint: String,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(24.dp)
            .background(ColorProvider(day = Teal, night = Teal))
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        verticalAlignment = Alignment.Vertical.CenterVertically,
    ) {
        Text(
            text = context.getString(R.string.app_name),
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = UnitColorProvider(CreamDim),
            ),
        )
        if (temp.isNotBlank()) {
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = temp,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitColorProvider(Cream),
                ),
            )
        }
        if (leave.isNotBlank()) {
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = leave,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = UnitColorProvider(Amber),
                ),
            )
        }
        if (event.isNotBlank()) {
            Text(
                text = event,
                style = TextStyle(fontSize = 13.sp, color = UnitColorProvider(CreamDim)),
                maxLines = 1,
            )
        }
        if (hint.isNotBlank()) {
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = hint,
                style = TextStyle(fontSize = 13.sp, color = UnitColorProvider(Cream)),
                maxLines = 2,
            )
        }
    }
}
